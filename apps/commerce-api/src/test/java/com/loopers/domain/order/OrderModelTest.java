package com.loopers.domain.order;

import com.loopers.domain.product.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class OrderModelTest {

    @DisplayName("주문 생성")
    @Nested
    class Create {

        @DisplayName("회원ID로 정상 생성된다")
        @Test
        void createsSuccessfully() {
            // arrange & act
            OrderModel order = new OrderModel(1L);
            // assert
            assertAll(
                () -> assertThat(order.getMemberId()).isEqualTo(1L),
                () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED),
                () -> assertThat(order.getTotalAmount().value()).isEqualTo(0),
                () -> assertThat(order.getOrderItems()).isEmpty()
            );
        }
    }

    @DisplayName("주문 항목 추가 및 총액 계산")
    @Nested
    class AddItemAndCalculate {

        @DisplayName("항목 추가 후 총액을 계산한다")
        @Test
        void calculatesTotalAmount() {
            // arrange
            OrderModel order = new OrderModel(1L);
            OrderItemModel item1 = new OrderItemModel(1L, "에어맥스", new Money(129000), 2);
            OrderItemModel item2 = new OrderItemModel(2L, "에어포스", new Money(109000), 1);
            // act
            order.addOrderItem(item1);
            order.addOrderItem(item2);
            order.calculateTotalAmount();
            // assert
            assertAll(
                () -> assertThat(order.getOrderItems()).hasSize(2),
                () -> assertThat(order.getTotalAmount().value()).isEqualTo(129000 * 2 + 109000)
            );
        }

        @DisplayName("항목이 없으면 총액은 0이다")
        @Test
        void zeroTotalWhenNoItems() {
            // arrange
            OrderModel order = new OrderModel(1L);
            // act
            order.calculateTotalAmount();
            // assert
            assertThat(order.getTotalAmount().value()).isEqualTo(0);
        }
    }
}

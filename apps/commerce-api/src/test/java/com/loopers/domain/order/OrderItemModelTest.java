package com.loopers.domain.order;

import com.loopers.domain.product.Money;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderItemModelTest {

    @DisplayName("주문 항목 생성")
    @Nested
    class Create {

        @DisplayName("정상적으로 생성된다")
        @Test
        void createsSuccessfully() {
            // arrange & act
            OrderItemModel item = new OrderItemModel(1L, "에어맥스", new Money(129000), 2);
            // assert
            assertAll(
                () -> assertThat(item.getProductId()).isEqualTo(1L),
                () -> assertThat(item.getProductName()).isEqualTo("에어맥스"),
                () -> assertThat(item.getProductPrice().value()).isEqualTo(129000),
                () -> assertThat(item.getQuantity()).isEqualTo(2)
            );
        }

        @DisplayName("수량이 0 이하면 BAD_REQUEST 예외가 발생한다")
        @Test
        void throwsOnZeroQuantity() {
            // act
            CoreException exception = assertThrows(CoreException.class, () -> {
                new OrderItemModel(1L, "에어맥스", new Money(129000), 0);
            });
            // assert
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }
}

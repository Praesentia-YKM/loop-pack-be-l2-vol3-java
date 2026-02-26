package com.loopers.domain.stock;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StockModelTest {

    @DisplayName("재고 생성")
    @Nested
    class Create {

        @DisplayName("유효한 정보로 생성할 수 있다")
        @Test
        void createsWithValidInput() {
            // given & when
            StockModel stock = new StockModel(1L, 100);

            // then
            assertAll(
                () -> assertThat(stock.productId()).isEqualTo(1L),
                () -> assertThat(stock.quantity()).isEqualTo(100)
            );
        }
    }

    @DisplayName("재고 감소")
    @Nested
    class Decrease {

        @DisplayName("충분한 수량이면 감소시킨다")
        @Test
        void decreasesQuantity() {
            // given
            StockModel stock = new StockModel(1L, 100);

            // when
            stock.decrease(30);

            // then
            assertThat(stock.quantity()).isEqualTo(70);
        }

        @DisplayName("수량을 초과하면 BAD_REQUEST 예외를 던진다")
        @Test
        void throwsWhenExceedsQuantity() {
            // given
            StockModel stock = new StockModel(1L, 10);

            // when
            CoreException result = assertThrows(CoreException.class, () -> stock.decrease(11));

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

    @DisplayName("재고 증가")
    @Nested
    class Increase {

        @DisplayName("수량을 증가시킨다")
        @Test
        void increasesQuantity() {
            // given
            StockModel stock = new StockModel(1L, 100);

            // when
            stock.increase(50);

            // then
            assertThat(stock.quantity()).isEqualTo(150);
        }
    }

    @DisplayName("재고 충분 여부 확인")
    @Nested
    class HasEnough {

        @DisplayName("수량이 충분하면 true를 반환한다")
        @Test
        void returnsTrueWhenEnough() {
            // given
            StockModel stock = new StockModel(1L, 10);

            // when & then
            assertThat(stock.hasEnough(10)).isTrue();
        }

        @DisplayName("수량이 부족하면 false를 반환한다")
        @Test
        void returnsFalseWhenNotEnough() {
            // given
            StockModel stock = new StockModel(1L, 10);

            // when & then
            assertThat(stock.hasEnough(11)).isFalse();
        }
    }

    @DisplayName("재고 상태 변환")
    @Nested
    class ToStatus {

        @DisplayName("수량이 11 이상이면 IN_STOCK을 반환한다")
        @Test
        void returnsInStock() {
            // given
            StockModel stock = new StockModel(1L, 11);

            // when & then
            assertThat(stock.toStatus()).isEqualTo(StockStatus.IN_STOCK);
        }

        @DisplayName("수량이 1~10이면 LOW_STOCK을 반환한다")
        @Test
        void returnsLowStock() {
            // given
            StockModel stock = new StockModel(1L, 5);

            // when & then
            assertThat(stock.toStatus()).isEqualTo(StockStatus.LOW_STOCK);
        }

        @DisplayName("수량이 0이면 OUT_OF_STOCK을 반환한다")
        @Test
        void returnsOutOfStock() {
            // given
            StockModel stock = new StockModel(1L, 0);

            // when & then
            assertThat(stock.toStatus()).isEqualTo(StockStatus.OUT_OF_STOCK);
        }
    }
}

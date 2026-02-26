package com.loopers.domain.product;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoneyTest {

    @DisplayName("Money 생성")
    @Nested
    class Create {

        @DisplayName("음수로 생성하면 BAD_REQUEST 예외를 던진다")
        @Test
        void throwsOnNegativeValue() {
            // given & when
            CoreException result = assertThrows(CoreException.class, () -> new Money(-1));

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("0으로 생성할 수 있다")
        @Test
        void createsWithZero() {
            // given & when
            Money money = new Money(0);

            // then
            assertThat(money.value()).isEqualTo(0);
        }

        @DisplayName("양수로 생성하면 value()로 값을 반환한다")
        @Test
        void createsWithPositiveValue() {
            // given & when
            Money money = new Money(10000);

            // then
            assertThat(money.value()).isEqualTo(10000);
        }
    }

    @DisplayName("Money 연산")
    @Nested
    class Operations {

        @DisplayName("add()로 두 Money를 합산할 수 있다")
        @Test
        void addsTwo() {
            // given
            Money a = new Money(1000);
            Money b = new Money(2000);

            // when
            Money result = a.add(b);

            // then
            assertThat(result.value()).isEqualTo(3000);
        }

        @DisplayName("multiply()로 Money에 정수를 곱할 수 있다")
        @Test
        void multipliesByInt() {
            // given
            Money money = new Money(1000);

            // when
            Money result = money.multiply(3);

            // then
            assertThat(result.value()).isEqualTo(3000);
        }
    }

    @DisplayName("Money 동등성")
    @Nested
    class Equality {

        @DisplayName("같은 값의 Money는 동등하다")
        @Test
        void equalsWithSameValue() {
            // given
            Money a = new Money(1000);
            Money b = new Money(1000);

            // when & then
            assertAll(
                () -> assertThat(a).isEqualTo(b),
                () -> assertThat(a.hashCode()).isEqualTo(b.hashCode())
            );
        }

        @DisplayName("다른 값의 Money는 동등하지 않다")
        @Test
        void notEqualsWithDifferentValue() {
            // given
            Money a = new Money(1000);
            Money b = new Money(2000);

            // when & then
            assertThat(a).isNotEqualTo(b);
        }
    }
}

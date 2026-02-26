package com.loopers.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ProductModelTest {

    @DisplayName("상품 생성")
    @Nested
    class Create {

        @DisplayName("유효한 정보로 생성할 수 있다")
        @Test
        void createsWithValidInput() {
            // given
            String name = "에어맥스 90";
            String description = "나이키 클래식 러닝화";
            Money price = new Money(129000);
            Long brandId = 1L;

            // when
            ProductModel product = new ProductModel(name, description, price, brandId);

            // then
            assertAll(
                () -> assertThat(product.name()).isEqualTo(name),
                () -> assertThat(product.description()).isEqualTo(description),
                () -> assertThat(product.price()).isEqualTo(price),
                () -> assertThat(product.brandId()).isEqualTo(brandId),
                () -> assertThat(product.likeCount()).isEqualTo(0)
            );
        }

        @DisplayName("description이 null이어도 생성할 수 있다")
        @Test
        void createsWithNullDescription() {
            // given & when
            ProductModel product = new ProductModel("에어맥스 90", null, new Money(129000), 1L);

            // then
            assertAll(
                () -> assertThat(product.name()).isEqualTo("에어맥스 90"),
                () -> assertThat(product.description()).isNull()
            );
        }
    }

    @DisplayName("상품 수정")
    @Nested
    class Update {

        @DisplayName("name, description, price를 변경할 수 있다")
        @Test
        void updatesNameDescriptionPrice() {
            // given
            ProductModel product = new ProductModel("에어맥스 90", "러닝화", new Money(129000), 1L);
            String newName = "에어맥스 95";
            String newDescription = "뉴 러닝화";
            Money newPrice = new Money(159000);

            // when
            product.update(newName, newDescription, newPrice);

            // then
            assertAll(
                () -> assertThat(product.name()).isEqualTo(newName),
                () -> assertThat(product.description()).isEqualTo(newDescription),
                () -> assertThat(product.price()).isEqualTo(newPrice)
            );
        }
    }

    @DisplayName("likeCount 초기값")
    @Nested
    class LikeCount {

        @DisplayName("생성 시 likeCount는 0이다")
        @Test
        void defaultsToZero() {
            // given & when
            ProductModel product = new ProductModel("에어맥스 90", "러닝화", new Money(129000), 1L);

            // then
            assertThat(product.likeCount()).isEqualTo(0);
        }
    }
}

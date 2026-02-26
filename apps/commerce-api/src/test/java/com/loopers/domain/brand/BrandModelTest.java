package com.loopers.domain.brand;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class BrandModelTest {

    @DisplayName("브랜드 생성")
    @Nested
    class Create {

        @DisplayName("유효한 정보로 생성할 수 있다")
        @Test
        void createsWithValidInput() {
            // given
            BrandName name = new BrandName("나이키");
            String description = "스포츠 브랜드";

            // when
            BrandModel brand = new BrandModel(name, description);

            // then
            assertAll(
                () -> assertThat(brand.name()).isEqualTo(name),
                () -> assertThat(brand.description()).isEqualTo(description)
            );
        }

        @DisplayName("description이 null이어도 생성할 수 있다")
        @Test
        void createsWithNullDescription() {
            // given
            BrandName name = new BrandName("아디다스");

            // when
            BrandModel brand = new BrandModel(name, null);

            // then
            assertAll(
                () -> assertThat(brand.name()).isEqualTo(name),
                () -> assertThat(brand.description()).isNull()
            );
        }
    }

    @DisplayName("브랜드 수정")
    @Nested
    class Update {

        @DisplayName("이름과 설명을 변경할 수 있다")
        @Test
        void updatesNameAndDescription() {
            // given
            BrandModel brand = new BrandModel(new BrandName("나이키"), "스포츠 브랜드");
            BrandName newName = new BrandName("뉴발란스");
            String newDescription = "라이프스타일 브랜드";

            // when
            brand.update(newName, newDescription);

            // then
            assertAll(
                () -> assertThat(brand.name()).isEqualTo(newName),
                () -> assertThat(brand.description()).isEqualTo(newDescription)
            );
        }
    }
}

package com.loopers.domain.like;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LikeModelTest {

    @DisplayName("좋아요 생성")
    @Nested
    class Create {

        @DisplayName("회원ID와 상품ID로 정상 생성된다")
        @Test
        void createsSuccessfully() {
            // arrange & act
            LikeModel like = new LikeModel(1L, 100L);
            // assert
            assertAll(
                () -> assertThat(like.getMemberId()).isEqualTo(1L),
                () -> assertThat(like.getProductId()).isEqualTo(100L)
            );
        }
    }
}

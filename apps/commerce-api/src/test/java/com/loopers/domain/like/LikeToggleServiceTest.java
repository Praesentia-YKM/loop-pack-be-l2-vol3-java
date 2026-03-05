package com.loopers.domain.like;

import com.loopers.domain.product.Money;
import com.loopers.domain.product.ProductModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LikeToggleServiceTest {

    private LikeToggleService likeToggleService;

    @BeforeEach
    void setUp() {
        likeToggleService = new LikeToggleService();
    }

    private ProductModel createProduct() {
        return new ProductModel("에어맥스", "설명", new Money(129000), 1L);
    }

    @DisplayName("좋아요 토글 - like")
    @Nested
    class Like {

        @DisplayName("좋아요가 없으면 새로 생성하고 likeCount를 증가시킨다")
        @Test
        void createsNewLikeWhenNoneExists() {
            // given
            ProductModel product = createProduct();
            Optional<LikeModel> existing = Optional.empty();

            // when
            Optional<LikeModel> result = likeToggleService.like(existing, product, 1L, 100L);

            // then
            assertAll(
                () -> assertThat(result).isPresent(),
                () -> assertThat(result.get().userId()).isEqualTo(1L),
                () -> assertThat(result.get().productId()).isEqualTo(100L),
                () -> assertThat(product.likeCount()).isEqualTo(1)
            );
        }

        @DisplayName("삭제된 좋아요가 있으면 복구하고 likeCount를 증가시킨다")
        @Test
        void restoresDeletedLike() {
            // given
            ProductModel product = createProduct();
            LikeModel deletedLike = new LikeModel(1L, 100L);
            deletedLike.delete();
            Optional<LikeModel> existing = Optional.of(deletedLike);

            // when
            Optional<LikeModel> result = likeToggleService.like(existing, product, 1L, 100L);

            // then
            assertAll(
                () -> assertThat(result).isEmpty(),
                () -> assertThat(deletedLike.getDeletedAt()).isNull(),
                () -> assertThat(product.likeCount()).isEqualTo(1)
            );
        }

        @DisplayName("이미 활성 좋아요가 있으면 아무것도 하지 않는다 (멱등)")
        @Test
        void skipsWhenAlreadyActive() {
            // given
            ProductModel product = createProduct();
            LikeModel activeLike = new LikeModel(1L, 100L);
            Optional<LikeModel> existing = Optional.of(activeLike);

            // when
            Optional<LikeModel> result = likeToggleService.like(existing, product, 1L, 100L);

            // then
            assertAll(
                () -> assertThat(result).isEmpty(),
                () -> assertThat(product.likeCount()).isEqualTo(0)
            );
        }
    }

    @DisplayName("좋아요 토글 - unlike")
    @Nested
    class Unlike {

        @DisplayName("활성 좋아요를 삭제하고 likeCount를 감소시킨다")
        @Test
        void deletesLikeAndDecrementsCount() {
            // given
            ProductModel product = createProduct();
            product.incrementLikeCount();
            LikeModel activeLike = new LikeModel(1L, 100L);

            // when
            likeToggleService.unlike(activeLike, product);

            // then
            assertAll(
                () -> assertThat(activeLike.getDeletedAt()).isNotNull(),
                () -> assertThat(product.likeCount()).isEqualTo(0)
            );
        }
    }
}

package com.loopers.domain.like;

import com.loopers.domain.product.ProductModel;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 좋아요 토글 도메인 서비스.
 * Like와 Product 두 엔티티의 상태를 종합하여 좋아요 반응을 결정한다.
 *
 * 인프라(Repository, DB) 의존 없이 순수 비즈니스 의사결정만 담당.
 */
@Component
public class LikeToggleService {

    /**
     * 좋아요 토글: 상태에 따라 신규 생성 / 복구 / 멱등 무시를 결정한다.
     *
     * @return 새로 생성된 LikeModel (저장 필요), 또는 empty (기존 엔티티 변경만 발생)
     */
    public Optional<LikeModel> like(Optional<LikeModel> existing, ProductModel product,
                                     Long userId, Long productId) {
        if (existing.isEmpty()) {
            product.incrementLikeCount();
            return Optional.of(new LikeModel(userId, productId));
        }

        LikeModel like = existing.get();
        if (like.getDeletedAt() != null) {
            like.restore();
            product.incrementLikeCount();
        }
        // else: 이미 활성 좋아요 → 멱등 무시

        return Optional.empty();
    }

    /**
     * 좋아요 취소: 활성 좋아요를 삭제하고 likeCount를 감소시킨다.
     */
    public void unlike(LikeModel like, ProductModel product) {
        like.delete();
        product.decrementLikeCount();
    }
}

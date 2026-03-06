package com.loopers.application.like;

import com.loopers.domain.like.LikeModel;
import com.loopers.domain.product.ProductModel;
import com.loopers.application.product.ProductService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class LikeFacade {

    private final LikeService likeService;
    private final ProductService productService;
    private final LikeTransactionService likeTransactionService;

    private static final int MAX_RETRY = 10;

    public void like(Long userId, Long productId) {
        retryOnOptimisticLock(() -> likeTransactionService.doLike(userId, productId));
    }

    public void unlike(Long userId, Long productId) {
        retryOnOptimisticLock(() -> likeTransactionService.doUnlike(userId, productId));
    }

    private void retryOnOptimisticLock(Runnable action) {
        for (int attempt = 0; attempt < MAX_RETRY; attempt++) {
            try {
                action.run();
                return;
            } catch (ObjectOptimisticLockingFailureException e) {
                if (attempt == MAX_RETRY - 1) {
                    throw new CoreException(ErrorType.CONFLICT, "동시 요청이 많아 처리에 실패했습니다. 다시 시도해주세요.");
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public Page<LikeModel> getMyLikes(Long userId, Pageable pageable) {
        return likeService.getMyLikes(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<LikeWithProduct> getMyLikesWithProducts(Long userId, Pageable pageable) {
        Page<LikeModel> likes = likeService.getMyLikes(userId, pageable);
        return likes.map(like -> {
            ProductModel product = productService.getProduct(like.productId());
            return new LikeWithProduct(
                like.getId(),
                product.getId(),
                product.name(),
                product.price().value(),
                like.getCreatedAt()
            );
        });
    }
}

package com.loopers.application.like;

import com.loopers.domain.like.LikeModel;
import com.loopers.domain.like.LikeToggleService;
import com.loopers.domain.product.ProductModel;
import com.loopers.application.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class LikeFacade {

    private final LikeService likeService;
    private final ProductService productService;
    private final LikeToggleService likeToggleService;

    @Transactional
    public void like(Long userId, Long productId) {
        ProductModel product = productService.getProduct(productId);
        Optional<LikeModel> existing = likeService.findByUserIdAndProductId(userId, productId);

        Optional<LikeModel> newLike = likeToggleService.like(existing, product, userId, productId);
        newLike.ifPresent(likeService::save);
    }

    @Transactional
    public void unlike(Long userId, Long productId) {
        Optional<LikeModel> activeLike = likeService.findActiveLike(userId, productId);
        if (activeLike.isEmpty()) return;

        ProductModel product = productService.getProduct(activeLike.get().productId());
        likeToggleService.unlike(activeLike.get(), product);
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

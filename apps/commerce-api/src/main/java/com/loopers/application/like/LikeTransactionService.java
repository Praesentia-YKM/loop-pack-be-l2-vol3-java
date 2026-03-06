package com.loopers.application.like;

import com.loopers.domain.like.LikeModel;
import com.loopers.domain.like.LikeToggleService;
import com.loopers.domain.product.ProductModel;
import com.loopers.application.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class LikeTransactionService {

    private final LikeService likeService;
    private final ProductService productService;
    private final LikeToggleService likeToggleService;

    @Transactional
    public void doLike(Long userId, Long productId) {
        ProductModel product = productService.getProduct(productId);
        Optional<LikeModel> existing = likeService.findByUserIdAndProductId(userId, productId);

        Optional<LikeModel> newLike = likeToggleService.like(existing, product, userId, productId);
        newLike.ifPresent(likeService::save);
    }

    @Transactional
    public void doUnlike(Long userId, Long productId) {
        Optional<LikeModel> activeLike = likeService.findActiveLike(userId, productId);
        if (activeLike.isEmpty()) return;

        ProductModel product = productService.getProduct(activeLike.get().productId());
        likeToggleService.unlike(activeLike.get(), product);
    }
}

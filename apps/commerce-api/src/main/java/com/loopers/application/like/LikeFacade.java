package com.loopers.application.like;

import com.loopers.domain.brand.BrandModel;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.like.LikeModel;
import com.loopers.domain.like.LikeService;
import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.ProductService;
import com.loopers.support.error.CoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class LikeFacade {

    private final LikeService likeService;
    private final ProductService productService;
    private final BrandService brandService;

    @Transactional
    public void register(Long memberId, Long productId) {
        productService.getById(productId);
        boolean created = likeService.register(memberId, productId);
        if (created) {
            productService.increaseLikeCount(productId);
        }
    }

    @Transactional
    public void cancel(Long memberId, Long productId) {
        productService.getById(productId);
        boolean cancelled = likeService.cancel(memberId, productId);
        if (cancelled) {
            productService.decreaseLikeCount(productId);
        }
    }

    @Transactional(readOnly = true)
    public Page<LikeInfo> getMyLikes(Long memberId, Pageable pageable) {
        Page<LikeModel> likes = likeService.getMyLikes(memberId, pageable);
        List<LikeInfo> filtered = likes.getContent().stream()
            .map(like -> toLikeInfo(like))
            .filter(Objects::nonNull)
            .toList();
        return new PageImpl<>(filtered, pageable, likes.getTotalElements());
    }

    private LikeInfo toLikeInfo(LikeModel like) {
        try {
            ProductModel product = productService.getById(like.getProductId());
            BrandModel brand = brandService.getById(product.getBrandId());
            return LikeInfo.from(like, product, brand);
        } catch (CoreException e) {
            return null;
        }
    }
}

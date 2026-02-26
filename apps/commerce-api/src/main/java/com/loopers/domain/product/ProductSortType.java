package com.loopers.domain.product;

import org.springframework.data.domain.Sort;

public enum ProductSortType {
    LATEST(Sort.by(Sort.Direction.DESC, "createdAt")),
    PRICE_ASC(Sort.by(Sort.Direction.ASC, "price.value")),
    PRICE_DESC(Sort.by(Sort.Direction.DESC, "price.value")),
    LIKES_DESC(Sort.by(Sort.Direction.DESC, "likeCount"));

    private final Sort sort;

    ProductSortType(Sort sort) {
        this.sort = sort;
    }

    public Sort toSort() {
        return sort;
    }
}

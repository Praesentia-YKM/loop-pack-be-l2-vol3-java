package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductInfo;

public class ProductV1Dto {

    public record ProductSummaryResponse(
        Long id, String name, int price, String brandName, String stockStatus
    ) {
        public static ProductSummaryResponse from(ProductInfo.Summary info) {
            return new ProductSummaryResponse(
                info.id(), info.name(), info.price(), info.brandName(), info.stockStatus().name()
            );
        }
    }

    public record ProductDetailResponse(
        Long id, String name, String description, int price,
        String brandName, int likeCount, String stockStatus
    ) {
        public static ProductDetailResponse from(ProductInfo.Detail info) {
            return new ProductDetailResponse(
                info.id(), info.name(), info.description(), info.price(),
                info.brandName(), info.likeCount(), info.stockStatus().name()
            );
        }
    }
}

package com.loopers.interfaces.api.product;

import com.loopers.domain.product.ProductModel;
import com.loopers.domain.stock.StockStatus;

public class ProductV1Dto {

    public record ProductResponse(
        Long id,
        String name,
        String description,
        int price,
        Long brandId,
        String brandName,
        int likeCount,
        StockStatus stockStatus
    ) {
        public static ProductResponse from(ProductModel model, String brandName, StockStatus stockStatus) {
            return new ProductResponse(
                model.getId(),
                model.name(),
                model.description(),
                model.price().value(),
                model.brandId(),
                brandName,
                model.likeCount(),
                stockStatus
            );
        }
    }
}

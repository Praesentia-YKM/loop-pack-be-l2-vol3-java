package com.loopers.interfaces.api.product.admin;

import com.loopers.domain.product.ProductModel;

import java.time.ZonedDateTime;

public class ProductAdminV1Dto {

    public record CreateRequest(
        String name,
        String description,
        int price,
        Long brandId,
        int initialStock
    ) {}

    public record UpdateRequest(
        String name,
        String description,
        int price
    ) {}

    public record ProductResponse(
        Long id,
        String name,
        String description,
        int price,
        Long brandId,
        String brandName,
        int likeCount,
        int stockQuantity,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt,
        ZonedDateTime deletedAt
    ) {
        public static ProductResponse from(ProductModel model, String brandName, int stockQuantity) {
            return new ProductResponse(
                model.getId(),
                model.name(),
                model.description(),
                model.price().value(),
                model.brandId(),
                brandName,
                model.likeCount(),
                stockQuantity,
                model.getCreatedAt(),
                model.getUpdatedAt(),
                model.getDeletedAt()
            );
        }
    }
}

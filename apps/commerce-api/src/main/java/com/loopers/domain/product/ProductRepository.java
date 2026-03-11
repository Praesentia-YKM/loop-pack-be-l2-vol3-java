package com.loopers.domain.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Optional<ProductModel> findById(Long id);
    List<ProductModel> findAllByBrandId(Long brandId);
    Page<ProductModel> findAll(Pageable pageable, ProductSortType sortType);
    ProductModel save(ProductModel product);
}

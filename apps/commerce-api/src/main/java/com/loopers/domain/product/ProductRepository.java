package com.loopers.domain.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    ProductModel save(ProductModel product);

    Optional<ProductModel> findById(Long id);

    Page<ProductModel> findAllByDeletedAtIsNull(Pageable pageable);

    Page<ProductModel> findAllByBrandIdAndDeletedAtIsNull(Long brandId, Pageable pageable);

    Page<ProductModel> findAll(Pageable pageable);

    Page<ProductModel> findAllByBrandId(Long brandId, Pageable pageable);

    List<ProductModel> findAllByBrandId(Long brandId);
}

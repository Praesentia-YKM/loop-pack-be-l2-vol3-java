package com.loopers.domain.brand;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BrandRepository {
    Optional<BrandModel> findById(Long id);
    Optional<BrandModel> findByName(String name);
    Page<BrandModel> findAll(Pageable pageable);
    BrandModel save(BrandModel brand);
}

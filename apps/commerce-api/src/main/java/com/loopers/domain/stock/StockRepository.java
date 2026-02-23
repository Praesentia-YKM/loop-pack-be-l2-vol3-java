package com.loopers.domain.stock;

import java.util.Optional;

public interface StockRepository {
    Optional<StockModel> findByProductId(Long productId);
    StockModel save(StockModel stock);
}

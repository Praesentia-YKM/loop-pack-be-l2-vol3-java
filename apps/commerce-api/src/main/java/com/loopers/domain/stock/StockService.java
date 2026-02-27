package com.loopers.domain.stock;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class StockService {

    private final StockRepository stockRepository;

    @Transactional
    public StockModel create(Long productId, int quantity) {
        StockModel stock = new StockModel(productId, quantity);
        return stockRepository.save(stock);
    }

    @Transactional(readOnly = true)
    public StockModel getByProductId(Long productId) {
        return stockRepository.findByProductId(productId)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "재고를 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public Map<Long, StockModel> getByProductIds(List<Long> productIds) {
        return stockRepository.findAllByProductIdIn(productIds)
            .stream()
            .collect(Collectors.toMap(StockModel::productId, stock -> stock));
    }
}

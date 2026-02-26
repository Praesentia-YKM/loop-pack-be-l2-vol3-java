package com.loopers.domain.stock;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    private StockService stockService;

    @BeforeEach
    void setUp() {
        stockService = new StockService(stockRepository);
    }

    @DisplayName("재고 생성")
    @Nested
    class Create {

        @DisplayName("productId와 quantity로 재고를 생성한다")
        @Test
        void createsStock() {
            // given
            Long productId = 1L;
            int quantity = 100;
            when(stockRepository.save(any(StockModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

            // when
            StockModel result = stockService.create(productId, quantity);

            // then
            assertAll(
                () -> assertThat(result.productId()).isEqualTo(productId),
                () -> assertThat(result.quantity()).isEqualTo(quantity)
            );
            verify(stockRepository).save(any(StockModel.class));
        }
    }

    @DisplayName("상품별 재고 조회")
    @Nested
    class GetByProductId {

        @DisplayName("존재하면 재고를 반환한다")
        @Test
        void returnsStockWhenExists() {
            // given
            Long productId = 1L;
            StockModel stock = new StockModel(productId, 100);
            when(stockRepository.findByProductId(productId)).thenReturn(Optional.of(stock));

            // when
            StockModel result = stockService.getByProductId(productId);

            // then
            assertAll(
                () -> assertThat(result.productId()).isEqualTo(productId),
                () -> assertThat(result.quantity()).isEqualTo(100)
            );
        }

        @DisplayName("미존재 시 NOT_FOUND 예외를 던진다")
        @Test
        void throwsWhenNotFound() {
            // given
            Long productId = 999L;
            when(stockRepository.findByProductId(productId)).thenReturn(Optional.empty());

            // when
            CoreException result = assertThrows(CoreException.class,
                () -> stockService.getByProductId(productId));

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }
}

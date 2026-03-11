package com.loopers.application.product;

import com.loopers.domain.brand.BrandModel;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.product.Money;
import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.stock.StockModel;
import com.loopers.domain.stock.StockService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductFacadeTest {

    @InjectMocks
    private ProductFacade productFacade;

    @Mock
    private ProductService productService;

    @Mock
    private BrandService brandService;

    @Mock
    private StockService stockService;

    @DisplayName("상품 등록")
    @Nested
    class Register {

        @DisplayName("브랜드가 존재하면 상품을 등록하고 AdminDetail을 반환한다")
        @Test
        void registersSuccessfully() {
            // arrange
            Long brandId = 1L;
            BrandModel brand = new BrandModel("나이키", "스포츠");
            ProductModel product = new ProductModel("에어맥스", "러닝화", new Money(129000), brandId);
            StockModel stock = new StockModel(1L, 100);
            given(brandService.getById(brandId)).willReturn(brand);
            given(productService.register(eq("에어맥스"), eq("러닝화"), any(Money.class), eq(brandId))).willReturn(product);
            given(stockService.save(anyLong(), eq(100))).willReturn(stock);
            // act
            ProductInfo.AdminDetail result = productFacade.register("에어맥스", "러닝화", new Money(129000), brandId, 100);
            // assert
            assertAll(
                () -> assertThat(result.name()).isEqualTo("에어맥스"),
                () -> assertThat(result.price()).isEqualTo(129000),
                () -> assertThat(result.stockQuantity()).isEqualTo(100)
            );
        }

        @DisplayName("삭제된 브랜드면 BAD_REQUEST 예외가 발생한다")
        @Test
        void throwsOnDeletedBrand() {
            // arrange
            Long brandId = 1L;
            BrandModel brand = new BrandModel("나이키", "스포츠");
            brand.delete();
            given(brandService.getById(brandId)).willReturn(brand);
            // act
            CoreException exception = assertThrows(CoreException.class, () -> {
                productFacade.register("에어맥스", "러닝화", new Money(129000), brandId, 100);
            });
            // assert
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

    @DisplayName("상품 상세 조회 (고객)")
    @Nested
    class GetDetailForCustomer {

        @DisplayName("상품, 브랜드, 재고 정보를 조합하여 반환한다")
        @Test
        void returnsDetailWithStockStatus() {
            // arrange
            Long productId = 1L;
            Long brandId = 1L;
            ProductModel product = new ProductModel("에어맥스", "러닝화", new Money(129000), brandId);
            ReflectionTestUtils.setField(product, "id", productId);
            BrandModel brand = new BrandModel("나이키", "스포츠");
            StockModel stock = new StockModel(productId, 3);
            given(productService.getById(productId)).willReturn(product);
            given(brandService.getById(brandId)).willReturn(brand);
            given(stockService.getByProductId(productId)).willReturn(stock);
            // act
            ProductInfo.Detail result = productFacade.getDetailForCustomer(productId);
            // assert
            assertAll(
                () -> assertThat(result.name()).isEqualTo("에어맥스"),
                () -> assertThat(result.brandName()).isEqualTo("나이키"),
                () -> assertThat(result.stockStatus()).isEqualTo(ProductInfo.StockStatus.LOW_STOCK)
            );
        }
    }
}

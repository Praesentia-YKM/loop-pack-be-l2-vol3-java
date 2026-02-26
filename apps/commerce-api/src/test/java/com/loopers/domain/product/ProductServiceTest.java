package com.loopers.domain.product;

import com.loopers.domain.brand.BrandModel;
import com.loopers.domain.brand.BrandName;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.stock.StockService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private StockService stockService;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, brandRepository, stockService);
    }

    @DisplayName("상품 등록")
    @Nested
    class Register {

        @DisplayName("성공하면 저장된 ProductModel을 반환한다")
        @Test
        void returnsSavedProduct() {
            // given
            String name = "에어맥스 90";
            String description = "러닝화";
            Money price = new Money(129000);
            Long brandId = 1L;
            int initialStock = 100;

            BrandModel brand = new BrandModel(new BrandName("나이키"), "스포츠 브랜드");
            when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
            when(productRepository.save(any(ProductModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

            // when
            ProductModel result = productService.register(name, description, price, brandId, initialStock);

            // then
            assertAll(
                () -> assertThat(result.name()).isEqualTo(name),
                () -> assertThat(result.description()).isEqualTo(description),
                () -> assertThat(result.price()).isEqualTo(price),
                () -> assertThat(result.brandId()).isEqualTo(brandId)
            );
            verify(productRepository).save(any(ProductModel.class));
        }

        @DisplayName("삭제된 브랜드에 등록하면 NOT_FOUND 예외를 던진다")
        @Test
        void throwsWhenBrandDeleted() {
            // given
            Long brandId = 1L;
            BrandModel brand = new BrandModel(new BrandName("나이키"), "스포츠 브랜드");
            brand.delete();
            when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));

            // when
            CoreException result = assertThrows(CoreException.class,
                () -> productService.register("에어맥스", "러닝화", new Money(129000), brandId, 100));

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }

        @DisplayName("존재하지 않는 브랜드에 등록하면 NOT_FOUND 예외를 던진다")
        @Test
        void throwsWhenBrandNotFound() {
            // given
            Long brandId = 999L;
            when(brandRepository.findById(brandId)).thenReturn(Optional.empty());

            // when
            CoreException result = assertThrows(CoreException.class,
                () -> productService.register("에어맥스", "러닝화", new Money(129000), brandId, 100));

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }

    @DisplayName("상품 조회 (Customer)")
    @Nested
    class GetProduct {

        @DisplayName("미삭제 상품을 반환한다")
        @Test
        void returnsProductWhenNotDeleted() {
            // given
            Long productId = 1L;
            ProductModel product = new ProductModel("에어맥스", "러닝화", new Money(129000), 1L);
            when(productRepository.findById(productId)).thenReturn(Optional.of(product));

            // when
            ProductModel result = productService.getProduct(productId);

            // then
            assertThat(result.name()).isEqualTo("에어맥스");
        }

        @DisplayName("삭제된 상품이면 NOT_FOUND 예외를 던진다")
        @Test
        void throwsWhenDeleted() {
            // given
            Long productId = 1L;
            ProductModel product = new ProductModel("에어맥스", "러닝화", new Money(129000), 1L);
            product.delete();
            when(productRepository.findById(productId)).thenReturn(Optional.of(product));

            // when
            CoreException result = assertThrows(CoreException.class,
                () -> productService.getProduct(productId));

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }

        @DisplayName("미존재 상품이면 NOT_FOUND 예외를 던진다")
        @Test
        void throwsWhenNotFound() {
            // given
            Long productId = 999L;
            when(productRepository.findById(productId)).thenReturn(Optional.empty());

            // when
            CoreException result = assertThrows(CoreException.class,
                () -> productService.getProduct(productId));

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }

    @DisplayName("상품 조회 (Admin)")
    @Nested
    class GetProductForAdmin {

        @DisplayName("삭제 여부와 관계없이 반환한다")
        @Test
        void returnsProductRegardlessOfDeletion() {
            // given
            Long productId = 1L;
            ProductModel product = new ProductModel("에어맥스", "러닝화", new Money(129000), 1L);
            product.delete();
            when(productRepository.findById(productId)).thenReturn(Optional.of(product));

            // when
            ProductModel result = productService.getProductForAdmin(productId);

            // then
            assertThat(result.name()).isEqualTo("에어맥스");
        }
    }

    @DisplayName("상품 목록 조회 (Customer)")
    @Nested
    class GetProducts {

        @DisplayName("brandId 없이 조회하면 미삭제 상품을 반환한다")
        @Test
        void returnsNotDeletedProducts() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            List<ProductModel> products = List.of(
                new ProductModel("에어맥스 90", "러닝화", new Money(129000), 1L),
                new ProductModel("에어맥스 95", "러닝화", new Money(159000), 1L)
            );
            Page<ProductModel> page = new PageImpl<>(products, pageable, products.size());
            when(productRepository.findAllByDeletedAtIsNull(any(Pageable.class))).thenReturn(page);

            // when
            Page<ProductModel> result = productService.getProducts(null, ProductSortType.LATEST, pageable);

            // then
            assertThat(result.getTotalElements()).isEqualTo(2);
        }

        @DisplayName("brandId로 필터링하여 조회한다")
        @Test
        void filtersbyBrandId() {
            // given
            Long brandId = 1L;
            Pageable pageable = PageRequest.of(0, 10);
            List<ProductModel> products = List.of(
                new ProductModel("에어맥스 90", "러닝화", new Money(129000), brandId)
            );
            Page<ProductModel> page = new PageImpl<>(products, pageable, products.size());
            when(productRepository.findAllByBrandIdAndDeletedAtIsNull(any(Long.class), any(Pageable.class))).thenReturn(page);

            // when
            Page<ProductModel> result = productService.getProducts(brandId, ProductSortType.LATEST, pageable);

            // then
            assertThat(result.getTotalElements()).isEqualTo(1);
        }
    }

    @DisplayName("상품 목록 조회 (Admin)")
    @Nested
    class GetProductsForAdmin {

        @DisplayName("삭제 포함하여 조회한다")
        @Test
        void returnsAllProducts() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            List<ProductModel> products = List.of(
                new ProductModel("에어맥스 90", "러닝화", new Money(129000), 1L)
            );
            Page<ProductModel> page = new PageImpl<>(products, pageable, products.size());
            when(productRepository.findAll(pageable)).thenReturn(page);

            // when
            Page<ProductModel> result = productService.getProductsForAdmin(null, pageable);

            // then
            assertThat(result.getTotalElements()).isEqualTo(1);
        }

        @DisplayName("brandId로 필터링하여 조회한다")
        @Test
        void filtersByBrandId() {
            // given
            Long brandId = 1L;
            Pageable pageable = PageRequest.of(0, 10);
            List<ProductModel> products = List.of(
                new ProductModel("에어맥스 90", "러닝화", new Money(129000), brandId)
            );
            Page<ProductModel> page = new PageImpl<>(products, pageable, products.size());
            when(productRepository.findAllByBrandId(brandId, pageable)).thenReturn(page);

            // when
            Page<ProductModel> result = productService.getProductsForAdmin(brandId, pageable);

            // then
            assertThat(result.getTotalElements()).isEqualTo(1);
        }
    }

    @DisplayName("상품 수정")
    @Nested
    class Update {

        @DisplayName("name, description, price를 변경한다")
        @Test
        void updatesSuccessfully() {
            // given
            Long productId = 1L;
            ProductModel product = new ProductModel("에어맥스 90", "러닝화", new Money(129000), 1L);
            when(productRepository.findById(productId)).thenReturn(Optional.of(product));

            // when
            ProductModel result = productService.update(productId, "에어맥스 95", "뉴 러닝화", new Money(159000));

            // then
            assertAll(
                () -> assertThat(result.name()).isEqualTo("에어맥스 95"),
                () -> assertThat(result.description()).isEqualTo("뉴 러닝화"),
                () -> assertThat(result.price()).isEqualTo(new Money(159000))
            );
        }
    }

    @DisplayName("상품 삭제")
    @Nested
    class Delete {

        @DisplayName("soft delete 한다")
        @Test
        void softDeletesSuccessfully() {
            // given
            Long productId = 1L;
            ProductModel product = new ProductModel("에어맥스 90", "러닝화", new Money(129000), 1L);
            when(productRepository.findById(productId)).thenReturn(Optional.of(product));

            // when
            productService.delete(productId);

            // then
            assertThat(product.getDeletedAt()).isNotNull();
        }
    }

    @DisplayName("브랜드별 상품 전체 삭제")
    @Nested
    class DeleteAllByBrandId {

        @DisplayName("해당 브랜드의 모든 상품을 soft delete 한다")
        @Test
        void softDeletesAllByBrandId() {
            // given
            Long brandId = 1L;
            ProductModel product1 = new ProductModel("에어맥스 90", "러닝화", new Money(129000), brandId);
            ProductModel product2 = new ProductModel("에어맥스 95", "러닝화", new Money(159000), brandId);
            when(productRepository.findAllByBrandId(brandId)).thenReturn(List.of(product1, product2));

            // when
            productService.deleteAllByBrandId(brandId);

            // then
            assertAll(
                () -> assertThat(product1.getDeletedAt()).isNotNull(),
                () -> assertThat(product2.getDeletedAt()).isNotNull()
            );
        }
    }
}

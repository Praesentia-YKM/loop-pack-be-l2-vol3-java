package com.loopers.domain.product;

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
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository);
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

            when(productRepository.save(any(ProductModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

            // when
            ProductModel result = productService.register(name, description, price, brandId);

            // then
            assertAll(
                () -> assertThat(result.name()).isEqualTo(name),
                () -> assertThat(result.description()).isEqualTo(description),
                () -> assertThat(result.price()).isEqualTo(price),
                () -> assertThat(result.brandId()).isEqualTo(brandId)
            );
            verify(productRepository).save(any(ProductModel.class));
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

    @DisplayName("상품 배치 조회")
    @Nested
    class GetProductsByIds {

        @DisplayName("ID 목록으로 미삭제 상품을 Map으로 반환한다")
        @Test
        void returnsMapOfProducts() {
            // given
            List<Long> productIds = List.of(1L, 2L);
            ProductModel product1 = new ProductModel("에어맥스", "러닝화", new Money(129000), 1L);
            ReflectionTestUtils.setField(product1, "id", 1L);
            ProductModel product2 = new ProductModel("조던", "농구화", new Money(159000), 1L);
            ReflectionTestUtils.setField(product2, "id", 2L);
            when(productRepository.findAllByIdInAndDeletedAtIsNull(productIds))
                .thenReturn(List.of(product1, product2));

            // when
            Map<Long, ProductModel> result = productService.getProductsByIds(productIds);

            // then
            assertAll(
                () -> assertThat(result).hasSize(2),
                () -> assertThat(result.get(1L).name()).isEqualTo("에어맥스"),
                () -> assertThat(result.get(2L).name()).isEqualTo("조던")
            );
        }

        @DisplayName("삭제된 상품은 제외된다")
        @Test
        void excludesDeletedProducts() {
            // given
            List<Long> productIds = List.of(1L, 2L);
            ProductModel product1 = new ProductModel("에어맥스", "러닝화", new Money(129000), 1L);
            ReflectionTestUtils.setField(product1, "id", 1L);
            when(productRepository.findAllByIdInAndDeletedAtIsNull(productIds))
                .thenReturn(List.of(product1));

            // when
            Map<Long, ProductModel> result = productService.getProductsByIds(productIds);

            // then
            assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result).containsKey(1L),
                () -> assertThat(result).doesNotContainKey(2L)
            );
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

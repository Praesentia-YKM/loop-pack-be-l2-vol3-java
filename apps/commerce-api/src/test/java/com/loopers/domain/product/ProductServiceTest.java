package com.loopers.domain.product;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @DisplayName("상품 등록")
    @Nested
    class Register {

        @DisplayName("정상적으로 등록된다")
        @Test
        void registersSuccessfully() {
            // arrange
            ProductModel product = new ProductModel("에어맥스", "러닝화", new Money(129000), 1L);
            given(productRepository.save(any(ProductModel.class))).willReturn(product);
            // act
            ProductModel result = productService.register("에어맥스", "러닝화", new Money(129000), 1L);
            // assert
            assertAll(
                () -> assertThat(result.getName()).isEqualTo("에어맥스"),
                () -> assertThat(result.getPrice().value()).isEqualTo(129000)
            );
        }
    }

    @DisplayName("상품 조회")
    @Nested
    class GetById {

        @DisplayName("존재하는 상품을 반환한다")
        @Test
        void returnsForExistingId() {
            // arrange
            Long id = 1L;
            ProductModel product = new ProductModel("에어맥스", "러닝화", new Money(129000), 1L);
            given(productRepository.findById(id)).willReturn(Optional.of(product));
            // act
            ProductModel result = productService.getById(id);
            // assert
            assertThat(result.getName()).isEqualTo("에어맥스");
        }

        @DisplayName("존재하지 않는 ID면 NOT_FOUND 예외가 발생한다")
        @Test
        void throwsOnNonExistentId() {
            // arrange
            Long id = 999L;
            given(productRepository.findById(id)).willReturn(Optional.empty());
            // act
            CoreException exception = assertThrows(CoreException.class, () -> {
                productService.getById(id);
            });
            // assert
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }

        @DisplayName("삭제된 상품이면 NOT_FOUND 예외가 발생한다")
        @Test
        void throwsOnDeletedProduct() {
            // arrange
            Long id = 1L;
            ProductModel product = new ProductModel("에어맥스", "러닝화", new Money(129000), 1L);
            product.delete();
            given(productRepository.findById(id)).willReturn(Optional.of(product));
            // act
            CoreException exception = assertThrows(CoreException.class, () -> {
                productService.getById(id);
            });
            // assert
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }

    @DisplayName("상품 수정")
    @Nested
    class Update {

        @DisplayName("상품 정보를 수정한다")
        @Test
        void updatesSuccessfully() {
            // arrange
            Long id = 1L;
            ProductModel product = new ProductModel("에어맥스", "러닝화", new Money(129000), 1L);
            given(productRepository.findById(id)).willReturn(Optional.of(product));
            // act
            ProductModel result = productService.update(id, "에어포스", "캐주얼", new Money(109000));
            // assert
            assertAll(
                () -> assertThat(result.getName()).isEqualTo("에어포스"),
                () -> assertThat(result.getPrice().value()).isEqualTo(109000)
            );
        }
    }

    @DisplayName("상품 삭제")
    @Nested
    class Delete {

        @DisplayName("상품을 soft delete 한다")
        @Test
        void softDeletesProduct() {
            // arrange
            Long id = 1L;
            ProductModel product = new ProductModel("에어맥스", "러닝화", new Money(129000), 1L);
            given(productRepository.findById(id)).willReturn(Optional.of(product));
            // act
            productService.delete(id);
            // assert
            assertThat(product.getDeletedAt()).isNotNull();
        }
    }

    @DisplayName("브랜드별 상품 일괄 삭제")
    @Nested
    class SoftDeleteByBrandId {

        @DisplayName("브랜드에 속한 모든 상품을 soft delete 한다")
        @Test
        void softDeletesAllByBrandId() {
            // arrange
            Long brandId = 1L;
            ProductModel product1 = new ProductModel("에어맥스", "러닝", new Money(129000), brandId);
            ProductModel product2 = new ProductModel("에어포스", "캐주얼", new Money(109000), brandId);
            given(productRepository.findAllByBrandId(brandId)).willReturn(List.of(product1, product2));
            // act
            productService.softDeleteByBrandId(brandId);
            // assert
            assertAll(
                () -> assertThat(product1.getDeletedAt()).isNotNull(),
                () -> assertThat(product2.getDeletedAt()).isNotNull()
            );
        }
    }

    @DisplayName("좋아요 수 관리")
    @Nested
    class LikeCount {

        @DisplayName("좋아요 수를 증가시킨다")
        @Test
        void increasesLikeCount() {
            // arrange
            Long id = 1L;
            ProductModel product = new ProductModel("에어맥스", "러닝화", new Money(129000), 1L);
            given(productRepository.findById(id)).willReturn(Optional.of(product));
            // act
            productService.increaseLikeCount(id);
            // assert
            assertThat(product.getLikeCount()).isEqualTo(1);
        }

        @DisplayName("좋아요 수를 감소시킨다")
        @Test
        void decreasesLikeCount() {
            // arrange
            Long id = 1L;
            ProductModel product = new ProductModel("에어맥스", "러닝화", new Money(129000), 1L);
            product.increaseLikeCount();
            given(productRepository.findById(id)).willReturn(Optional.of(product));
            // act
            productService.decreaseLikeCount(id);
            // assert
            assertThat(product.getLikeCount()).isEqualTo(0);
        }
    }
}

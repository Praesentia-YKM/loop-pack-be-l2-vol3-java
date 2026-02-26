package com.loopers.domain.brand;

import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.Money;
import com.loopers.domain.product.ProductRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private ProductRepository productRepository;

    private BrandService brandService;

    @BeforeEach
    void setUp() {
        brandService = new BrandService(brandRepository, productRepository);
    }

    @DisplayName("브랜드 등록")
    @Nested
    class Register {

        @DisplayName("성공하면 저장된 BrandModel을 반환한다")
        @Test
        void returnsSavedBrand() {
            // given
            String name = "나이키";
            String description = "스포츠 브랜드";
            when(brandRepository.findByName(name)).thenReturn(Optional.empty());
            when(brandRepository.save(any(BrandModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

            // when
            BrandModel result = brandService.register(name, description);

            // then
            assertAll(
                () -> assertThat(result.name().value()).isEqualTo(name),
                () -> assertThat(result.description()).isEqualTo(description)
            );
            verify(brandRepository).save(any(BrandModel.class));
        }

        @DisplayName("중복 브랜드명이면 CONFLICT 예외를 던진다")
        @Test
        void throwsOnDuplicateName() {
            // given
            String name = "나이키";
            BrandModel existing = new BrandModel(new BrandName(name), "기존 브랜드");
            when(brandRepository.findByName(name)).thenReturn(Optional.of(existing));

            // when
            CoreException result = assertThrows(CoreException.class,
                () -> brandService.register(name, "스포츠 브랜드"));

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.CONFLICT);
            verify(brandRepository, never()).save(any());
        }
    }

    @DisplayName("브랜드 조회")
    @Nested
    class GetBrand {

        @DisplayName("존재하고 미삭제 상태면 BrandModel을 반환한다")
        @Test
        void returnsBrandWhenExistsAndNotDeleted() {
            // given
            Long brandId = 1L;
            BrandModel brand = new BrandModel(new BrandName("나이키"), "스포츠 브랜드");
            when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));

            // when
            BrandModel result = brandService.getBrand(brandId);

            // then
            assertThat(result.name().value()).isEqualTo("나이키");
        }

        @DisplayName("삭제된 브랜드면 NOT_FOUND 예외를 던진다")
        @Test
        void throwsWhenDeleted() {
            // given
            Long brandId = 1L;
            BrandModel brand = new BrandModel(new BrandName("나이키"), "스포츠 브랜드");
            brand.delete();
            when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));

            // when
            CoreException result = assertThrows(CoreException.class,
                () -> brandService.getBrand(brandId));

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }

        @DisplayName("미존재 브랜드면 NOT_FOUND 예외를 던진다")
        @Test
        void throwsWhenNotFound() {
            // given
            Long brandId = 999L;
            when(brandRepository.findById(brandId)).thenReturn(Optional.empty());

            // when
            CoreException result = assertThrows(CoreException.class,
                () -> brandService.getBrand(brandId));

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }

    @DisplayName("브랜드 상세 조회 (Admin)")
    @Nested
    class GetBrandForAdmin {

        @DisplayName("존재하면 삭제 여부와 관계없이 반환한다")
        @Test
        void returnsBrandRegardlessOfDeletion() {
            // given
            Long brandId = 1L;
            BrandModel brand = new BrandModel(new BrandName("나이키"), "스포츠 브랜드");
            brand.delete();
            when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));

            // when
            BrandModel result = brandService.getBrandForAdmin(brandId);

            // then
            assertThat(result.name().value()).isEqualTo("나이키");
        }

        @DisplayName("미존재 브랜드면 NOT_FOUND 예외를 던진다")
        @Test
        void throwsWhenNotFound() {
            // given
            Long brandId = 999L;
            when(brandRepository.findById(brandId)).thenReturn(Optional.empty());

            // when
            CoreException result = assertThrows(CoreException.class,
                () -> brandService.getBrandForAdmin(brandId));

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }

    @DisplayName("브랜드 수정")
    @Nested
    class Update {

        @DisplayName("성공하면 변경된 정보를 반환한다")
        @Test
        void updatesSuccessfully() {
            // given
            Long brandId = 1L;
            BrandModel brand = new BrandModel(new BrandName("나이키"), "스포츠 브랜드");
            when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
            when(brandRepository.findByName("뉴발란스")).thenReturn(Optional.empty());

            // when
            BrandModel result = brandService.update(brandId, "뉴발란스", "라이프스타일 브랜드");

            // then
            assertAll(
                () -> assertThat(result.name().value()).isEqualTo("뉴발란스"),
                () -> assertThat(result.description()).isEqualTo("라이프스타일 브랜드")
            );
        }

        @DisplayName("동일명 유지 시 중복 체크를 통과한다")
        @Test
        void skipsDuplicateCheckWhenSameName() {
            // given
            Long brandId = 1L;
            BrandModel brand = new BrandModel(new BrandName("나이키"), "스포츠 브랜드");
            when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));

            // when
            BrandModel result = brandService.update(brandId, "나이키", "설명 변경");

            // then
            assertAll(
                () -> assertThat(result.name().value()).isEqualTo("나이키"),
                () -> assertThat(result.description()).isEqualTo("설명 변경")
            );
            verify(brandRepository, never()).findByName(any());
        }

        @DisplayName("다른 이름으로 변경 시 중복이면 CONFLICT 예외를 던진다")
        @Test
        void throwsOnDuplicateNameChange() {
            // given
            Long brandId = 1L;
            BrandModel brand = new BrandModel(new BrandName("나이키"), "스포츠 브랜드");
            BrandModel other = new BrandModel(new BrandName("아디다스"), "다른 브랜드");
            when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
            when(brandRepository.findByName("아디다스")).thenReturn(Optional.of(other));

            // when
            CoreException result = assertThrows(CoreException.class,
                () -> brandService.update(brandId, "아디다스", "변경 시도"));

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.CONFLICT);
        }
    }

    @DisplayName("브랜드 삭제")
    @Nested
    class Delete {

        @DisplayName("존재하는 브랜드를 soft delete 한다")
        @Test
        void softDeletesSuccessfully() {
            // given
            Long brandId = 1L;
            BrandModel brand = new BrandModel(new BrandName("나이키"), "스포츠 브랜드");
            when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
            when(productRepository.findAllByBrandId(brandId)).thenReturn(List.of());

            // when
            brandService.delete(brandId);

            // then
            assertThat(brand.getDeletedAt()).isNotNull();
        }

        @DisplayName("삭제 시 소속 상품도 연쇄 soft delete 한다")
        @Test
        void cascadeSoftDeletesProducts() {
            // given
            Long brandId = 1L;
            BrandModel brand = new BrandModel(new BrandName("나이키"), "스포츠 브랜드");
            ProductModel product1 = new ProductModel("에어맥스 90", "러닝화", new Money(129000), brandId);
            ProductModel product2 = new ProductModel("에어맥스 95", "러닝화", new Money(159000), brandId);
            when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
            when(productRepository.findAllByBrandId(brandId)).thenReturn(List.of(product1, product2));

            // when
            brandService.delete(brandId);

            // then
            assertAll(
                () -> assertThat(brand.getDeletedAt()).isNotNull(),
                () -> assertThat(product1.getDeletedAt()).isNotNull(),
                () -> assertThat(product2.getDeletedAt()).isNotNull()
            );
        }

        @DisplayName("미존재 브랜드면 NOT_FOUND 예외를 던진다")
        @Test
        void throwsWhenNotFound() {
            // given
            Long brandId = 999L;
            when(brandRepository.findById(brandId)).thenReturn(Optional.empty());

            // when
            CoreException result = assertThrows(CoreException.class,
                () -> brandService.delete(brandId));

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }

    @DisplayName("브랜드 목록 조회")
    @Nested
    class GetAll {

        @DisplayName("페이징된 결과를 반환한다")
        @Test
        void returnsPagedResult() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            List<BrandModel> brands = List.of(
                new BrandModel(new BrandName("나이키"), "스포츠"),
                new BrandModel(new BrandName("아디다스"), "스포츠")
            );
            Page<BrandModel> page = new PageImpl<>(brands, pageable, brands.size());
            when(brandRepository.findAll(pageable)).thenReturn(page);

            // when
            Page<BrandModel> result = brandService.getAll(pageable);

            // then
            assertAll(
                () -> assertThat(result.getTotalElements()).isEqualTo(2),
                () -> assertThat(result.getContent()).hasSize(2)
            );
        }
    }
}

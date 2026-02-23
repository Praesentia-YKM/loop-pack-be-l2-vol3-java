package com.loopers.application.brand;

import com.loopers.domain.brand.BrandModel;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.product.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class BrandFacadeTest {

    @InjectMocks
    private BrandFacade brandFacade;

    @Mock
    private BrandService brandService;

    @Mock
    private ProductService productService;

    @DisplayName("브랜드 등록")
    @Nested
    class Register {

        @DisplayName("정상적으로 등록하고 BrandInfo를 반환한다")
        @Test
        void registersAndReturnsInfo() {
            // arrange
            BrandModel brand = new BrandModel("나이키", "스포츠");
            given(brandService.register("나이키", "스포츠")).willReturn(brand);
            // act
            BrandInfo result = brandFacade.register("나이키", "스포츠");
            // assert
            assertAll(
                () -> assertThat(result.name()).isEqualTo("나이키"),
                () -> assertThat(result.description()).isEqualTo("스포츠")
            );
        }
    }

    @DisplayName("브랜드 삭제")
    @Nested
    class Delete {

        @DisplayName("브랜드 삭제 시 해당 브랜드의 상품도 함께 삭제한다")
        @Test
        void deletesWithCascade() {
            // arrange
            Long brandId = 1L;
            // act
            brandFacade.delete(brandId);
            // assert
            then(brandService).should().delete(brandId);
            then(productService).should().softDeleteByBrandId(brandId);
        }
    }
}

package com.loopers.interfaces.api.product;

import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.product.ProductSortType;
import com.loopers.domain.stock.StockService;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductV1Controller {

    private final ProductService productService;
    private final StockService stockService;

    @GetMapping
    public ApiResponse<Page<ProductV1Dto.ProductResponse>> getProducts(
        @RequestParam(required = false) Long brandId,
        @RequestParam(defaultValue = "LATEST") ProductSortType sort,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Page<ProductModel> products = productService.getProducts(brandId, sort, PageRequest.of(page, size));
        Page<ProductV1Dto.ProductResponse> response = products.map(product -> {
            String brandName = productService.getBrandName(product.brandId());
            var stockStatus = stockService.getByProductId(product.getId()).toStatus();
            return ProductV1Dto.ProductResponse.from(product, brandName, stockStatus);
        });
        return ApiResponse.success(response);
    }

    @GetMapping("/{productId}")
    public ApiResponse<ProductV1Dto.ProductResponse> getProduct(@PathVariable Long productId) {
        ProductModel product = productService.getProduct(productId);
        String brandName = productService.getBrandName(product.brandId());
        var stockStatus = stockService.getByProductId(product.getId()).toStatus();
        return ApiResponse.success(ProductV1Dto.ProductResponse.from(product, brandName, stockStatus));
    }
}

package com.loopers.interfaces.api.product.admin;

import com.loopers.domain.product.Money;
import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.stock.StockModel;
import com.loopers.domain.stock.StockService;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api-admin/v1/products")
public class ProductAdminV1Controller {

    private final ProductService productService;
    private final StockService stockService;

    @PostMapping
    public ApiResponse<ProductAdminV1Dto.ProductResponse> create(
        @RequestBody ProductAdminV1Dto.CreateRequest request
    ) {
        ProductModel product = productService.register(
            request.name(), request.description(), new Money(request.price()),
            request.brandId(), request.initialStock()
        );
        String brandName = productService.getBrandName(product.brandId());
        StockModel stock = stockService.getByProductId(product.getId());
        return ApiResponse.success(ProductAdminV1Dto.ProductResponse.from(product, brandName, stock.quantity()));
    }

    @GetMapping
    public ApiResponse<Page<ProductAdminV1Dto.ProductResponse>> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) Long brandId
    ) {
        Page<ProductModel> result = productService.getProductsForAdmin(brandId, PageRequest.of(page, size));
        Page<ProductAdminV1Dto.ProductResponse> response = result.map(product -> {
            String brandName = productService.getBrandName(product.brandId());
            StockModel stock = stockService.getByProductId(product.getId());
            return ProductAdminV1Dto.ProductResponse.from(product, brandName, stock.quantity());
        });
        return ApiResponse.success(response);
    }

    @GetMapping("/{productId}")
    public ApiResponse<ProductAdminV1Dto.ProductResponse> getProduct(@PathVariable Long productId) {
        ProductModel product = productService.getProductForAdmin(productId);
        String brandName = productService.getBrandName(product.brandId());
        StockModel stock = stockService.getByProductId(product.getId());
        return ApiResponse.success(ProductAdminV1Dto.ProductResponse.from(product, brandName, stock.quantity()));
    }

    @PutMapping("/{productId}")
    public ApiResponse<ProductAdminV1Dto.ProductResponse> update(
        @PathVariable Long productId,
        @RequestBody ProductAdminV1Dto.UpdateRequest request
    ) {
        ProductModel product = productService.update(productId, request.name(), request.description(), new Money(request.price()));
        String brandName = productService.getBrandName(product.brandId());
        StockModel stock = stockService.getByProductId(product.getId());
        return ApiResponse.success(ProductAdminV1Dto.ProductResponse.from(product, brandName, stock.quantity()));
    }

    @DeleteMapping("/{productId}")
    public ApiResponse<Object> delete(@PathVariable Long productId) {
        productService.delete(productId);
        return ApiResponse.success();
    }
}

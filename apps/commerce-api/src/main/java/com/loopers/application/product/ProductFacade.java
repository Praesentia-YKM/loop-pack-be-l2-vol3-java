package com.loopers.application.product;

import com.loopers.domain.brand.BrandModel;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.product.Money;
import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.product.ProductSortType;
import com.loopers.domain.stock.StockModel;
import com.loopers.domain.stock.StockService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class ProductFacade {

    private final ProductService productService;
    private final BrandService brandService;
    private final StockService stockService;

    @Transactional
    public ProductInfo.AdminDetail register(String name, String description, Money price, Long brandId, int stockQuantity) {
        BrandModel brand = brandService.getById(brandId);
        if (brand.getDeletedAt() != null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "삭제된 브랜드에는 상품을 등록할 수 없습니다.");
        }
        ProductModel product = productService.register(name, description, price, brandId);
        StockModel stock = stockService.save(product.getId(), stockQuantity);
        return ProductInfo.AdminDetail.from(product, brand, stock);
    }

    public Page<ProductInfo.Summary> getAllForCustomer(Pageable pageable, ProductSortType sortType) {
        Page<ProductModel> products = productService.getAll(pageable, sortType);
        return products.map(product -> {
            BrandModel brand = brandService.getById(product.getBrandId());
            StockModel stock = stockService.getByProductId(product.getId());
            return ProductInfo.Summary.from(product, brand, stock);
        });
    }

    public ProductInfo.Detail getDetailForCustomer(Long id) {
        ProductModel product = productService.getById(id);
        BrandModel brand = brandService.getById(product.getBrandId());
        StockModel stock = stockService.getByProductId(product.getId());
        return ProductInfo.Detail.from(product, brand, stock);
    }

    public Page<ProductInfo.AdminSummary> getAllForAdmin(Pageable pageable, ProductSortType sortType) {
        Page<ProductModel> products = productService.getAll(pageable, sortType);
        return products.map(product -> {
            BrandModel brand = brandService.getById(product.getBrandId());
            StockModel stock = stockService.getByProductId(product.getId());
            return ProductInfo.AdminSummary.from(product, brand, stock);
        });
    }

    public ProductInfo.AdminDetail getDetailForAdmin(Long id) {
        ProductModel product = productService.getById(id);
        BrandModel brand = brandService.getById(product.getBrandId());
        StockModel stock = stockService.getByProductId(product.getId());
        return ProductInfo.AdminDetail.from(product, brand, stock);
    }

    @Transactional
    public ProductInfo.AdminDetail update(Long id, String name, String description, Money price) {
        ProductModel product = productService.update(id, name, description, price);
        BrandModel brand = brandService.getById(product.getBrandId());
        StockModel stock = stockService.getByProductId(product.getId());
        return ProductInfo.AdminDetail.from(product, brand, stock);
    }

    @Transactional
    public void delete(Long id) {
        productService.delete(id);
    }

    @Transactional
    public ProductInfo.AdminDetail updateStock(Long productId, int quantity) {
        ProductModel product = productService.getById(productId);
        StockModel stock = stockService.getByProductId(productId);
        stock.update(quantity);
        BrandModel brand = brandService.getById(product.getBrandId());
        return ProductInfo.AdminDetail.from(product, brand, stock);
    }
}

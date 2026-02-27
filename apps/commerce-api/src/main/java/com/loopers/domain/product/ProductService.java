package com.loopers.domain.product;

import com.loopers.domain.brand.BrandModel;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.stock.StockService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final StockService stockService;

    @Transactional
    public ProductModel register(String name, String description, Money price, Long brandId, int initialStock) {
        BrandModel brand = brandRepository.findById(brandId)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "브랜드를 찾을 수 없습니다."));

        if (brand.getDeletedAt() != null) {
            throw new CoreException(ErrorType.NOT_FOUND, "삭제된 브랜드에 상품을 등록할 수 없습니다.");
        }

        ProductModel product = new ProductModel(name, description, price, brandId);
        ProductModel saved = productRepository.save(product);

        stockService.create(saved.getId(), initialStock);

        return saved;
    }

    @Transactional(readOnly = true)
    public ProductModel getProduct(Long productId) {
        ProductModel product = findById(productId);
        if (product.getDeletedAt() != null) {
            throw new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다.");
        }
        return product;
    }

    @Transactional(readOnly = true)
    public ProductModel getProductForAdmin(Long productId) {
        return findById(productId);
    }

    @Transactional(readOnly = true)
    public Page<ProductModel> getProducts(Long brandId, ProductSortType sortType, Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortType.toSort());
        if (brandId != null) {
            return productRepository.findAllByBrandIdAndDeletedAtIsNull(brandId, sortedPageable);
        }
        return productRepository.findAllByDeletedAtIsNull(sortedPageable);
    }

    @Transactional(readOnly = true)
    public Page<ProductModel> getProductsForAdmin(Long brandId, Pageable pageable) {
        if (brandId != null) {
            return productRepository.findAllByBrandId(brandId, pageable);
        }
        return productRepository.findAll(pageable);
    }

    @Transactional
    public ProductModel update(Long productId, String name, String description, Money price) {
        ProductModel product = findById(productId);
        product.update(name, description, price);
        return product;
    }

    @Transactional
    public void delete(Long productId) {
        ProductModel product = findById(productId);
        product.delete();
    }

    @Transactional
    public void deleteAllByBrandId(Long brandId) {
        List<ProductModel> products = productRepository.findAllByBrandId(brandId);
        products.forEach(ProductModel::delete);
    }

    @Transactional(readOnly = true)
    public Map<Long, ProductModel> getProductsByIds(List<Long> productIds) {
        return productRepository.findAllByIdInAndDeletedAtIsNull(productIds)
            .stream()
            .collect(Collectors.toMap(ProductModel::getId, Function.identity()));
    }

    @Transactional(readOnly = true)
    public Map<Long, String> getBrandNamesByIds(List<Long> brandIds) {
        return brandRepository.findAllByIdIn(brandIds)
            .stream()
            .collect(Collectors.toMap(BrandModel::getId, brand -> brand.name().value()));
    }

    public String getBrandName(Long brandId) {
        return brandRepository.findById(brandId)
            .map(brand -> brand.name().value())
            .orElse(null);
    }

    private ProductModel findById(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다."));
    }
}

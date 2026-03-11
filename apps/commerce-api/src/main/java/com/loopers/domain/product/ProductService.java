package com.loopers.domain.product;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductModel register(String name, String description, Money price, Long brandId) {
        return productRepository.save(new ProductModel(name, description, price, brandId));
    }

    @Transactional(readOnly = true)
    public ProductModel getById(Long id) {
        ProductModel product = productRepository.findById(id)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다. [id = " + id + "]"));
        if (product.getDeletedAt() != null) {
            throw new CoreException(ErrorType.NOT_FOUND, "삭제된 상품입니다. [id = " + id + "]");
        }
        return product;
    }

    @Transactional(readOnly = true)
    public Page<ProductModel> getAll(Pageable pageable, ProductSortType sortType) {
        return productRepository.findAll(pageable, sortType);
    }

    @Transactional
    public ProductModel update(Long id, String name, String description, Money price) {
        ProductModel product = getById(id);
        product.update(name, description, price);
        return product;
    }

    @Transactional
    public void delete(Long id) {
        ProductModel product = getById(id);
        product.delete();
    }

    @Transactional
    public void softDeleteByBrandId(Long brandId) {
        List<ProductModel> products = productRepository.findAllByBrandId(brandId);
        products.forEach(ProductModel::delete);
    }

    @Transactional
    public void increaseLikeCount(Long productId) {
        ProductModel product = getById(productId);
        product.increaseLikeCount();
    }

    @Transactional
    public void decreaseLikeCount(Long productId) {
        ProductModel product = getById(productId);
        product.decreaseLikeCount();
    }
}

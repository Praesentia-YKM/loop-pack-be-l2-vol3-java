package com.loopers.domain.product;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "product")
public class ProductModel extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Embedded
    private Money price;

    @Column(name = "brand_id", nullable = false)
    private Long brandId;

    @Column(name = "like_count", nullable = false)
    private int likeCount;

    protected ProductModel() {}

    public ProductModel(String name, String description, Money price, Long brandId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.brandId = brandId;
        this.likeCount = 0;
    }

    public void update(String name, String description, Money price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public Money price() {
        return price;
    }

    public Long brandId() {
        return brandId;
    }

    public int likeCount() {
        return likeCount;
    }
}

package com.loopers.domain.brand;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "brand")
public class BrandModel extends BaseEntity {

    @Embedded
    private BrandName name;

    @Column(name = "description")
    private String description;

    protected BrandModel() {}

    public BrandModel(BrandName name, String description) {
        this.name = name;
        this.description = description;
    }

    public void update(BrandName name, String description) {
        this.name = name;
        this.description = description;
    }

    public BrandName name() {
        return name;
    }

    public String description() {
        return description;
    }
}

package com.loopers.domain.stock;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "stock")
public class StockModel extends BaseEntity {

    @Column(name = "product_id", nullable = false, unique = true)
    private Long productId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    protected StockModel() {}

    public StockModel(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public void decrease(int amount) {
        if (this.quantity < amount) {
            throw new CoreException(ErrorType.BAD_REQUEST, "재고가 부족합니다.");
        }
        this.quantity -= amount;
    }

    public void increase(int amount) {
        this.quantity += amount;
    }

    public boolean hasEnough(int amount) {
        return this.quantity >= amount;
    }

    public StockStatus toStatus() {
        return StockStatus.from(this.quantity);
    }

    public Long productId() {
        return productId;
    }

    public int quantity() {
        return quantity;
    }
}

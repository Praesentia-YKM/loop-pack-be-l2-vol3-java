package com.loopers.domain.order;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.product.Money;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_item")
public class OrderItemModel extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderModel order;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "product_price", nullable = false))
    private Money productPrice;

    @Column(nullable = false)
    private int quantity;

    protected OrderItemModel() {}

    public OrderItemModel(Long productId, String productName, Money productPrice, int quantity) {
        if (quantity < 1) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주문 수량은 1 이상이어야 합니다.");
        }
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
    }

    void setOrder(OrderModel order) {
        this.order = order;
    }

    public OrderModel getOrder() { return order; }
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public Money getProductPrice() { return productPrice; }
    public int getQuantity() { return quantity; }

    public Money getSubtotal() {
        return productPrice.multiply(quantity);
    }
}

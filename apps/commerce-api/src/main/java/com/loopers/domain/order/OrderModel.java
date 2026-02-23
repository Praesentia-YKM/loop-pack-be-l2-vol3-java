package com.loopers.domain.order;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.product.Money;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "orders")
public class OrderModel extends BaseEntity {

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "total_amount", nullable = false))
    private Money totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemModel> orderItems = new ArrayList<>();

    protected OrderModel() {}

    public OrderModel(Long memberId) {
        this.memberId = memberId;
        this.totalAmount = new Money(0);
        this.status = OrderStatus.CREATED;
    }

    public Long getMemberId() { return memberId; }
    public Money getTotalAmount() { return totalAmount; }
    public OrderStatus getStatus() { return status; }
    public List<OrderItemModel> getOrderItems() { return Collections.unmodifiableList(orderItems); }

    public void addOrderItem(OrderItemModel item) {
        item.setOrder(this);
        this.orderItems.add(item);
    }

    public void calculateTotalAmount() {
        Money total = new Money(0);
        for (OrderItemModel item : orderItems) {
            total = total.add(item.getSubtotal());
        }
        this.totalAmount = total;
    }
}

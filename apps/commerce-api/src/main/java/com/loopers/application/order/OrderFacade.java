package com.loopers.application.order;

import com.loopers.domain.order.OrderItemModel;
import com.loopers.domain.order.OrderModel;
import com.loopers.domain.product.Money;
import com.loopers.domain.product.ProductModel;
import com.loopers.application.product.ProductService;
import com.loopers.application.stock.StockService;
import com.loopers.domain.stock.StockModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class OrderFacade {

    private final OrderService orderService;
    private final ProductService productService;
    private final StockService stockService;

    @Transactional
    public OrderResult placeOrder(Long userId, List<OrderItemCommand> commands) {
        Money totalAmount = Money.ZERO;
        List<SnapshotHolder> snapshots = new ArrayList<>();

        for (OrderItemCommand cmd : commands) {
            ProductModel product = productService.getProduct(cmd.productId());

            StockModel stock = stockService.getByProductId(cmd.productId());
            stock.decrease(cmd.quantity());

            Money subtotal = product.price().multiply(cmd.quantity());
            totalAmount = totalAmount.add(subtotal);

            snapshots.add(new SnapshotHolder(
                product.getId(), product.name(), product.price(), cmd.quantity()
            ));
        }

        OrderModel order = orderService.save(new OrderModel(userId, totalAmount));

        List<OrderItemModel> items = snapshots.stream()
            .map(s -> new OrderItemModel(
                order.getId(), s.productId(), s.productName(), s.productPrice(), s.quantity()
            ))
            .toList();

        List<OrderItemModel> savedItems = orderService.saveAllItems(items);

        return OrderResult.of(order, savedItems);
    }

    @Transactional(readOnly = true)
    public OrderInfo getOrder(Long orderId, Long userId) {
        OrderModel order = orderService.getOrder(orderId, userId);
        List<OrderItemModel> items = orderService.getOrderItems(orderId);
        return OrderInfo.from(order, items);
    }

    @Transactional(readOnly = true)
    public List<OrderInfo> getOrdersByUser(Long userId, ZonedDateTime startAt, ZonedDateTime endAt) {
        List<OrderModel> orders = orderService.getOrdersByUser(userId, startAt, endAt);
        return orders.stream().map(OrderInfo::summaryFrom).toList();
    }

    @Transactional(readOnly = true)
    public OrderInfo getOrderForAdmin(Long orderId) {
        OrderModel order = orderService.getOrderForAdmin(orderId);
        List<OrderItemModel> items = orderService.getOrderItems(orderId);
        return OrderInfo.from(order, items);
    }

    @Transactional(readOnly = true)
    public Page<OrderInfo> getAllForAdmin(Pageable pageable) {
        Page<OrderModel> orders = orderService.getAllForAdmin(pageable);
        return orders.map(OrderInfo::summaryFrom);
    }

    private record SnapshotHolder(
        Long productId, String productName, Money productPrice, int quantity
    ) {}
}

package com.loopers.application.order;

import com.loopers.domain.order.OrderItemModel;
import com.loopers.domain.order.OrderModel;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.stock.StockModel;
import com.loopers.domain.stock.StockService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class OrderFacade {

    private final OrderService orderService;
    private final ProductService productService;
    private final StockService stockService;

    @Transactional
    public OrderInfo.Detail createOrder(Long memberId, List<OrderItemCommand> commands) {
        List<ProductModel> products = new ArrayList<>();
        List<StockModel> stocks = new ArrayList<>();

        // 1. 모든 상품 존재/삭제 확인
        for (OrderItemCommand command : commands) {
            ProductModel product = productService.getById(command.productId());
            if (product.getDeletedAt() != null) {
                throw new CoreException(ErrorType.NOT_FOUND, "삭제된 상품이 포함되어 있습니다: " + product.getName());
            }
            products.add(product);
        }

        // 2. 모든 재고 확인 + 차감
        for (int i = 0; i < commands.size(); i++) {
            StockModel stock = stockService.getByProductId(commands.get(i).productId());
            if (!stock.hasEnough(commands.get(i).quantity())) {
                throw new CoreException(ErrorType.BAD_REQUEST, "재고가 부족합니다: " + products.get(i).getName());
            }
            stock.decrease(commands.get(i).quantity());
            stocks.add(stock);
        }

        // 3. 스냅샷 생성 + 주문 저장
        OrderModel order = new OrderModel(memberId);
        for (int i = 0; i < commands.size(); i++) {
            ProductModel product = products.get(i);
            OrderItemModel item = new OrderItemModel(
                product.getId(),
                product.getName(),
                product.getPrice(),
                commands.get(i).quantity()
            );
            order.addOrderItem(item);
        }

        // 4. 총액 계산 + 저장
        order.calculateTotalAmount();
        OrderModel saved = orderService.save(order);
        return OrderInfo.Detail.from(saved);
    }

    public OrderInfo.Detail getById(Long orderId) {
        OrderModel order = orderService.getById(orderId);
        return OrderInfo.Detail.from(order);
    }

    public Page<OrderInfo.Summary> getMyOrders(Long memberId, Pageable pageable) {
        return orderService.getByMemberId(memberId, pageable).map(OrderInfo.Summary::from);
    }

    public Page<OrderInfo.Summary> getAllOrders(Pageable pageable) {
        return orderService.getAll(pageable).map(OrderInfo.Summary::from);
    }

    public OrderInfo.Detail getDetailForAdmin(Long orderId) {
        return getById(orderId);
    }
}

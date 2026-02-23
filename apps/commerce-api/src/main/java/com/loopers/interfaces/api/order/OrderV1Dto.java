package com.loopers.interfaces.api.order;

import com.loopers.application.order.OrderInfo;

import java.time.ZonedDateTime;
import java.util.List;

public class OrderV1Dto {

    public record OrderItemRequest(Long productId, int quantity) {}

    public record CreateOrderRequest(List<OrderItemRequest> items) {}

    public record OrderSummaryResponse(
        Long id, int totalAmount, String status, int itemCount, ZonedDateTime createdAt
    ) {
        public static OrderSummaryResponse from(OrderInfo.Summary info) {
            return new OrderSummaryResponse(
                info.id(), info.totalAmount(), info.status(), info.itemCount(), info.createdAt()
            );
        }
    }

    public record OrderDetailResponse(
        Long id, Long memberId, int totalAmount, String status,
        List<OrderItemResponse> orderItems, ZonedDateTime createdAt
    ) {
        public static OrderDetailResponse from(OrderInfo.Detail info) {
            List<OrderItemResponse> items = info.orderItems().stream()
                .map(OrderItemResponse::from)
                .toList();
            return new OrderDetailResponse(
                info.id(), info.memberId(), info.totalAmount(), info.status(), items, info.createdAt()
            );
        }
    }

    public record OrderItemResponse(
        Long id, Long productId, String productName, int productPrice, int quantity, int subtotal
    ) {
        public static OrderItemResponse from(OrderInfo.OrderItemInfo info) {
            return new OrderItemResponse(
                info.id(), info.productId(), info.productName(),
                info.productPrice(), info.quantity(), info.subtotal()
            );
        }
    }
}

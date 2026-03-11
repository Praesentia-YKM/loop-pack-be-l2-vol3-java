package com.loopers.interfaces.api.order;

import com.loopers.application.order.OrderFacade;
import com.loopers.application.order.OrderInfo;
import com.loopers.application.order.OrderItemCommand;
import com.loopers.domain.member.MemberModel;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.auth.LoginMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
public class OrderV1Controller implements OrderV1ApiSpec {

    private final OrderFacade orderFacade;

    @PostMapping
    @Override
    public ApiResponse<OrderV1Dto.OrderDetailResponse> createOrder(
        @LoginMember MemberModel member,
        @RequestBody OrderV1Dto.CreateOrderRequest request
    ) {
        List<OrderItemCommand> commands = request.items().stream()
            .map(item -> new OrderItemCommand(item.productId(), item.quantity()))
            .toList();
        OrderInfo.Detail info = orderFacade.createOrder(member.getId(), commands);
        return ApiResponse.success(OrderV1Dto.OrderDetailResponse.from(info));
    }

    @GetMapping
    @Override
    public ApiResponse<Page<OrderV1Dto.OrderSummaryResponse>> getMyOrders(
        @LoginMember MemberModel member,
        Pageable pageable
    ) {
        Page<OrderV1Dto.OrderSummaryResponse> response = orderFacade.getMyOrders(member.getId(), pageable)
            .map(OrderV1Dto.OrderSummaryResponse::from);
        return ApiResponse.success(response);
    }

    @GetMapping("/{orderId}")
    @Override
    public ApiResponse<OrderV1Dto.OrderDetailResponse> getById(
        @LoginMember MemberModel member,
        @PathVariable(value = "orderId") Long orderId
    ) {
        OrderInfo.Detail info = orderFacade.getById(orderId);
        return ApiResponse.success(OrderV1Dto.OrderDetailResponse.from(info));
    }
}

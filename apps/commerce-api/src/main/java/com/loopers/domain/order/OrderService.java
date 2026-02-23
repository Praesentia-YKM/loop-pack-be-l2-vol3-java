package com.loopers.domain.order;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public OrderModel save(OrderModel order) {
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public OrderModel getById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "주문을 찾을 수 없습니다. [id = " + id + "]"));
    }

    @Transactional(readOnly = true)
    public Page<OrderModel> getByMemberId(Long memberId, Pageable pageable) {
        return orderRepository.findByMemberId(memberId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<OrderModel> getAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }
}

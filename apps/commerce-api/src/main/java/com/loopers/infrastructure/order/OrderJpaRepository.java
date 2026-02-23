package com.loopers.infrastructure.order;

import com.loopers.domain.order.OrderModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<OrderModel, Long> {
    Page<OrderModel> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);
    Page<OrderModel> findAllByOrderByCreatedAtDesc(Pageable pageable);
}

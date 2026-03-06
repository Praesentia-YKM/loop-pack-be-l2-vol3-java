package com.loopers.infrastructure.coupon;

import com.loopers.domain.coupon.CouponIssueModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CouponIssueJpaRepository extends JpaRepository<CouponIssueModel, Long> {

    List<CouponIssueModel> findAllByUserId(Long userId);

    Page<CouponIssueModel> findAllByCouponId(Long couponId, Pageable pageable);

    Optional<CouponIssueModel> findByUserIdAndCouponId(Long userId, Long couponId);
}

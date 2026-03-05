package com.loopers.infrastructure.coupon;

import com.loopers.domain.coupon.CouponIssueModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CouponIssueJpaRepository extends JpaRepository<CouponIssueModel, Long> {

    List<CouponIssueModel> findAllByUserId(Long userId);

    List<CouponIssueModel> findAllByCouponId(Long couponId);

    Optional<CouponIssueModel> findByUserIdAndCouponId(Long userId, Long couponId);
}

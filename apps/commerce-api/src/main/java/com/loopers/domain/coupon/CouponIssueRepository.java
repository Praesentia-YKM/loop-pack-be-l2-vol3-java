package com.loopers.domain.coupon;

import java.util.List;
import java.util.Optional;

public interface CouponIssueRepository {

    CouponIssueModel save(CouponIssueModel couponIssue);

    Optional<CouponIssueModel> findById(Long id);

    Optional<CouponIssueModel> findByIdForUpdate(Long id);

    List<CouponIssueModel> findAllByUserId(Long userId);

    List<CouponIssueModel> findAllByCouponId(Long couponId);

    Optional<CouponIssueModel> findByUserIdAndCouponId(Long userId, Long couponId);
}

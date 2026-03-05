package com.loopers.domain.coupon;

import java.util.Optional;

public interface CouponRepository {

    CouponModel save(CouponModel coupon);

    Optional<CouponModel> findById(Long id);

    java.util.List<CouponModel> findAll();

    void delete(CouponModel coupon);
}

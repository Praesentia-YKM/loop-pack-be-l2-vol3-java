package com.loopers.infrastructure.coupon;

import com.loopers.domain.coupon.CouponModel;
import com.loopers.domain.coupon.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponJpaRepository couponJpaRepository;

    @Override
    public CouponModel save(CouponModel coupon) {
        return couponJpaRepository.save(coupon);
    }

    @Override
    public Optional<CouponModel> findById(Long id) {
        return couponJpaRepository.findById(id);
    }

    @Override
    public List<CouponModel> findAll() {
        return couponJpaRepository.findAll();
    }

    @Override
    public void delete(CouponModel coupon) {
        couponJpaRepository.delete(coupon);
    }
}

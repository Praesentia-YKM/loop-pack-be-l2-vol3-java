package com.loopers.interfaces.api.coupon;

import com.loopers.application.coupon.CouponIssueService;
import com.loopers.application.coupon.CouponService;
import com.loopers.domain.coupon.CouponIssueModel;
import com.loopers.domain.coupon.CouponModel;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CouponAdminV1Controller {

    private final CouponService couponService;
    private final CouponIssueService couponIssueService;

    @GetMapping("/api-admin/v1/coupons")
    public ApiResponse<List<CouponAdminV1Dto.CouponResponse>> getCoupons() {
        List<CouponModel> coupons = couponService.getAllCoupons();
        List<CouponAdminV1Dto.CouponResponse> response = coupons.stream()
            .map(CouponAdminV1Dto.CouponResponse::from)
            .toList();
        return ApiResponse.success(response);
    }

    @GetMapping("/api-admin/v1/coupons/{couponId}")
    public ApiResponse<CouponAdminV1Dto.CouponResponse> getCoupon(@PathVariable Long couponId) {
        CouponModel coupon = couponService.getCoupon(couponId);
        return ApiResponse.success(CouponAdminV1Dto.CouponResponse.from(coupon));
    }

    @PostMapping("/api-admin/v1/coupons")
    public ApiResponse<CouponAdminV1Dto.CouponResponse> createCoupon(
        @RequestBody CouponAdminV1Dto.CreateRequest request
    ) {
        CouponModel coupon = couponService.create(
            request.name(), request.type(), request.value(),
            request.toMinOrderAmount(), request.expiredAt()
        );
        return ApiResponse.success(CouponAdminV1Dto.CouponResponse.from(coupon));
    }

    @PutMapping("/api-admin/v1/coupons/{couponId}")
    public ApiResponse<CouponAdminV1Dto.CouponResponse> updateCoupon(
        @PathVariable Long couponId,
        @RequestBody CouponAdminV1Dto.UpdateRequest request
    ) {
        CouponModel coupon = couponService.update(
            couponId, request.name(), request.type(), request.value(),
            request.toMinOrderAmount(), request.expiredAt()
        );
        return ApiResponse.success(CouponAdminV1Dto.CouponResponse.from(coupon));
    }

    @DeleteMapping("/api-admin/v1/coupons/{couponId}")
    public ApiResponse<Void> deleteCoupon(@PathVariable Long couponId) {
        couponService.delete(couponId);
        return ApiResponse.success(null);
    }

    @GetMapping("/api-admin/v1/coupons/{couponId}/issues")
    public ApiResponse<List<CouponV1Dto.CouponIssueResponse>> getCouponIssues(
        @PathVariable Long couponId
    ) {
        List<CouponIssueModel> issues = couponIssueService.getIssuesByCoupon(couponId);
        List<CouponV1Dto.CouponIssueResponse> response = issues.stream()
            .map(CouponV1Dto.CouponIssueResponse::from)
            .toList();
        return ApiResponse.success(response);
    }
}

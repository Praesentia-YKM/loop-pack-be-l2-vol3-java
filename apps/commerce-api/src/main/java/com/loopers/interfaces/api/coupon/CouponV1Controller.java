package com.loopers.interfaces.api.coupon;

import com.loopers.application.coupon.CouponIssueService;
import com.loopers.application.coupon.CouponService;
import com.loopers.application.member.MemberFacade;
import com.loopers.domain.coupon.CouponIssueModel;
import com.loopers.domain.coupon.CouponModel;
import com.loopers.domain.member.MemberModel;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CouponV1Controller {

    private final CouponService couponService;
    private final CouponIssueService couponIssueService;
    private final MemberFacade memberFacade;

    @PostMapping("/api/v1/coupons/{couponId}/issue")
    public ApiResponse<CouponV1Dto.CouponIssueResponse> issueCoupon(
        @PathVariable Long couponId,
        @RequestHeader("X-Loopers-LoginId") String loginId,
        @RequestHeader("X-Loopers-LoginPw") String password
    ) {
        MemberModel member = memberFacade.authenticate(loginId, password);
        CouponModel coupon = couponService.getCoupon(couponId);
        CouponIssueModel issue = couponIssueService.issue(coupon, member.getId());
        return ApiResponse.success(CouponV1Dto.CouponIssueResponse.from(issue));
    }

    @GetMapping("/api/v1/users/me/coupons")
    public ApiResponse<List<CouponV1Dto.CouponIssueResponse>> getMyCoupons(
        @RequestHeader("X-Loopers-LoginId") String loginId,
        @RequestHeader("X-Loopers-LoginPw") String password
    ) {
        MemberModel member = memberFacade.authenticate(loginId, password);
        List<CouponIssueModel> issues = couponIssueService.getMyIssues(member.getId());
        List<CouponV1Dto.CouponIssueResponse> response = issues.stream()
            .map(CouponV1Dto.CouponIssueResponse::from)
            .toList();
        return ApiResponse.success(response);
    }
}

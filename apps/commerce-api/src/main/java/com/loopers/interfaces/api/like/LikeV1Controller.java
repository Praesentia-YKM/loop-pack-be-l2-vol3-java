package com.loopers.interfaces.api.like;

import com.loopers.application.like.LikeFacade;
import com.loopers.domain.like.LikeModel;
import com.loopers.domain.member.MemberAuthService;
import com.loopers.domain.member.MemberModel;
import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.ProductService;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class LikeV1Controller {

    private final LikeFacade likeFacade;
    private final MemberAuthService memberAuthService;
    private final ProductService productService;

    @PostMapping("/api/v1/products/{productId}/likes")
    public ApiResponse<Void> like(
        @PathVariable Long productId,
        @RequestHeader("X-Loopers-LoginId") String loginId,
        @RequestHeader("X-Loopers-LoginPw") String loginPw
    ) {
        MemberModel member = memberAuthService.authenticate(loginId, loginPw);
        likeFacade.like(member.getId(), productId);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/api/v1/products/{productId}/likes")
    public ApiResponse<Void> unlike(
        @PathVariable Long productId,
        @RequestHeader("X-Loopers-LoginId") String loginId,
        @RequestHeader("X-Loopers-LoginPw") String loginPw
    ) {
        MemberModel member = memberAuthService.authenticate(loginId, loginPw);
        likeFacade.unlike(member.getId(), productId);
        return ApiResponse.success(null);
    }

    @GetMapping("/api/v1/users/{userId}/likes")
    public ApiResponse<Page<LikeV1Dto.LikeResponse>> getMyLikes(
        @PathVariable Long userId,
        @RequestHeader("X-Loopers-LoginId") String loginId,
        @RequestHeader("X-Loopers-LoginPw") String loginPw,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        MemberModel member = memberAuthService.authenticate(loginId, loginPw);
        Page<LikeModel> likes = likeFacade.getMyLikes(member.getId(),
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        Page<LikeV1Dto.LikeResponse> response = likes.map(like -> {
            ProductModel product = productService.getProduct(like.productId());
            return LikeV1Dto.LikeResponse.from(like, product);
        });
        return ApiResponse.success(response);
    }
}

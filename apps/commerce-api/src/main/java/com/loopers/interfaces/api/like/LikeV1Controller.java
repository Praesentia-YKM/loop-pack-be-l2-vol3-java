package com.loopers.interfaces.api.like;

import com.loopers.application.like.LikeFacade;
import com.loopers.domain.member.MemberModel;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.auth.LoginMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class LikeV1Controller implements LikeV1ApiSpec {

    private final LikeFacade likeFacade;

    @PostMapping("/api/v1/products/{productId}/likes")
    @Override
    public ApiResponse<Object> register(
        @LoginMember MemberModel member,
        @PathVariable(value = "productId") Long productId
    ) {
        likeFacade.register(member.getId(), productId);
        return ApiResponse.success();
    }

    @DeleteMapping("/api/v1/products/{productId}/likes")
    @Override
    public ApiResponse<Object> cancel(
        @LoginMember MemberModel member,
        @PathVariable(value = "productId") Long productId
    ) {
        likeFacade.cancel(member.getId(), productId);
        return ApiResponse.success();
    }

    @GetMapping("/api/v1/likes")
    @Override
    public ApiResponse<Page<LikeV1Dto.LikeResponse>> getMyLikes(
        @LoginMember MemberModel member,
        Pageable pageable
    ) {
        Page<LikeV1Dto.LikeResponse> response = likeFacade.getMyLikes(member.getId(), pageable)
            .map(LikeV1Dto.LikeResponse::from);
        return ApiResponse.success(response);
    }
}

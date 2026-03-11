package com.loopers.interfaces.api.like;

import com.loopers.application.like.LikeInfo;

import java.time.ZonedDateTime;

public class LikeV1Dto {

    public record LikeResponse(
        Long likeId, Long productId, String productName,
        int productPrice, String brandName, ZonedDateTime likedAt
    ) {
        public static LikeResponse from(LikeInfo info) {
            return new LikeResponse(
                info.likeId(), info.productId(), info.productName(),
                info.productPrice(), info.brandName(), info.likedAt()
            );
        }
    }
}

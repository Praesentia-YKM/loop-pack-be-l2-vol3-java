package com.loopers.domain.like;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface LikeRepository {
    Optional<LikeModel> findByMemberIdAndProductId(Long memberId, Long productId);
    Page<LikeModel> findAllByMemberId(Long memberId, Pageable pageable);
    LikeModel save(LikeModel like);
    void delete(LikeModel like);
}

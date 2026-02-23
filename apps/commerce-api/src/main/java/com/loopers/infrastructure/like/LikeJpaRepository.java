package com.loopers.infrastructure.like;

import com.loopers.domain.like.LikeModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeJpaRepository extends JpaRepository<LikeModel, Long> {
    Optional<LikeModel> findByMemberIdAndProductId(Long memberId, Long productId);
    Page<LikeModel> findAllByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);
}

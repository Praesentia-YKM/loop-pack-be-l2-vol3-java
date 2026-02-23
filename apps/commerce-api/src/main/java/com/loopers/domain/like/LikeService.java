package com.loopers.domain.like;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class LikeService {

    private final LikeRepository likeRepository;

    @Transactional
    public boolean register(Long memberId, Long productId) {
        if (likeRepository.findByMemberIdAndProductId(memberId, productId).isPresent()) {
            return false;
        }
        likeRepository.save(new LikeModel(memberId, productId));
        return true;
    }

    @Transactional
    public boolean cancel(Long memberId, Long productId) {
        return likeRepository.findByMemberIdAndProductId(memberId, productId)
            .map(like -> {
                likeRepository.delete(like);
                return true;
            })
            .orElse(false);
    }

    @Transactional(readOnly = true)
    public Page<LikeModel> getMyLikes(Long memberId, Pageable pageable) {
        return likeRepository.findAllByMemberId(memberId, pageable);
    }
}

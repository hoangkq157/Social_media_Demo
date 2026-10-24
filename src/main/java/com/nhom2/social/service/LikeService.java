package com.nhom2.social.service;

import com.nhom2.social.dto.LikeResponse;
import com.nhom2.social.entity.Post;
import com.nhom2.social.entity.PostLike;
import com.nhom2.social.entity.User;
import com.nhom2.social.exception.NotFoundException;
import com.nhom2.social.repository.PostLikeRepository;
import com.nhom2.social.repository.PostRepository;
import com.nhom2.social.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service xu ly luot thich (Like / Unlike) cho bai viet.
 */
@Service
@Transactional(readOnly = true)
public class LikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public LikeService(PostLikeRepository postLikeRepository,
                       PostRepository postRepository,
                       UserRepository userRepository) {
        this.postLikeRepository = postLikeRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    /**
     * Chuyen doi trang thai thich (neu da thich -> bo thich, chua thich -> thich).
     * Xu ly bat dong bo qua AJAX, dam bao idempotent va khong bi trung lap.
     */
    @Transactional
    public LikeResponse toggleLike(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bài viết #" + postId));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng @" + username));

        Optional<PostLike> existingLike = postLikeRepository.findByPostIdAndUserId(postId, user.getId());
        boolean liked;
        if (existingLike.isPresent()) {
            postLikeRepository.delete(existingLike.get());
            liked = false;
        } else {
            postLikeRepository.save(new PostLike(post, user));
            liked = true;
        }

        long count = postLikeRepository.countByPostId(postId);
        return new LikeResponse(liked, count);
    }

    public boolean isLikedByUser(Long postId, Long userId) {
        if (userId == null) {
            return false;
        }
        return postLikeRepository.existsByPostIdAndUserId(postId, userId);
    }

    public long countLikes(Long postId) {
        return postLikeRepository.countByPostId(postId);
    }
}

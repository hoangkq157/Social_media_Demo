package com.nhom2.social.repository;

import com.nhom2.social.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository quan ly luot thich (PostLike).
 */
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByPostIdAndUserId(Long postId, Long userId);

    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);

    long countByPostId(Long postId);

    void deleteByPostIdAndUserId(Long postId, Long userId);

    void deleteByPostId(Long postId);
}

package com.nhom2.social.repository;

import com.nhom2.social.entity.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository thao tac voi bang comments.
 * Fetch san 'user' de hien thi thong tin nguoi binh luan ma khong phat sinh N+1 query.
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = {"user"})
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);

    long countByPostId(Long postId);

    void deleteByPostId(Long postId);
}

package com.nhom2.social.repository;

import com.nhom2.social.entity.MediaType;
import com.nhom2.social.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repository thao tac voi bang posts.
 * Su dung @EntityGraph de eager fetch 'user', giai quyet triet de van de N+1 query.
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = {"user"})
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    Page<Post> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    Page<Post> findByUserIdAndMediaTypeOrderByCreatedAtDesc(Long userId, MediaType mediaType, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT p FROM Post p WHERE LOWER(p.content) LIKE LOWER(CONCAT('%', :q, '%')) ORDER BY p.createdAt DESC")
    Page<Post> searchPosts(@Param("q") String q, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    Optional<Post> findWithUserById(Long id);

    long countByUserId(Long userId);

    long countByUserIdAndMediaType(Long userId, MediaType mediaType);
}

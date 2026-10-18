package com.nhom2.social.repository;

import com.nhom2.social.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    /** Bang tin: bai moi nhat dung truoc. */
    List<Post> findAllByOrderByCreatedAtDesc();
}

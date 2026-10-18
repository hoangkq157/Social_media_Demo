package com.nhom2.social.service;

import com.nhom2.social.entity.Post;
import com.nhom2.social.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> getFeed(String q) {
        if (q == null || q.isBlank()) {
            return postRepository.findAllByOrderByCreatedAtDesc();
        }
        String key = q.trim();
        return postRepository
                .findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrderByCreatedAtDesc(key, key);
    }

    public Optional<Post> getPost(Long id) {
        return postRepository.findById(id);
    }

    @Transactional
    public Post save(Post post) {
        return postRepository.save(post);
    }
}

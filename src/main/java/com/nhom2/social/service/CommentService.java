package com.nhom2.social.service;

import com.nhom2.social.dto.CommentView;
import com.nhom2.social.entity.Comment;
import com.nhom2.social.entity.Post;
import com.nhom2.social.entity.User;
import com.nhom2.social.exception.NotFoundException;
import com.nhom2.social.repository.CommentRepository;
import com.nhom2.social.repository.PostRepository;
import com.nhom2.social.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service quan ly binh luan tren cac bai viet.
 */
@Service
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository,
                          PostRepository postRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    /**
     * Them binh luan moi vao bai viet va tra ve CommentView DTO de render DOM hoac JSON.
     */
    @Transactional
    public CommentView addComment(Long postId, String username, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Nội dung bình luận không được để trống");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bài viết #" + postId));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng @" + username));

        Comment comment = new Comment(post, user, content.trim());
        Comment saved = commentRepository.save(comment);

        return toCommentView(saved);
    }

    public List<CommentView> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(this::toCommentView)
                .toList();
    }

    public CommentView toCommentView(Comment c) {
        User u = c.getUser();
        return new CommentView(
                c.getId(),
                c.getPost().getId(),
                u.getUsername(),
                u.getFullName(),
                u.getAvatarPath(),
                u.getInitials(),
                u.getAvatarColor(),
                c.getContent(),
                c.getCreatedAtFormatted()
        );
    }
}

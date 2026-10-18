package com.nhom2.social.controller;

import com.nhom2.social.entity.Comment;
import com.nhom2.social.entity.Post;
import com.nhom2.social.entity.User;
import com.nhom2.social.service.CommentService;
import com.nhom2.social.service.PostService;
import com.nhom2.social.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CommentController {

    private final CommentService commentService;
    private final PostService postService;
    private final UserService userService;

    public CommentController(CommentService commentService,
                             PostService postService,
                             UserService userService) {
        this.commentService = commentService;
        this.postService = postService;
        this.userService = userService;
    }

    /** Tinh nang 3: Binh luan bai viet - luu vao bang comments roi ve lai bang tin. */
    @PostMapping("/comments/create")
    public String createComment(@RequestParam Long postId,
                                @RequestParam String content,
                                HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            return "redirect:/login";
        }
        Post post = postService.getPost(postId).orElse(null);
        if (post != null && content != null && !content.isBlank()) {
            User author = userService.findByUsername((String) session.getAttribute("username")).orElseThrow();
            commentService.save(new Comment(post, author, content.trim()));
        }
        return "redirect:/#post-" + postId;
    }
}

package com.nhom2.social.controller.api;

import com.nhom2.social.dto.CommentRequest;
import com.nhom2.social.dto.CommentView;
import com.nhom2.social.dto.LikeResponse;
import com.nhom2.social.service.CommentService;
import com.nhom2.social.service.LikeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API Controller phu trach cac thao tac AJAX:
 * - Like / Bo like (toggle) tra ve JSON {liked, likeCount}
 * - Gui binh luan inline tra ve CommentView JSON voi status 201 Created
 */
@RestController
@RequestMapping("/api/posts")
public class PostApiController {

    private final LikeService likeService;
    private final CommentService commentService;

    public PostApiController(LikeService likeService, CommentService commentService) {
        this.likeService = likeService;
        this.commentService = commentService;
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<LikeResponse> toggleLike(@AuthenticationPrincipal UserDetails principal,
                                                   @PathVariable("id") Long id) {
        String username = principal != null ? principal.getUsername() : "an";
        LikeResponse response = likeService.toggleLike(id, username);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentView> addComment(@AuthenticationPrincipal UserDetails principal,
                                                  @PathVariable("id") Long id,
                                                  @Valid @RequestBody CommentRequest request) {
        String username = principal != null ? principal.getUsername() : "an";
        CommentView commentView = commentService.addComment(id, username, request.content());
        return ResponseEntity.status(HttpStatus.CREATED).body(commentView);
    }
}

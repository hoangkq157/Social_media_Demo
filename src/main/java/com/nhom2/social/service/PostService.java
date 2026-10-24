package com.nhom2.social.service;

import com.nhom2.social.dto.CommentView;
import com.nhom2.social.dto.PostView;
import com.nhom2.social.entity.MediaType;
import com.nhom2.social.entity.Post;
import com.nhom2.social.entity.User;
import com.nhom2.social.exception.ForbiddenException;
import com.nhom2.social.exception.NotFoundException;
import com.nhom2.social.repository.CommentRepository;
import com.nhom2.social.repository.PostLikeRepository;
import com.nhom2.social.repository.PostRepository;
import com.nhom2.social.repository.UserRepository;
import com.nhom2.social.storage.FileStorageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service quan ly bai viet (Post):
 * - Dang bai kem dinh kem anh/video
 * - Phan trang bang tin, tranh N+1 query bang DTO projection va @EntityGraph
 * - Xoa bai viet va kiem tra quyen chu so huu
 */
@Service
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final FileStorageService fileStorageService;
    private final CommentService commentService;

    public PostService(PostRepository postRepository,
                       UserRepository userRepository,
                       CommentRepository commentRepository,
                       PostLikeRepository postLikeRepository,
                       FileStorageService fileStorageService,
                       CommentService commentService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.postLikeRepository = postLikeRepository;
        this.fileStorageService = fileStorageService;
        this.commentService = commentService;
    }

    /**
     * Tao bai viet moi.
     * Bat buoc: phai co noi dung text hoac co media dinh kem.
     */
    @Transactional
    public Long createPost(String username, String content, MultipartFile media) {
        boolean hasContent = content != null && !content.trim().isEmpty();
        boolean hasMedia = media != null && !media.isEmpty();

        if (!hasContent && !hasMedia) {
            throw new IllegalArgumentException("Bài viết phải có nội dung hoặc đính kèm ảnh/video!");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng @" + username));

        String mediaPath = null;
        MediaType mediaType = MediaType.NONE;

        if (hasMedia) {
            FileStorageService.UploadResult result = fileStorageService.storeMedia(media, "posts");
            if (result != null) {
                mediaPath = result.webPath();
                mediaType = result.mediaType();
            }
        }

        Post post = new Post(user, hasContent ? content.trim() : null, mediaPath, mediaType);
        Post saved = postRepository.save(post);
        return saved.getId();
    }

    /**
     * Xoa bai viet kem kiem tra quyen chu so huu.
     */
    @Transactional
    public void deletePost(Long postId, String username) {
        Post post = postRepository.findWithUserById(postId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bài viết #" + postId));

        if (!post.getUser().getUsername().equalsIgnoreCase(username)) {
            throw new ForbiddenException("Bạn không có quyền xóa bài viết của người khác!");
        }

        if (post.getMediaPath() != null) {
            fileStorageService.deleteFileByWebPath(post.getMediaPath());
        }

        postRepository.delete(post);
    }

    public Page<PostView> getFeed(String currentUsername, Pageable pageable) {
        Long currentUserId = getUserIdOrNull(currentUsername);
        Page<Post> posts = postRepository.findAllByOrderByCreatedAtDesc(pageable);
        return posts.map(p -> toPostView(p, currentUsername, currentUserId));
    }

    public Page<PostView> getUserPosts(Long userId, String currentUsername, String tab, Pageable pageable) {
        Long currentUserId = getUserIdOrNull(currentUsername);
        Page<Post> posts;

        if ("photos".equalsIgnoreCase(tab)) {
            posts = postRepository.findByUserIdAndMediaTypeOrderByCreatedAtDesc(userId, MediaType.IMAGE, pageable);
        } else if ("videos".equalsIgnoreCase(tab)) {
            posts = postRepository.findByUserIdAndMediaTypeOrderByCreatedAtDesc(userId, MediaType.VIDEO, pageable);
        } else {
            posts = postRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        }

        return posts.map(p -> toPostView(p, currentUsername, currentUserId));
    }

    public Page<PostView> searchPosts(String query, String currentUsername, Pageable pageable) {
        if (query == null || query.isBlank()) {
            return Page.empty(pageable);
        }
        Long currentUserId = getUserIdOrNull(currentUsername);
        Page<Post> posts = postRepository.searchPosts(query.trim(), pageable);
        return posts.map(p -> toPostView(p, currentUsername, currentUserId));
    }

    public PostView getPostViewById(Long postId, String currentUsername) {
        Post post = postRepository.findWithUserById(postId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bài viết #" + postId));
        Long currentUserId = getUserIdOrNull(currentUsername);
        return toPostView(post, currentUsername, currentUserId);
    }

    public long countTotalPosts(Long userId) {
        return postRepository.countByUserId(userId);
    }

    public long countPhotos(Long userId) {
        return postRepository.countByUserIdAndMediaType(userId, MediaType.IMAGE);
    }

    public long countVideos(Long userId) {
        return postRepository.countByUserIdAndMediaType(userId, MediaType.VIDEO);
    }

    public PostView toPostView(Post post, String currentUsername, Long currentUserId) {
        User author = post.getUser();
        boolean isOwner = currentUsername != null && currentUsername.equalsIgnoreCase(author.getUsername());
        boolean liked = currentUserId != null && postLikeRepository.existsByPostIdAndUserId(post.getId(), currentUserId);
        long likeCount = postLikeRepository.countByPostId(post.getId());
        long commentCount = commentRepository.countByPostId(post.getId());

        List<CommentView> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(post.getId())
                .stream()
                .map(commentService::toCommentView)
                .toList();

        return new PostView(
                post.getId(),
                author.getUsername(),
                author.getFullName(),
                author.getAvatarPath(),
                author.getInitials(),
                author.getAvatarColor(),
                post.getContent(),
                post.getMediaPath(),
                post.getMediaType(),
                post.getCreatedAtFormatted(),
                likeCount,
                commentCount,
                liked,
                isOwner,
                comments
        );
    }

    private Long getUserIdOrNull(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        return userRepository.findByUsername(username).map(User::getId).orElse(null);
    }
}

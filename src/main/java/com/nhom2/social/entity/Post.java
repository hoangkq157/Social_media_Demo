package com.nhom2.social.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity dai dien cho bai viet tren bang tin.
 * Bo cot title kieu cu, ho tro noi dung text va dinh kem da phuong tien (anh/video).
 */
@Entity
@Table(
    name = "posts",
    indexes = {
        @Index(name = "idx_posts_user_created", columnList = "user_id, created_at"),
        @Index(name = "idx_posts_media_type", columnList = "media_type")
    }
)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "media_path", length = 255)
    private String mediaPath;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false, length = 20)
    private MediaType mediaType = MediaType.NONE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> likes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (mediaType == null) {
            mediaType = MediaType.NONE;
        }
    }

    public Post() {
    }

    public Post(User user, String content, String mediaPath, MediaType mediaType) {
        this.user = user;
        this.content = content;
        this.mediaPath = mediaPath;
        this.mediaType = mediaType != null ? mediaType : MediaType.NONE;
    }

    public String getCreatedAtFormatted() {
        return createdAt == null ? "" : createdAt.format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getMediaPath() { return mediaPath; }
    public void setMediaPath(String mediaPath) { this.mediaPath = mediaPath; }
    public MediaType getMediaType() { return mediaType; }
    public void setMediaType(MediaType mediaType) { this.mediaType = mediaType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
    public List<PostLike> getLikes() { return likes; }
    public void setLikes(List<PostLike> likes) { this.likes = likes; }
}

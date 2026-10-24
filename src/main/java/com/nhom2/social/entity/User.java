package com.nhom2.social.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Entity dai dien cho nguoi dung trong he thong.
 */
@Entity
@Table(name = "users")
public class User {

    private static final String[] AVATAR_COLORS = {"1877F2", "E8453C", "F0A02A", "8B5CF6", "16A34A", "EC4899"};

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(length = 255)
    private String bio;

    @Column(name = "avatar_path", length = 255)
    private String avatarPath;

    @Column(name = "cover_path", length = 255)
    private String coverPath;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public User() {
    }

    public User(String username, String email, String password, String fullName) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
    }

    /**
     * Chu cai dau lam anh dai dien mac dinh (Facebook initials avatar).
     */
    public String getInitials() {
        if (fullName == null || fullName.isBlank()) {
            return username == null || username.isBlank() ? "?" : username.substring(0, 1).toUpperCase();
        }
        String[] parts = fullName.trim().split("\\s+");
        String ini = parts[0].substring(0, 1);
        if (parts.length > 1) {
            ini += parts[parts.length - 1].substring(0, 1);
        }
        return ini.toUpperCase();
    }

    /**
     * Ma mau on dinh dua theo hash cua username.
     */
    public String getAvatarColor() {
        int h = username == null ? 0 : Math.abs(username.hashCode());
        return "#" + AVATAR_COLORS[h % AVATAR_COLORS.length];
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getAvatarPath() { return avatarPath; }
    public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath; }
    public String getCoverPath() { return coverPath; }
    public void setCoverPath(String coverPath) { this.coverPath = coverPath; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

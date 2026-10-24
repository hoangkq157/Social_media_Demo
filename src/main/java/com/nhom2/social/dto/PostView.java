package com.nhom2.social.dto;

import com.nhom2.social.entity.MediaType;

import java.util.List;

/**
 * Thong tin hien thi day du cua mot bai viet tren bang tin hoac trang ca nhan.
 */
public record PostView(
    Long id,
    String authorUsername,
    String authorFullName,
    String authorAvatarPath,
    String authorInitials,
    String authorAvatarColor,
    String content,
    String mediaPath,
    MediaType mediaType,
    String createdAtFormatted,
    long likeCount,
    long commentCount,
    boolean likedByCurrentUser,
    boolean isOwner,
    List<CommentView> comments
) {}

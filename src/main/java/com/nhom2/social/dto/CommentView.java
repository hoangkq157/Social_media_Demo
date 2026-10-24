package com.nhom2.social.dto;

/**
 * Thong tin hien thi mot binh luan (tra ve qua Thymeleaf hoac REST API).
 */
public record CommentView(
    Long id,
    Long postId,
    String authorUsername,
    String authorFullName,
    String authorAvatarPath,
    String authorInitials,
    String authorAvatarColor,
    String content,
    String createdAtFormatted
) {}

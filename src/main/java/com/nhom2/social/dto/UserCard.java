package com.nhom2.social.dto;

/**
 * Thong tin tom tat cua nguoi dung de hien thi tren the (card) va trang ca nhan.
 */
public record UserCard(
    Long id,
    String username,
    String fullName,
    String bio,
    String avatarPath,
    String coverPath,
    String initials,
    String avatarColor,
    String createdAtFormatted,
    boolean isCurrent
) {}

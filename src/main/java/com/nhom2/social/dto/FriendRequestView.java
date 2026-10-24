package com.nhom2.social.dto;

/**
 * Thong tin loi moi ket ban hien thi tren danh sach / widget.
 */
public record FriendRequestView(
    Long id,
    UserCard user,
    String createdAtFormatted
) {}

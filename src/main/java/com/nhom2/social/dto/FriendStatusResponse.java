package com.nhom2.social.dto;

/**
 * Phan hoi JSON cho cac thao tac ket ban qua AJAX.
 */
public record FriendStatusResponse(
    String relation,
    long friendCount,
    String message
) {}

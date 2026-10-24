package com.nhom2.social.dto;

/**
 * Phan hoi JSON khi nguoi dung thich hoac bo thich mot bai viet qua AJAX.
 */
public record LikeResponse(
    boolean liked,
    long likeCount
) {}

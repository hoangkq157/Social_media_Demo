package com.nhom2.social.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO nhan yeu cau gui binh luan moi qua AJAX.
 */
public record CommentRequest(
    @NotBlank(message = "Nội dung bình luận không được để trống")
    @Size(max = 1000, message = "Bình luận tối đa 1000 ký tự")
    String content
) {}

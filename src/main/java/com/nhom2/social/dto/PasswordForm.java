package com.nhom2.social.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO doi mat khau cho nguoi dung hien tai.
 */
public record PasswordForm(
    @NotBlank(message = "Vui lòng nhập mật khẩu hiện tại")
    String currentPassword,

    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 6, max = 50, message = "Mật khẩu mới phải từ 6 đến 50 ký tự")
    String newPassword,

    @NotBlank(message = "Vui lòng xác nhận mật khẩu mới")
    String confirmPassword
) {}

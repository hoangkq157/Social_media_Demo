package com.nhom2.social.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO cap nhat thong tin ca nhan.
 */
public record ProfileForm(
    @NotBlank(message = "Họ và tên không được để trống")
    @Size(max = 100, message = "Họ và tên tối đa 100 ký tự")
    String fullName,

    @Size(max = 255, message = "Tiểu sử tối đa 255 ký tự")
    String bio,

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 100, message = "Email tối đa 100 ký tự")
    String email
) {}

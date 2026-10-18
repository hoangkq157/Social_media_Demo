-- ============================================================
-- SCRIPT TAO BANG — CSDL mang xa hoi don gian (Nhom 2)
-- Cú pháp chuẩn MySQL, chạy được trên H2 (chế độ MODE=MySQL)
-- Lưu ý: khi chạy app, bảng do Hibernate tự tạo từ entity;
-- file này để nộp/nạp tay trên CSDL khác theo yêu cầu đề bài.
-- ============================================================

CREATE DATABASE IF NOT EXISTS social_db;
USE social_db;

CREATE TABLE users (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    email      VARCHAR(100),
    password   VARCHAR(100) NOT NULL,
    full_name  VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE posts (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    title      VARCHAR(200) NOT NULL,
    content    TEXT         NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE comments (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id    BIGINT NOT NULL,
    user_id    BIGINT NOT NULL,
    content    TEXT   NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

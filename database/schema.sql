-- ============================================================
-- SCRIPT TAO BANG — CSDL mang xa hoi SpringBook (Nhom 2)
-- Cu phap chuan MySQL, dong bo hoan toan voi Entity va H2
-- ============================================================

CREATE DATABASE IF NOT EXISTS social_db;
USE social_db;

CREATE TABLE users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(100) NOT NULL,
    full_name   VARCHAR(100),
    bio         VARCHAR(255),
    avatar_path VARCHAR(255),
    cover_path  VARCHAR(255),
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE posts (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    content     TEXT,
    media_path  VARCHAR(255),
    media_type  VARCHAR(20)  NOT NULL DEFAULT 'NONE',
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id),
    INDEX idx_posts_user_created (user_id, created_at),
    INDEX idx_posts_media_type (media_type)
);

CREATE TABLE comments (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id     BIGINT NOT NULL,
    user_id     BIGINT NOT NULL,
    content     TEXT   NOT NULL,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE post_likes (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id     BIGINT NOT NULL,
    user_id     BIGINT NOT NULL,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT uk_post_user_like UNIQUE (post_id, user_id)
);

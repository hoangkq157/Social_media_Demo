package com.nhom2.social.dto;

import org.springframework.data.domain.Page;

/**
 * Thong tin tong the trang ca nhan cua mot nguoi dung.
 */
public record ProfileView(
    UserCard user,
    long totalPosts,
    long photoCount,
    long videoCount,
    Page<PostView> posts,
    String activeTab,
    boolean isOwnProfile,
    long friendCount,
    FriendRelation friendRelation
) {}

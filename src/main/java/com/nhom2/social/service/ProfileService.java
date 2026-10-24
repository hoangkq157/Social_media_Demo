package com.nhom2.social.service;

import com.nhom2.social.dto.PostView;
import com.nhom2.social.dto.ProfileView;
import com.nhom2.social.dto.UserCard;
import com.nhom2.social.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service tong hop thong tin cho trang ca nhan cua nguoi dung (Profile).
 */
@Service
@Transactional(readOnly = true)
public class ProfileService {

    private final UserService userService;
    private final PostService postService;

    public ProfileService(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    public ProfileView getProfile(String targetUsername, String currentUsername, String tab, Pageable pageable) {
        User user = userService.getByUsername(targetUsername);
        boolean isOwnProfile = currentUsername != null && currentUsername.equalsIgnoreCase(targetUsername);
        UserCard userCard = userService.toUserCard(user, isOwnProfile);

        long totalPosts = postService.countTotalPosts(user.getId());
        long photoCount = postService.countPhotos(user.getId());
        long videoCount = postService.countVideos(user.getId());

        String activeTab = (tab != null && !tab.isBlank()) ? tab.toLowerCase() : "all";
        Page<PostView> posts = postService.getUserPosts(user.getId(), currentUsername, activeTab, pageable);

        return new ProfileView(userCard, totalPosts, photoCount, videoCount, posts, activeTab, isOwnProfile);
    }
}

package com.nhom2.social.service;

import com.nhom2.social.dto.FriendRelation;
import com.nhom2.social.dto.PostView;
import com.nhom2.social.dto.ProfileView;
import com.nhom2.social.dto.UserCard;
import com.nhom2.social.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service tong hop thong tin cho trang ca nhan cua nguoi dung (Profile):
 * - Thong tin user, bai viet, anh, video
 * - Quan he ban be voi nguoi dang xem va so luong ban be
 */
@Service
@Transactional(readOnly = true)
public class ProfileService {

    private final UserService userService;
    private final PostService postService;
    private final FriendshipService friendshipService;

    public ProfileService(UserService userService,
                          PostService postService,
                          FriendshipService friendshipService) {
        this.userService = userService;
        this.postService = postService;
        this.friendshipService = friendshipService;
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

        long friendCount = friendshipService.countFriends(targetUsername);
        FriendRelation friendRelation = friendshipService.getRelation(currentUsername, targetUsername);

        return new ProfileView(
                userCard,
                totalPosts,
                photoCount,
                videoCount,
                posts,
                activeTab,
                isOwnProfile,
                friendCount,
                friendRelation
        );
    }
}

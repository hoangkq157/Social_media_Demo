package com.nhom2.social.controller;

import com.nhom2.social.dto.ProfileView;
import com.nhom2.social.dto.UserCard;
import com.nhom2.social.service.ProfileService;
import com.nhom2.social.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller hien thi trang ca nhan cua nguoi dung:
 * - Hien thi thong tin ca nhan, avatar, anh bia
 * - Loc bai viet theo tab: Tat ca / Anh / Video
 */
@Controller
public class ProfileController {

    private final ProfileService profileService;
    private final UserService userService;

    public ProfileController(ProfileService profileService, UserService userService) {
        this.profileService = profileService;
        this.userService = userService;
    }

    @GetMapping("/users/{username}")
    public String profile(@AuthenticationPrincipal UserDetails principal,
                          @PathVariable("username") String username,
                          @RequestParam(value = "tab", defaultValue = "all") String tab,
                          @RequestParam(value = "page", defaultValue = "0") int page,
                          Model model) {
        String currentUsername = principal != null ? principal.getUsername() : "";
        ProfileView profile = profileService.getProfile(username, currentUsername, tab, PageRequest.of(page, 10));
        UserCard currentUser = userService.getUserCard(currentUsername, currentUsername);

        model.addAttribute("profile", profile);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("activeTab", profile.activeTab());

        return "profile";
    }
}

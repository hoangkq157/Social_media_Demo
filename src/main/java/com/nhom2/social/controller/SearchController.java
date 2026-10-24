package com.nhom2.social.controller;

import com.nhom2.social.dto.PostView;
import com.nhom2.social.dto.UserCard;
import com.nhom2.social.service.PostService;
import com.nhom2.social.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Controller phu trach tim kiem:
 * - Tim kiem nguoi dung theo username hoac ho ten
 * - Tim kiem bai viet theo noi dung
 */
@Controller
public class SearchController {

    private final UserService userService;
    private final PostService postService;
    private final com.nhom2.social.service.FriendshipService friendshipService;

    public SearchController(UserService userService,
                            PostService postService,
                            com.nhom2.social.service.FriendshipService friendshipService) {
        this.userService = userService;
        this.postService = postService;
        this.friendshipService = friendshipService;
    }

    @GetMapping("/search")
    public String search(@AuthenticationPrincipal UserDetails principal,
                         @RequestParam(value = "q", defaultValue = "") String query,
                         @RequestParam(value = "type", defaultValue = "users") String type,
                         @RequestParam(value = "page", defaultValue = "0") int page,
                         Model model) {
        String currentUsername = principal != null ? principal.getUsername() : "";
        UserCard currentUser = userService.getUserCard(currentUsername, currentUsername);
        List<UserCard> suggestions = userService.getSuggestions(currentUsername);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("suggestions", suggestions);
        model.addAttribute("query", query);
        model.addAttribute("type", type);

        if ("posts".equalsIgnoreCase(type)) {
            Page<PostView> posts = postService.searchPosts(query, currentUsername, PageRequest.of(page, 10));
            model.addAttribute("posts", posts);
        } else {
            List<UserCard> users = userService.searchUsers(query, currentUsername);
            java.util.Map<String, com.nhom2.social.dto.FriendRelation> relations = new java.util.HashMap<>();
            for (UserCard u : users) {
                relations.put(u.username(), friendshipService.getRelation(currentUsername, u.username()));
            }
            model.addAttribute("users", users);
            model.addAttribute("relations", relations);
        }

        return "search";
    }
}

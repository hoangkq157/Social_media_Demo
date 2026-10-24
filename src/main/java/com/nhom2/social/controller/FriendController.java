package com.nhom2.social.controller;

import com.nhom2.social.dto.FriendRequestView;
import com.nhom2.social.dto.UserCard;
import com.nhom2.social.service.FriendshipService;
import com.nhom2.social.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller phu trach trang Ban be (/friends) va cac form POST ket ban truyen thong:
 * - Hien thi danh sach loi moi ket ban dang cho
 * - Hien thi danh sach ban be hien tai
 * - Hien thi danh sach nguoi dung goi y ket ban
 */
@Controller
@RequestMapping("/friends")
public class FriendController {

    private final FriendshipService friendshipService;
    private final UserService userService;

    public FriendController(FriendshipService friendshipService, UserService userService) {
        this.friendshipService = friendshipService;
        this.userService = userService;
    }

    /**
     * Trang quan ly ban be: Tab loi moi, ban be, goi y.
     */
    @GetMapping
    public String friendsPage(@AuthenticationPrincipal UserDetails principal,
                              @RequestParam(value = "tab", defaultValue = "requests") String tab,
                              Model model) {
        String currentUsername = principal != null ? principal.getUsername() : "an";
        UserCard currentUser = userService.getUserCard(currentUsername, currentUsername);

        List<FriendRequestView> requests = friendshipService.getPendingRequestsReceived(currentUsername);
        List<UserCard> friends = friendshipService.getFriends(currentUsername);
        List<UserCard> suggestions = friendshipService.getSuggestedUsers(currentUsername);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("requests", requests);
        model.addAttribute("friends", friends);
        model.addAttribute("suggestions", suggestions);
        model.addAttribute("activeTab", tab);
        model.addAttribute("pendingCount", requests.size());
        model.addAttribute("friendCount", friends.size());

        return "friends";
    }

    @PostMapping("/request/{targetUsername}")
    public String sendRequestForm(@AuthenticationPrincipal UserDetails principal,
                                  @PathVariable("targetUsername") String targetUsername,
                                  HttpServletRequest request,
                                  RedirectAttributes redirectAttributes) {
        try {
            friendshipService.sendRequest(principal.getUsername(), targetUsername);
            redirectAttributes.addFlashAttribute("successMessage", "Đã gửi lời mời kết bạn tới @" + targetUsername);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return redirectToReferer(request);
    }

    @PostMapping("/accept/{senderUsername}")
    public String acceptRequestForm(@AuthenticationPrincipal UserDetails principal,
                                    @PathVariable("senderUsername") String senderUsername,
                                    HttpServletRequest request,
                                    RedirectAttributes redirectAttributes) {
        try {
            friendshipService.acceptRequest(principal.getUsername(), senderUsername);
            redirectAttributes.addFlashAttribute("successMessage", "Đã chấp nhận lời mời kết bạn từ @" + senderUsername);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return redirectToReferer(request);
    }

    @PostMapping("/reject/{senderUsername}")
    public String rejectRequestForm(@AuthenticationPrincipal UserDetails principal,
                                    @PathVariable("senderUsername") String senderUsername,
                                    HttpServletRequest request,
                                    RedirectAttributes redirectAttributes) {
        try {
            friendshipService.rejectRequest(principal.getUsername(), senderUsername);
            redirectAttributes.addFlashAttribute("successMessage", "Đã từ chối lời mời kết bạn từ @" + senderUsername);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return redirectToReferer(request);
    }

    @PostMapping("/cancel/{targetUsername}")
    public String cancelRequestForm(@AuthenticationPrincipal UserDetails principal,
                                    @PathVariable("targetUsername") String targetUsername,
                                    HttpServletRequest request,
                                    RedirectAttributes redirectAttributes) {
        try {
            friendshipService.cancelRequest(principal.getUsername(), targetUsername);
            redirectAttributes.addFlashAttribute("successMessage", "Đã hủy lời mời kết bạn tới @" + targetUsername);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return redirectToReferer(request);
    }

    @PostMapping("/unfriend/{targetUsername}")
    public String unfriendForm(@AuthenticationPrincipal UserDetails principal,
                               @PathVariable("targetUsername") String targetUsername,
                               HttpServletRequest request,
                               RedirectAttributes redirectAttributes) {
        try {
            friendshipService.unfriend(principal.getUsername(), targetUsername);
            redirectAttributes.addFlashAttribute("successMessage", "Đã hủy kết bạn với @" + targetUsername);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return redirectToReferer(request);
    }

    private String redirectToReferer(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        return (referer != null && !referer.isBlank()) ? "redirect:" + referer : "redirect:/friends";
    }
}

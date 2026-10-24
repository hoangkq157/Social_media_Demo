package com.nhom2.social.controller;

import com.nhom2.social.config.AppProperties;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller phu trach bang tin chinh (/):
 * - Hien thi composer va danh sach bai viet
 * - Ho tro tai them (load more) tra ve Thymeleaf fragment khong reload trang
 * - Xu ly dang bai moi va xoa bai viet
 */
@Controller
public class FeedController {

    private final PostService postService;
    private final UserService userService;
    private final AppProperties appProperties;

    public FeedController(PostService postService, UserService userService, AppProperties appProperties) {
        this.postService = postService;
        this.userService = userService;
        this.appProperties = appProperties;
    }

    @GetMapping("/")
    public String feed(@AuthenticationPrincipal UserDetails principal,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       Model model) {
        int pageSize = appProperties.getFeed().getPageSize();
        String currentUsername = principal != null ? principal.getUsername() : "an";

        UserCard currentUser = userService.getUserCard(currentUsername, currentUsername);
        List<UserCard> suggestions = userService.getSuggestions(currentUsername);
        Page<PostView> posts = postService.getFeed(currentUsername, PageRequest.of(page, pageSize));

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("suggestions", suggestions);
        model.addAttribute("posts", posts);
        model.addAttribute("currentPage", page);
        model.addAttribute("hasNext", posts.hasNext());

        return "feed";
    }

    @GetMapping("/feed/more")
    public String loadMore(@AuthenticationPrincipal UserDetails principal,
                           @RequestParam(value = "page", defaultValue = "1") int page,
                           Model model) {
        int pageSize = appProperties.getFeed().getPageSize();
        String currentUsername = principal != null ? principal.getUsername() : "an";

        Page<PostView> posts = postService.getFeed(currentUsername, PageRequest.of(page, pageSize));

        model.addAttribute("posts", posts);
        model.addAttribute("currentPage", page);
        model.addAttribute("hasNext", posts.hasNext());

        return "fragments/post-card :: postList";
    }

    @PostMapping("/posts")
    public String createPost(@AuthenticationPrincipal UserDetails principal,
                             @RequestParam(value = "content", required = false) String content,
                             @RequestParam(value = "media", required = false) MultipartFile media,
                             RedirectAttributes redirectAttributes) {
        try {
            postService.createPost(principal.getUsername(), content, media);
            redirectAttributes.addFlashAttribute("successMessage", "Đã đăng bài viết mới!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/";
    }

    @PostMapping("/posts/{id}/delete")
    public String deletePost(@AuthenticationPrincipal UserDetails principal,
                             @PathVariable("id") Long id,
                             RedirectAttributes redirectAttributes) {
        postService.deletePost(id, principal.getUsername());
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa bài viết thành công!");
        return "redirect:/";
    }
}

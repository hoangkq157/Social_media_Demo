package com.nhom2.social.controller;

import com.nhom2.social.entity.Post;
import com.nhom2.social.entity.User;
import com.nhom2.social.service.PostService;
import com.nhom2.social.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PostController {

    private final PostService postService;
    private final UserService userService;

    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    /** Bang tin - chua dang nhap thi day ve /login. Ho tro ?q= tim kiem. */
    @GetMapping("/")
    public String feed(@RequestParam(required = false) String q, Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            return "redirect:/login";
        }
        User me = userService.findByUsername((String) session.getAttribute("username")).orElse(null);
        model.addAttribute("me", me);
        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("posts", postService.getFeed(q));
        return "feed";
    }

    @GetMapping("/posts/new")
    public String postForm(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            return "redirect:/login";
        }
        User me = userService.findByUsername((String) session.getAttribute("username")).orElse(null);
        model.addAttribute("me", me);
        return "post-form";
    }

    /** Tinh nang 2: Dang bai viet - luu vao bang posts. */
    @PostMapping("/posts/create")
    public String createPost(@RequestParam String title,
                             @RequestParam String content,
                             HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            return "redirect:/login";
        }
        User author = userService.findByUsername((String) session.getAttribute("username")).orElseThrow();
        postService.save(new Post(author, title, content));
        return "redirect:/";
    }
}

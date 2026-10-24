package com.nhom2.social.controller;

import com.nhom2.social.dto.RegisterForm;
import com.nhom2.social.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller phu trach dang nhap va dang ky tai khoan.
 */
@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }

        if (error != null) {
            model.addAttribute("loginError", "Tên đăng nhập hoặc mật khẩu không chính xác!");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "Bạn đã đăng xuất an toàn.");
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }

        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new RegisterForm("", "", "", ""));
        }
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("form") RegisterForm form,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            userService.register(form);
            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký tài khoản thành công! Bạn có thể đăng nhập ngay.");
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "register";
        }
    }
}

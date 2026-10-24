package com.nhom2.social.controller;

import com.nhom2.social.dto.PasswordForm;
import com.nhom2.social.dto.ProfileForm;
import com.nhom2.social.dto.UserCard;
import com.nhom2.social.entity.User;
import com.nhom2.social.service.UserService;
import com.nhom2.social.storage.FileStorageService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller phu trach cai dat thong tin ca nhan va bao mat:
 * - Thay doi ho ten, tieu su, email
 * - Upload doi anh dai dien va anh bia
 * - Doi mat khau nguoi dung
 */
@Controller
@RequestMapping("/settings")
public class SettingsController {

    private final UserService userService;
    private final FileStorageService fileStorageService;

    public SettingsController(UserService userService, FileStorageService fileStorageService) {
        this.userService = userService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/profile")
    public String profileSettings(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = userService.getByUsername(principal.getUsername());
        UserCard currentUser = userService.toUserCard(user, true);

        if (!model.containsAttribute("profileForm")) {
            model.addAttribute("profileForm", new ProfileForm(
                    user.getFullName() != null ? user.getFullName() : "",
                    user.getBio() != null ? user.getBio() : "",
                    user.getEmail()
            ));
        }
        if (!model.containsAttribute("passwordForm")) {
            model.addAttribute("passwordForm", new PasswordForm("", "", ""));
        }
        model.addAttribute("currentUser", currentUser);

        return "settings-profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal UserDetails principal,
                                @Valid @ModelAttribute("profileForm") ProfileForm profileForm,
                                BindingResult bindingResult,
                                @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
                                @RequestParam(value = "coverFile", required = false) MultipartFile coverFile,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        String username = principal.getUsername();
        User user = userService.getByUsername(username);

        if (bindingResult.hasErrors()) {
            model.addAttribute("currentUser", userService.toUserCard(user, true));
            if (!model.containsAttribute("passwordForm")) {
                model.addAttribute("passwordForm", new PasswordForm("", "", ""));
            }
            return "settings-profile";
        }

        try {
            // Cap nhat avatar neu co
            if (avatarFile != null && !avatarFile.isEmpty()) {
                FileStorageService.UploadResult res = fileStorageService.storeMedia(avatarFile, "avatars");
                if (res != null) {
                    if (user.getAvatarPath() != null) {
                        fileStorageService.deleteFileByWebPath(user.getAvatarPath());
                    }
                    userService.updateAvatar(username, res.webPath());
                }
            }

            // Cap nhat anh bia neu co
            if (coverFile != null && !coverFile.isEmpty()) {
                FileStorageService.UploadResult res = fileStorageService.storeMedia(coverFile, "covers");
                if (res != null) {
                    if (user.getCoverPath() != null) {
                        fileStorageService.deleteFileByWebPath(user.getCoverPath());
                    }
                    userService.updateCover(username, res.webPath());
                }
            }

            // Cap nhat ho ten, bio, email
            userService.updateProfile(username, profileForm);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin cá nhân thành công!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/settings/profile";
    }

    @PostMapping("/password")
    public String changePassword(@AuthenticationPrincipal UserDetails principal,
                                 @Valid @ModelAttribute("passwordForm") PasswordForm passwordForm,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        String username = principal.getUsername();

        if (bindingResult.hasErrors()) {
            User user = userService.getByUsername(username);
            model.addAttribute("currentUser", userService.toUserCard(user, true));
            if (!model.containsAttribute("profileForm")) {
                model.addAttribute("profileForm", new ProfileForm(
                        user.getFullName() != null ? user.getFullName() : "",
                        user.getBio() != null ? user.getBio() : "",
                        user.getEmail()
                ));
            }
            return "settings-profile";
        }

        try {
            userService.changePassword(username, passwordForm);
            redirectAttributes.addFlashAttribute("passwordSuccessMessage", "Đổi mật khẩu thành công!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("passwordErrorMessage", ex.getMessage());
        }

        return "redirect:/settings/profile";
    }
}

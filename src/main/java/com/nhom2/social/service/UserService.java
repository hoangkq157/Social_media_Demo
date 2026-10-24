package com.nhom2.social.service;

import com.nhom2.social.config.UserPrincipal;
import com.nhom2.social.dto.PasswordForm;
import com.nhom2.social.dto.ProfileForm;
import com.nhom2.social.dto.RegisterForm;
import com.nhom2.social.dto.UserCard;
import com.nhom2.social.entity.User;
import com.nhom2.social.exception.NotFoundException;
import com.nhom2.social.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Service quan ly nguoi dung va tich hop UserDetailsService cua Spring Security.
 * Thuc hien cac nghiep vu xac thuc, dang ky, cap nhat thong tin, doi mat khau.
 */
@Service
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng: " + username));
        return new UserPrincipal(user);
    }

    /**
     * Dang ky tai khoan moi kem ma hoa mat khau bang BCrypt.
     */
    @Transactional
    public User register(RegisterForm form) {
        if (userRepository.existsByUsername(form.username())) {
            throw new IllegalArgumentException("Tên đăng nhập '" + form.username() + "' đã được sử dụng");
        }
        if (userRepository.existsByEmail(form.email())) {
            throw new IllegalArgumentException("Email '" + form.email() + "' đã được sử dụng");
        }

        User user = new User();
        user.setUsername(form.username().trim());
        user.setFullName(form.fullName().trim());
        user.setEmail(form.email().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(form.password()));

        return userRepository.save(user);
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng @" + username));
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public UserCard getUserCard(String targetUsername, String currentUsername) {
        User user = getByUsername(targetUsername);
        boolean isCurrent = currentUsername != null && currentUsername.equalsIgnoreCase(targetUsername);
        return toUserCard(user, isCurrent);
    }

    public List<UserCard> getSuggestions(String currentUsername) {
        return userRepository.findTop5ByUsernameNotOrderByCreatedAtDesc(currentUsername != null ? currentUsername : "")
                .stream()
                .map(u -> toUserCard(u, false))
                .toList();
    }

    public List<UserCard> searchUsers(String query, String currentUsername) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        return userRepository.searchUsers(query.trim())
                .stream()
                .map(u -> toUserCard(u, currentUsername != null && currentUsername.equalsIgnoreCase(u.getUsername())))
                .toList();
    }

    @Transactional
    public void updateProfile(String username, ProfileForm form) {
        User user = getByUsername(username);
        String newEmail = form.email().trim().toLowerCase();

        // Kiem tra email da bi user khac su dung chua
        if (!newEmail.equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("Email '" + newEmail + "' đã được tài khoản khác sử dụng");
        }

        user.setFullName(form.fullName().trim());
        user.setBio(form.bio() != null ? form.bio().trim() : null);
        user.setEmail(newEmail);
        userRepository.save(user);
    }

    @Transactional
    public void updateAvatar(String username, String avatarPath) {
        User user = getByUsername(username);
        user.setAvatarPath(avatarPath);
        userRepository.save(user);
    }

    @Transactional
    public void updateCover(String username, String coverPath) {
        User user = getByUsername(username);
        user.setCoverPath(coverPath);
        userRepository.save(user);
    }

    @Transactional
    public void changePassword(String username, PasswordForm form) {
        User user = getByUsername(username);

        if (!passwordEncoder.matches(form.currentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không chính xác");
        }
        if (!form.newPassword().equals(form.confirmPassword())) {
            throw new IllegalArgumentException("Xác nhận mật khẩu mới không khớp");
        }
        if (passwordEncoder.matches(form.newPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu mới không được trùng với mật khẩu hiện tại");
        }

        user.setPassword(passwordEncoder.encode(form.newPassword()));
        userRepository.save(user);
    }

    public UserCard toUserCard(User user, boolean isCurrent) {
        String createdFormatted = user.getCreatedAt() != null ? user.getCreatedAt().format(DATE_FORMATTER) : "";
        return new UserCard(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getBio(),
                user.getAvatarPath(),
                user.getCoverPath(),
                user.getInitials(),
                user.getAvatarColor(),
                createdFormatted,
                isCurrent
        );
    }
}

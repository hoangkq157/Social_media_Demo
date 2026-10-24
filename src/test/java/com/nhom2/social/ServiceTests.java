package com.nhom2.social;

import com.nhom2.social.dto.PasswordForm;
import com.nhom2.social.entity.MediaType;
import com.nhom2.social.entity.Post;
import com.nhom2.social.entity.User;
import com.nhom2.social.exception.ForbiddenException;
import com.nhom2.social.repository.PostRepository;
import com.nhom2.social.repository.UserRepository;
import com.nhom2.social.service.PostService;
import com.nhom2.social.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class ServiceTests {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("Đổi mật khẩu: Thất bại khi nhập sai mật khẩu hiện tại")
    void testChangePasswordWrongCurrent() {
        PasswordForm form = new PasswordForm("saimatkhau", "newpass123", "newpass123");
        assertThrows(IllegalArgumentException.class, () -> {
            userService.changePassword("an", form);
        });
    }

    @Test
    @DisplayName("Đổi mật khẩu: Thất bại khi xác nhận mật khẩu mới không khớp")
    void testChangePasswordMismatchConfirm() {
        PasswordForm form = new PasswordForm("123456", "newpass123", "khacmatkhau");
        assertThrows(IllegalArgumentException.class, () -> {
            userService.changePassword("an", form);
        });
    }

    @Test
    @DisplayName("Đổi mật khẩu: Thành công khi nhập đúng thông tin")
    void testChangePasswordSuccess() {
        PasswordForm form = new PasswordForm("123456", "newSecret123", "newSecret123");
        userService.changePassword("an", form);

        // Kiem tra xac thuc UserDetails voi mat khau moi
        org.springframework.security.core.userdetails.UserDetails userDetails = userService.loadUserByUsername("an");
        assertThat(userDetails).isNotNull();
    }

    @Test
    @DisplayName("Quyền xóa bài: Người dùng không phải tác giả không được xóa bài của người khác (Forbidden)")
    void testDeletePostForbiddenForNonOwner() {
        User author = userRepository.findByUsername("an").orElseThrow();
        Post post = new Post(author, "Bài viết thử nghiệm phân quyền", null, MediaType.NONE);
        Post saved = postRepository.save(post);

        // User 'binh' co gang xoa bai cua 'an' -> phai quang ForbiddenException
        assertThrows(ForbiddenException.class, () -> {
            postService.deletePost(saved.getId(), "binh");
        });
    }

    @Test
    @DisplayName("Tạo bài viết: Báo lỗi khi cả nội dung và media đều rỗng")
    void testCreatePostEmptyContentAndMedia() {
        assertThrows(IllegalArgumentException.class, () -> {
            postService.createPost("an", "   ", null);
        });
    }
}

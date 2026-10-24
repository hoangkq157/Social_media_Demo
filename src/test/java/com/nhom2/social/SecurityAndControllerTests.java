package com.nhom2.social;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.MockMvcAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityAndControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Bảo mật: Chưa đăng nhập truy cập / bị chuyển hướng về /login")
    void testUnauthenticatedRedirectToLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/login")));
    }

    @Test
    @DisplayName("Bảo mật: Trang /login và /register cho phép truy cập công khai")
    void testPublicAuthPages() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/register"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Bảo mật: POST thiếu mã CSRF bị từ chối 403 Forbidden")
    void testPostWithoutCsrfForbidden() throws Exception {
        mockMvc.perform(post("/api/posts/1/like")
                        .with(user("an"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Bảo mật & Controller: Đăng nhập hợp lệ truy cập được /")
    void testAuthenticatedAccessFeed() throws Exception {
        mockMvc.perform(get("/").with(user("an")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("AJAX API: Like bài viết với CSRF trả về JSON hợp lệ")
    void testLikePostApiWithCsrf() throws Exception {
        mockMvc.perform(post("/api/posts/1/like")
                        .with(user("an"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liked").isBoolean())
                .andExpect(jsonPath("$.likeCount").isNumber());
    }

    @Test
    @DisplayName("AJAX API: Gửi bình luận hợp lệ trả về status 201 Created và CommentView JSON")
    void testAddCommentApiWithCsrf() throws Exception {
        String jsonBody = "{\"content\": \"Bình luận kiểm thử tự động với MockMvc\"}";
        mockMvc.perform(post("/api/posts/1/comments")
                        .with(user("an"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Bình luận kiểm thử tự động với MockMvc"))
                .andExpect(jsonPath("$.authorUsername").value("an"));
    }
}

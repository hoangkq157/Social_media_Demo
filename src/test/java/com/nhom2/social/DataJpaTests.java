package com.nhom2.social;

import com.nhom2.social.entity.MediaType;
import com.nhom2.social.entity.Post;
import com.nhom2.social.entity.PostLike;
import com.nhom2.social.entity.User;
import com.nhom2.social.repository.PostLikeRepository;
import com.nhom2.social.repository.PostRepository;
import com.nhom2.social.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class DataJpaTests {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Test
    @DisplayName("Kiểm tra tìm kiếm người dùng theo username hoặc họ tên không phân biệt hoa thường")
    void testSearchUsers() {
        User u1 = new User("nguyenvana", "nva@test.com", "pass123", "Nguyễn Văn A");
        User u2 = new User("tranthib", "ttb@test.com", "pass123", "Trần Thị B");
        entityManager.persist(u1);
        entityManager.persist(u2);
        entityManager.flush();

        List<User> results = userRepository.searchUsers("vĂn");
        assertThat(results).anyMatch(u -> "nguyenvana".equals(u.getUsername()));

        List<User> searchByUsername = userRepository.searchUsers("tranthi");
        assertThat(searchByUsername).anyMatch(u -> "tranthib".equals(u.getUsername()));
    }

    @Test
    @DisplayName("Kiểm tra lọc bài viết theo MediaType (Ảnh / Video)")
    void testFilterPostsByMediaType() {
        User user = new User("author1", "author1@test.com", "pass123", "Tác Giả 1");
        entityManager.persist(user);

        Post textPost = new Post(user, "Bài viết chỉ có chữ", null, MediaType.NONE);
        Post photoPost = new Post(user, "Bài viết có ảnh", "/uploads/posts/photo.jpg", MediaType.IMAGE);
        Post videoPost = new Post(user, "Bài viết có video", "/uploads/posts/video.mp4", MediaType.VIDEO);

        entityManager.persist(textPost);
        entityManager.persist(photoPost);
        entityManager.persist(videoPost);
        entityManager.flush();

        Page<Post> photoPosts = postRepository.findByUserIdAndMediaTypeOrderByCreatedAtDesc(user.getId(), MediaType.IMAGE, PageRequest.of(0, 10));
        assertThat(photoPosts.getContent()).hasSize(1);
        assertThat(photoPosts.getContent().get(0).getMediaType()).isEqualTo(MediaType.IMAGE);

        Page<Post> videoPosts = postRepository.findByUserIdAndMediaTypeOrderByCreatedAtDesc(user.getId(), MediaType.VIDEO, PageRequest.of(0, 10));
        assertThat(videoPosts.getContent()).hasSize(1);
        assertThat(videoPosts.getContent().get(0).getMediaType()).isEqualTo(MediaType.VIDEO);

        long photoCount = postRepository.countByUserIdAndMediaType(user.getId(), MediaType.IMAGE);
        long videoCount = postRepository.countByUserIdAndMediaType(user.getId(), MediaType.VIDEO);
        long totalCount = postRepository.countByUserId(user.getId());

        assertThat(photoCount).isEqualTo(1);
        assertThat(videoCount).isEqualTo(1);
        assertThat(totalCount).isEqualTo(3);
    }

    @Test
    @DisplayName("Kiểm tra ràng buộc duy nhất (Unique) của lượt thích: mỗi user chỉ like 1 bài 1 lần")
    void testUniquePostLike() {
        User user = new User("liker", "liker@test.com", "pass123", "Người Thích");
        Post post = new Post(user, "Bài viết cần like", null, MediaType.NONE);
        entityManager.persist(user);
        entityManager.persist(post);
        entityManager.flush();

        PostLike like1 = new PostLike(post, user);
        postLikeRepository.saveAndFlush(like1);

        PostLike like2 = new PostLike(post, user);
        assertThrows(Exception.class, () -> {
            postLikeRepository.saveAndFlush(like2);
        });
    }
}

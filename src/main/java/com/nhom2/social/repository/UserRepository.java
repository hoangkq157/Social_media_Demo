package com.nhom2.social.repository;

import com.nhom2.social.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository thao tac voi bang users.
 * Su dung derived query va JPQL de tim kiem nguoi dung.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :q, '%')) ORDER BY u.fullName ASC")
    List<User> searchUsers(@Param("q") String q);

    List<User> findTop5ByUsernameNotOrderByCreatedAtDesc(String username);
}

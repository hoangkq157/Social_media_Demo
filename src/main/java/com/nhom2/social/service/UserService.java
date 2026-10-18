package com.nhom2.social.service;

import com.nhom2.social.entity.User;
import com.nhom2.social.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** Kiem tra dang nhap: dung tra ve User, sai tra ve empty. */
    public Optional<User> checkLogin(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(u -> u.getPassword().equals(password));
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}

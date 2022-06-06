package com.example.websocketdemo.service;

import com.example.websocketdemo.entity.User;
import com.example.websocketdemo.exception.CustomException;
import com.example.websocketdemo.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.example.websocketdemo.exception.ErrorCode.*;

@Service
@Transactional
public class UserService {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void save(String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(DUPLICATE_ACCOUNT);
        }
        userRepository.save(new User(email, encoder.encode(password)));
    }

    public void login(String email, String password) {
        userRepository.findByEmail(email).ifPresentOrElse((foundUser) -> {
            if (!encoder.matches(password, foundUser.getPassword())) {
                throw new CustomException(UNAUTHENTICATED_USER);
            }
        }, () -> {
            throw new CustomException(NOT_FOUND_USER);
        });
    }
}

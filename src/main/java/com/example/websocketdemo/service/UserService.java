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
        validateId(id);
        return userRepository.findById(id).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
    }

    public User findByEmail(String email) {
        validateEmail(email);
        return userRepository.findByEmail(email).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
    }

    public void save(String email, String password) {
        validateEmail(email);
        validatePassword(password);

        if (userRepository.existsByEmail(email)) {
            throw new CustomException(DUPLICATE_ACCOUNT);
        }
        userRepository.save(new User(email, encoder.encode(password)));
    }

    public void login(String email, String password) {
        validateEmail(email);
        validatePassword(password);

        userRepository.findByEmail(email).ifPresentOrElse((foundUser) -> {
            if (!encoder.matches(password, foundUser.getPassword())) {
                throw new CustomException(UNAUTHENTICATED_USER);
            }
        }, () -> {
            throw new CustomException(NOT_FOUND_USER);
        });
    }

    private void validateId(String id) {
        if (!isValid(id)) {
            throw new CustomException(INVALID_ID);
        }
    }

    private void validateEmail(String email) {
        if (!isValid(email)) {
            throw new CustomException(INVALID_EMAIL);
        }
    }

    private void validatePassword(String password) {
        if (!isValid(password)) {
            throw new CustomException(INVALID_PASSWORD);
        }
    }

    private boolean isValid(String input) {
        if (input == null || input.isBlank()) {
            return false;
        }
        return true;
    }
}

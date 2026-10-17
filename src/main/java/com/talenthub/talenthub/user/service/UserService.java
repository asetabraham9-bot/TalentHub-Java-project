package com.talenthub.talenthub.user.service;

import com.talenthub.talenthub.user.dto.RegisterRequest;
import com.talenthub.talenthub.user.dto.UserResponse;
import com.talenthub.talenthub.user.model.User;
import com.talenthub.talenthub.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor injection: Spring passes in the beans automatically
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse register(RegisterRequest request) {
        if (request.password() == null || request.password().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
        if (request.email() != null && userRepository.existsByEmail(request.email())) {
            throw new IllegalStateException("Email is already registered");
        }
        User user = User.register(request.fullName(), request.email(),
                passwordEncoder.encode(request.password()), request.role(), LocalDateTime.now());
        return UserResponse.from(userRepository.save(user));
    }
}
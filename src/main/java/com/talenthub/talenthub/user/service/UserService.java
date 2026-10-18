package com.talenthub.talenthub.user.service;

import com.talenthub.talenthub.user.dto.RegisterRequest;
import com.talenthub.talenthub.user.dto.UserResponse;
import com.talenthub.talenthub.user.model.User;
import com.talenthub.talenthub.user.repository.UserRepository;
import com.talenthub.talenthub.common.exception.AccountNotActiveException;
import com.talenthub.talenthub.common.exception.InvalidCredentialsException;
import com.talenthub.talenthub.common.exception.ResourceNotFoundException;
import com.talenthub.talenthub.user.dto.LoginRequest;

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

    public UserResponse getById(Long id) {
    return userRepository.findById(id)
            .map(UserResponse::from)
            .orElseThrow(() -> new ResourceNotFoundException("User " + id + " not found"));
}

public UserResponse login(LoginRequest request) {
    if (request.email() == null || request.password() == null) {
        throw new InvalidCredentialsException();
    }
    User user = userRepository.findByEmail(request.email())
            .orElseThrow(InvalidCredentialsException::new);

    if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
        throw new InvalidCredentialsException();
    }
    // Checked only AFTER the password is correct, so strangers can't probe account status
    if (!user.canLogin()) {
        throw new AccountNotActiveException("Account is " + user.getStatus() + ", login not allowed");
    }
    return UserResponse.from(user);
}
}
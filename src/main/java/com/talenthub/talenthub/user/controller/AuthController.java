package com.talenthub.talenthub.user.controller;

import com.talenthub.talenthub.user.dto.RegisterRequest;
import com.talenthub.talenthub.user.dto.LoginRequest;
import com.talenthub.talenthub.user.dto.UserResponse;
import com.talenthub.talenthub.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public UserResponse login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }
}
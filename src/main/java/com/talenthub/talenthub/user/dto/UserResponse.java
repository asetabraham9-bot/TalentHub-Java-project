package com.talenthub.talenthub.user.dto;

import com.talenthub.talenthub.user.model.AccountStatus;
import com.talenthub.talenthub.user.model.Role;
import com.talenthub.talenthub.user.model.User;

public record UserResponse(Long id, String fullName, String email, Role role, AccountStatus status) {

    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getFullName(), u.getEmail(), u.getRole(), u.getStatus());
    }
}
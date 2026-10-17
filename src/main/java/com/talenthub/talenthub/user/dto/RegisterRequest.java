package com.talenthub.talenthub.user.dto;

import com.talenthub.talenthub.user.model.Role;

public record RegisterRequest(String fullName, String email, String password, Role role) {
}
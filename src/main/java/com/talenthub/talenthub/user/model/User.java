package com.talenthub.talenthub.user.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class User {

    private Long id;
    private final String fullName;
    private final String email;
    private final String passwordHash;
    private final Role role;
    private AccountStatus status;
    private final LocalDateTime createdAt;

    private User(String fullName, String email, String passwordHash,
                 Role role, AccountStatus status, LocalDateTime createdAt) {
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
    }

    /** Public sign-up: only TALENT, ORG_REP and SPONSOR_REP are allowed. */
    public static User register(String fullName, String email, String passwordHash,
                                Role role, LocalDateTime now) {
        validate(fullName, email, passwordHash, role);
        if (!role.isSelfRegistrable()) {
            throw new IllegalArgumentException(role + " accounts cannot self-register");
        }
        AccountStatus initial = role.requiresApproval() ? AccountStatus.PENDING : AccountStatus.ACTIVE;
        return new User(fullName.trim(), normalize(email), passwordHash, role, initial, now);
    }

    /** Created by an admin: any role, active immediately. */
    public static User createByAdmin(String fullName, String email, String passwordHash,
                                     Role role, LocalDateTime now) {
        validate(fullName, email, passwordHash, role);
        return new User(fullName.trim(), normalize(email), passwordHash, role, AccountStatus.ACTIVE, now);
    }

    public void approve() {
        if (status != AccountStatus.PENDING) {
            throw new IllegalStateException("Only a pending account can be approved");
        }
        status = AccountStatus.ACTIVE;
    }

    public void suspend() {
        if (status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Only an active account can be suspended");
        }
        status = AccountStatus.SUSPENDED;
    }

    public boolean canLogin() {
        return status == AccountStatus.ACTIVE;
    }

    private static void validate(String fullName, String email, String passwordHash, Role role) {
        requireText(fullName, "Full name");
        requireText(email, "Email");
        requireText(passwordHash, "Password hash");
        Objects.requireNonNull(role, "Role is required");
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Email is not valid");
        }
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
    }

    private static String normalize(String email) {
        return email.trim().toLowerCase();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }
    public AccountStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public boolean equals(Object o) {
        return o instanceof User other && id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
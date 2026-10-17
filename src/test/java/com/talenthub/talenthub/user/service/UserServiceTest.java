package com.talenthub.talenthub.user.service;

import com.talenthub.talenthub.user.dto.RegisterRequest;
import com.talenthub.talenthub.user.dto.UserResponse;
import com.talenthub.talenthub.user.model.AccountStatus;
import com.talenthub.talenthub.user.model.Role;
import com.talenthub.talenthub.user.model.User;
import com.talenthub.talenthub.user.repository.InMemoryUserRepository;
import com.talenthub.talenthub.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserRepository repository;
    private PasswordEncoder encoder;
    private UserService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
        encoder = new BCryptPasswordEncoder();
        service = new UserService(repository, encoder);
    }

    private RegisterRequest request(String email, Role role) {
        return new RegisterRequest("Abel Tesfaye", email, "secret123", role);
    }

    @Test
    void passwordIsStoredHashedNotRaw() {
        service.register(request("abel@mail.com", Role.TALENT));

        User saved = repository.findByEmail("abel@mail.com").orElseThrow();
        assertNotEquals("secret123", saved.getPasswordHash());
        assertTrue(encoder.matches("secret123", saved.getPasswordHash()));
    }

    @Test
    void registerAssignsIdAndActiveStatusForTalent() {
        UserResponse response = service.register(request("abel@mail.com", Role.TALENT));

        assertNotNull(response.id());
        assertEquals(AccountStatus.ACTIVE, response.status());
    }

    @Test
    void orgRepStartsPending() {
        UserResponse response = service.register(request("org@mail.com", Role.ORG_REP));
        assertEquals(AccountStatus.PENDING, response.status());
    }

    @Test
    void duplicateEmailIsRejectedEvenWithDifferentCase() {
        service.register(request("abel@mail.com", Role.TALENT));

        assertThrows(IllegalStateException.class,
                () -> service.register(request("ABEL@Mail.com", Role.TALENT)));
    }

    @Test
    void shortPasswordIsRejected() {
        RegisterRequest weak = new RegisterRequest("Abel", "abel@mail.com", "123", Role.TALENT);
        assertThrows(IllegalArgumentException.class, () -> service.register(weak));
    }

    @Test
    void adminCannotSelfRegister() {
        assertThrows(IllegalArgumentException.class,
                () -> service.register(request("root@mail.com", Role.ADMIN)));
    }
}
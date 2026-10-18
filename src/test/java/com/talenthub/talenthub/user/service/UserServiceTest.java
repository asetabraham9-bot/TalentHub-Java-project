package com.talenthub.talenthub.user.service;

import com.talenthub.talenthub.user.dto.RegisterRequest;
import com.talenthub.talenthub.user.dto.UserResponse;
import com.talenthub.talenthub.user.model.AccountStatus;
import com.talenthub.talenthub.user.model.Role;
import com.talenthub.talenthub.user.model.User;
import com.talenthub.talenthub.user.repository.InMemoryUserRepository;
import com.talenthub.talenthub.user.repository.UserRepository;
import com.talenthub.talenthub.common.exception.AccountNotActiveException;
import com.talenthub.talenthub.common.exception.InvalidCredentialsException;
import com.talenthub.talenthub.common.exception.ResourceNotFoundException;
import com.talenthub.talenthub.user.dto.LoginRequest;

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

    @Test
void getByIdReturnsUser() {
    UserResponse created = service.register(request("abel@mail.com", Role.TALENT));
    assertEquals("abel@mail.com", service.getById(created.id()).email());
}

@Test
void getByIdThrowsWhenMissing() {
    assertThrows(ResourceNotFoundException.class, () -> service.getById(99L));
}

@Test
void loginSucceedsWithCorrectPassword() {
    service.register(request("abel@mail.com", Role.TALENT));
    UserResponse response = service.login(new LoginRequest("ABEL@mail.com", "secret123"));
    assertEquals("abel@mail.com", response.email());
}

@Test
void wrongPasswordAndUnknownEmailGiveTheSameError() {
    service.register(request("abel@mail.com", Role.TALENT));

    InvalidCredentialsException wrongPassword = assertThrows(InvalidCredentialsException.class,
            () -> service.login(new LoginRequest("abel@mail.com", "wrong-password")));
    InvalidCredentialsException unknownEmail = assertThrows(InvalidCredentialsException.class,
            () -> service.login(new LoginRequest("nobody@mail.com", "secret123")));

    assertEquals(wrongPassword.getMessage(), unknownEmail.getMessage());
}

@Test
void pendingAccountCannotLogin() {
    service.register(request("org@mail.com", Role.ORG_REP));
    assertThrows(AccountNotActiveException.class,
            () -> service.login(new LoginRequest("org@mail.com", "secret123")));
}

@Test
void pendingAccountWithWrongPasswordDoesNotRevealStatus() {
    service.register(request("org@mail.com", Role.ORG_REP));
    assertThrows(InvalidCredentialsException.class,
            () -> service.login(new LoginRequest("org@mail.com", "wrong-password")));
}
}
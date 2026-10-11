package com.talenthub.talenthub.user.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private final LocalDateTime now = LocalDateTime.of(2026, 10, 12, 9, 0);

    @Test
    void talentIsActiveImmediately() {
        User u = User.register("Abel Tesfaye", "  Abel@Mail.com ", "hash", Role.TALENT, now);
        assertEquals(AccountStatus.ACTIVE, u.getStatus());
        assertEquals("abel@mail.com", u.getEmail());
        assertTrue(u.canLogin());
    }

    @Test
    void orgRepStartsPendingUntilApproved() {
        User u = User.register("Meron Alemu", "meron@org.et", "hash", Role.ORG_REP, now);
        assertEquals(AccountStatus.PENDING, u.getStatus());
        assertFalse(u.canLogin());

        u.approve();
        assertTrue(u.canLogin());
    }

    @Test
    void adminCannotSelfRegister() {
        assertThrows(IllegalArgumentException.class,
                () -> User.register("Eve", "eve@mail.com", "hash", Role.ADMIN, now));
    }

    @Test
    void invalidEmailIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> User.register("Abel", "not-an-email", "hash", Role.TALENT, now));
    }

    @Test
    void suspendedUserCannotLogin() {
        User u = User.createByAdmin("Judge One", "judge@mail.com", "hash", Role.JUDGE, now);
        u.suspend();
        assertFalse(u.canLogin());
    }
}
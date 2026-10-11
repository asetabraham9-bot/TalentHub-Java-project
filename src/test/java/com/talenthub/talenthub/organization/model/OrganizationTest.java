package com.talenthub.talenthub.organization.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OrganizationTest {

    private final LocalDateTime now = LocalDateTime.of(2026, 10, 12, 9, 0);

    private Organization sample() {
        return new Organization("Gesuba Farmers Union", "Agriculture cooperative", "info@gfu.et");
    }

    @Test
    void newOrganizationCannotPublishChallenges() {
        assertFalse(sample().canPublishChallenges());
    }

    @Test
    void approvedOrganizationCanPublishChallenges() {
        Organization org = sample();
        org.approve(now);
        assertTrue(org.canPublishChallenges());
    }

    @Test
    void rejectionRequiresReason() {
        assertThrows(IllegalArgumentException.class, () -> sample().reject(" ", now));
    }

    @Test
    void approvedOrganizationCannotBeRejected() {
        Organization org = sample();
        org.approve(now);
        assertThrows(IllegalStateException.class, () -> org.reject("changed mind", now));
    }
}
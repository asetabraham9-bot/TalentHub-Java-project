package com.talenthub.talenthub.challenge.model;

import com.talenthub.talenthub.organization.model.Organization;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChallengeTest {

    private final LocalDateTime now = LocalDateTime.of(2026, 10, 12, 9, 0);

    private Organization approvedOrg() {
        Organization org = new Organization("Gesuba Farmers Union", "Agriculture", "info@gfu.et");
        org.approve(now);
        return org;
    }

    private Challenge sampleChallenge(Organization org, LocalDateTime deadline) {
        return Challenge.draft(org, "Crop price tracker", "Build a price board for farmers",
                List.of("Java", " java ", "SQL"), deadline,
                "Working prototype and short demo video", "Certificate");
    }

    @Test
    void unapprovedOrganizationCannotCreateChallenge() {
        Organization pending = new Organization("New Org", "desc", "a@org.et");
        assertThrows(IllegalStateException.class,
                () -> sampleChallenge(pending, now.plusDays(10)));
    }

    @Test
    void skillsAreNormalizedAndDeduplicated() {
        Challenge c = sampleChallenge(approvedOrg(), now.plusDays(10));
        assertEquals(List.of("java", "sql"), c.getRequiredSkills());
    }

    @Test
    void cannotPublishWithPastDeadline() {
        Challenge c = sampleChallenge(approvedOrg(), now.minusDays(1));
        assertThrows(IllegalStateException.class, () -> c.publish(now));
    }

    @Test
    void acceptsSolutionsOnlyWhilePublishedAndBeforeDeadline() {
        Challenge c = sampleChallenge(approvedOrg(), now.plusDays(10));
        assertFalse(c.acceptsSolutions(now));                  // still a draft

        c.publish(now);
        assertTrue(c.acceptsSolutions(now.plusDays(1)));
        assertFalse(c.acceptsSolutions(now.plusDays(11)));     // after the deadline

        c.close();
        assertFalse(c.acceptsSolutions(now.plusDays(1)));      // closed
    }

    @Test
    void cannotCloseADraft() {
        Challenge c = sampleChallenge(approvedOrg(), now.plusDays(10));
        assertThrows(IllegalStateException.class, c::close);
    }
}
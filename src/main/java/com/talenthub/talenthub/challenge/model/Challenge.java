package com.talenthub.talenthub.challenge.model;

import com.talenthub.talenthub.common.util.SkillUtils;
import com.talenthub.talenthub.organization.model.Organization;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Challenge {

    private Long id;
    private final Organization organization;
    private final String title;
    private final String description;
    private final List<String> requiredSkills;
    private final LocalDateTime deadline;
    private final String expectedDeliverables;
    private final String rewardDescription; // optional
    private ChallengeStatus status = ChallengeStatus.DRAFT;

    private Challenge(Organization organization, String title, String description,
                      List<String> requiredSkills, LocalDateTime deadline,
                      String expectedDeliverables, String rewardDescription) {
        this.organization = organization;
        this.title = title.trim();
        this.description = description.trim();
        this.requiredSkills = requiredSkills;
        this.deadline = deadline;
        this.expectedDeliverables = expectedDeliverables.trim();
        this.rewardDescription = rewardDescription;
    }

    /** Design rule 2: only APPROVED organizations can publish challenges. */
    public static Challenge draft(Organization organization, String title, String description,
                                  List<String> requiredSkills, LocalDateTime deadline,
                                  String expectedDeliverables, String rewardDescription) {
        Objects.requireNonNull(organization, "Organization is required");
        Objects.requireNonNull(deadline, "Deadline is required");
        requireText(title, "Title");
        requireText(description, "Description");
        requireText(expectedDeliverables, "Expected deliverables");
        if (!organization.canPublishChallenges()) {
            throw new IllegalStateException("Only approved organizations can create challenges");
        }
        return new Challenge(organization, title, description,
                SkillUtils.normalizeAll(requiredSkills), deadline,
                expectedDeliverables, rewardDescription);
    }

    public void publish(LocalDateTime now) {
        if (status != ChallengeStatus.DRAFT) {
            throw new IllegalStateException("Only a draft challenge can be published");
        }
        if (!deadline.isAfter(now)) {
            throw new IllegalStateException("Deadline must be in the future to publish");
        }
        if (!organization.canPublishChallenges()) {
            throw new IllegalStateException("Organization is no longer approved");
        }
        status = ChallengeStatus.PUBLISHED;
    }

    public void close() {
        if (status != ChallengeStatus.PUBLISHED) {
            throw new IllegalStateException("Only a published challenge can be closed");
        }
        status = ChallengeStatus.CLOSED;
    }

    /** Design rule 3: nobody can submit after the deadline. */
    public boolean acceptsSolutions(LocalDateTime now) {
        return status == ChallengeStatus.PUBLISHED && now.isBefore(deadline);
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Organization getOrganization() { return organization; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public List<String> getRequiredSkills() { return requiredSkills; }
    public LocalDateTime getDeadline() { return deadline; }
    public String getExpectedDeliverables() { return expectedDeliverables; }
    public String getRewardDescription() { return rewardDescription; }
    public ChallengeStatus getStatus() { return status; }

    @Override
    public boolean equals(Object o) {
        return o instanceof Challenge other && id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
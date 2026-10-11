package com.talenthub.talenthub.organization.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Organization {

    private Long id;
    private final String name;
    private final String description;
    private final String contactEmail;
    private OrganizationStatus status = OrganizationStatus.PENDING;
    private String rejectionReason;
    private LocalDateTime reviewedAt;

    public Organization(String name, String description, String contactEmail) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Organization name is required");
        }
        if (contactEmail == null || !contactEmail.contains("@")) {
            throw new IllegalArgumentException("A valid contact email is required");
        }
        this.name = name.trim();
        this.description = description;
        this.contactEmail = contactEmail.trim().toLowerCase();
    }

    public void approve(LocalDateTime now) {
        if (status == OrganizationStatus.APPROVED) {
            throw new IllegalStateException("Organization is already approved");
        }
        status = OrganizationStatus.APPROVED;
        rejectionReason = null;
        reviewedAt = now;
    }

    public void reject(String reason, LocalDateTime now) {
        if (status != OrganizationStatus.PENDING) {
            throw new IllegalStateException("Only a pending organization can be rejected");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("A rejection reason is required");
        }
        status = OrganizationStatus.REJECTED;
        rejectionReason = reason.trim();
        reviewedAt = now;
    }

    /** Design rule 2: only APPROVED organizations can publish challenges. */
    public boolean canPublishChallenges() {
        return status == OrganizationStatus.APPROVED;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getContactEmail() { return contactEmail; }
    public OrganizationStatus getStatus() { return status; }
    public String getRejectionReason() { return rejectionReason; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }

    @Override
    public boolean equals(Object o) {
        return o instanceof Organization other && id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
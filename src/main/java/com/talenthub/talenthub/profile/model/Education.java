package com.talenthub.talenthub.profile.model;

public record Education(String institution, String fieldOfStudy, int startYear, Integer endYear) {

    // Compact constructor: runs on every creation, so invalid data can't exist
    public Education {
        if (institution == null || institution.isBlank()) {
            throw new IllegalArgumentException("Institution is required");
        }
        if (endYear != null && endYear < startYear) {
            throw new IllegalArgumentException("End year cannot be before start year");
        }
    }

    public boolean isOngoing() {
        return endYear == null;
    }
}
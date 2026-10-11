package com.talenthub.talenthub.profile.model;

public record Achievement(String title, int year) {

    public Achievement {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Achievement title is required");
        }
    }
}
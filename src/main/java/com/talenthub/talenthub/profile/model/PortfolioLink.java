package com.talenthub.talenthub.profile.model;

public record PortfolioLink(LinkType type, String url) {

    public PortfolioLink {
        if (type == null) {
            throw new IllegalArgumentException("Link type is required");
        }
        if (url == null || !url.startsWith("https://")) {
            throw new IllegalArgumentException("Link must start with https://");
        }
    }
}
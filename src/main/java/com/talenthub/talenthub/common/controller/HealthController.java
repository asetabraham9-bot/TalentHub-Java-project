package com.talenthub.talenthub.common.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    public record HealthResponse(String status, String service) {
    }

    @GetMapping("/")
    public String home() {
        return "TalentHub is running. Empowering talents in Gesuba, Wolaita Zone.";
    }

    @GetMapping("/api/health")
    public HealthResponse health() {
        return new HealthResponse("UP", "TalentHub");
    }
}
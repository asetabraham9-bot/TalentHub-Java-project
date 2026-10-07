package com.talenthub.talenthub.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.talenthub.talenthub.dto.response.HealthResponse;
@RestController
public class HelloController {

    @GetMapping("/")
    public String home() {
        return "TalentHub: Gesuba's home for hackathons.";
    }

    @GetMapping("/api/health")
    public HealthResponse health() {
        return new HealthResponse("UP","TalentHub");
    }
}
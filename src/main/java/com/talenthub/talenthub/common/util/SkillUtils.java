package com.talenthub.talenthub.common.util;

import java.util.Collection;
import java.util.List;

public final class SkillUtils {

    private SkillUtils() {
    }

    public static String normalize(String skill) {
        if (skill == null || skill.isBlank()) {
            throw new IllegalArgumentException("Skill must not be blank");
        }
        return skill.trim().toLowerCase();
    }

    public static List<String> normalizeAll(Collection<String> skills) {
        if (skills == null) {
            return List.of();
        }
        return skills.stream().map(SkillUtils::normalize).distinct().toList();
    }
}
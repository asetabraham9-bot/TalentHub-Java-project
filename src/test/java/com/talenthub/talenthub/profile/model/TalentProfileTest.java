package com.talenthub.talenthub.profile.model;

import com.talenthub.talenthub.user.model.Role;
import com.talenthub.talenthub.user.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TalentProfileTest {

    private final LocalDateTime now = LocalDateTime.of(2026, 10, 12, 9, 0);

    private User talent() {
        return User.register("Abel Tesfaye", "abel@mail.com", "hash", Role.TALENT, now);
    }

    @Test
    void onlyTalentUsersGetProfiles() {
        User judge = User.createByAdmin("Judge One", "judge@mail.com", "hash", Role.JUDGE, now);
        assertThrows(IllegalArgumentException.class, () -> TalentProfile.createFor(judge));
    }

    @Test
    void skillsAreNormalizedAndUnique() {
        TalentProfile p = TalentProfile.createFor(talent());
        p.addSkill("Java");
        p.addSkill(" java ");
        p.addSkill("React");
        assertEquals(2, p.getSkills().size());
        assertTrue(p.getSkills().contains("java"));
    }

    @Test
    void skillSetCannotBeModifiedFromOutside() {
        TalentProfile p = TalentProfile.createFor(talent());
        assertThrows(UnsupportedOperationException.class, () -> p.getSkills().add("hack"));
    }

    @Test
    void completenessGrowsAsProfileIsFilled() {
        TalentProfile p = TalentProfile.createFor(talent());
        assertEquals(0, p.completeness());

        p.updateBasics("Java developer", "Student from Gesuba", "Gesuba");
        p.addSkill("java");
        p.addSkill("sql");
        p.addSkill("git");
        p.addEducation(new Education("Wolaita Sodo University", "Computer Science", 2023, null));
        p.addPortfolioLink(new PortfolioLink(LinkType.GITHUB, "https://github.com/abel"));
        p.addAchievement(new Achievement("Won campus coding contest", 2025));
        assertEquals(100, p.completeness());
    }

    @Test
    void skillMatchPercentComparesWithRequiredSkills() {
        TalentProfile p = TalentProfile.createFor(talent());
        p.addSkill("java");
        p.addSkill("sql");
        assertEquals(50, p.skillMatchPercent(List.of("Java", "React")));
        assertEquals(0, p.skillMatchPercent(List.of()));
    }

    @Test
    void invalidValueObjectsAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Education("University", "CS", 2024, 2020));
        assertThrows(IllegalArgumentException.class,
                () -> new PortfolioLink(LinkType.GITHUB, "http://insecure.com"));
    }
}
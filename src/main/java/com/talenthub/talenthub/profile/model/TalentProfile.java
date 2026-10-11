package com.talenthub.talenthub.profile.model;

import com.talenthub.talenthub.common.util.SkillUtils;
import com.talenthub.talenthub.user.model.Role;
import com.talenthub.talenthub.user.model.User;

import java.util.*;

public class TalentProfile {

    private Long id;
    private final User user;
    private String headline;
    private String bio;
    private String location;
    private boolean visibleToOrganizations = true;
    private final Set<String> skills = new LinkedHashSet<>();
    private final List<Education> educations = new ArrayList<>();
    private final List<Achievement> achievements = new ArrayList<>();
    private final List<PortfolioLink> portfolioLinks = new ArrayList<>();

    private TalentProfile(User user) {
        this.user = user;
    }

    public static TalentProfile createFor(User user) {
        Objects.requireNonNull(user, "User is required");
        if (user.getRole() != Role.TALENT) {
            throw new IllegalArgumentException("Only TALENT users have a talent profile");
        }
        return new TalentProfile(user);
    }

    public void updateBasics(String headline, String bio, String location) {
        this.headline = headline;
        this.bio = bio;
        this.location = location;
    }

    public void addSkill(String skill) {
        skills.add(SkillUtils.normalize(skill));
    }

    public void removeSkill(String skill) {
        skills.remove(SkillUtils.normalize(skill));
    }

    public void addEducation(Education education) {
        educations.add(Objects.requireNonNull(education));
    }

    public void addAchievement(Achievement achievement) {
        achievements.add(Objects.requireNonNull(achievement));
    }

    public void addPortfolioLink(PortfolioLink link) {
        portfolioLinks.add(Objects.requireNonNull(link));
    }

    public void setVisibleToOrganizations(boolean visible) {
        this.visibleToOrganizations = visible;
    }

    /** Profile completeness, 0 to 100. Used later for the talent dashboard. */
    public int completeness() {
        int score = 0;
        if (headline != null && !headline.isBlank()) score += 15;
        if (bio != null && !bio.isBlank()) score += 15;
        if (skills.size() >= 3) score += 25;
        if (!educations.isEmpty()) score += 20;
        if (!portfolioLinks.isEmpty()) score += 15;
        if (!achievements.isEmpty()) score += 10;
        return score;
    }

    /** Talent discovery: percentage of the required skills this talent has (0 to 100). */
    public int skillMatchPercent(Collection<String> requiredSkills) {
        List<String> required = SkillUtils.normalizeAll(requiredSkills);
        if (required.isEmpty()) {
            return 0;
        }
        long matched = required.stream().filter(skills::contains).count();
        return (int) (matched * 100 / required.size());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public String getHeadline() { return headline; }
    public String getBio() { return bio; }
    public String getLocation() { return location; }
    public boolean isVisibleToOrganizations() { return visibleToOrganizations; }

    // Read-only views: outside code must go through addX/removeX
    public Set<String> getSkills() { return Collections.unmodifiableSet(skills); }
    public List<Education> getEducations() { return Collections.unmodifiableList(educations); }
    public List<Achievement> getAchievements() { return Collections.unmodifiableList(achievements); }
    public List<PortfolioLink> getPortfolioLinks() { return Collections.unmodifiableList(portfolioLinks); }

    @Override
    public boolean equals(Object o) {
        return o instanceof TalentProfile other && id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
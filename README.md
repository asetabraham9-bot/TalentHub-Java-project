
# TalentHub

A platform connecting students and young talents around Gesuba, Wolaita Zone, South Ethiopia Region
with hackathons and real-world challenges posted by organizations.

**Stack:** Java 21, Spring Boot, PostgreSQL, React
Health check: GET /api/health

Review file by file: by saying:

1. does this do what the PR says?
2. are there bugs or edge cases?
3. is it readable?
4. are there tests?

// Priject folder and file structure

com.talenthub.talenthub/
├── TalenthubApplication.java
├── common/          ← exceptions, util, base classes, shared config
├── security/        ← JWT, filters, security config
├── user/            ← User, Role, auth
├── profile/         ← TalentProfile, Skill, discovery
├── organization/    ← Organization, approval
├── challenge/       ← Challenge, ChallengeSolution
├── hackathon/       ← Hackathon, Team, Registration, Submission, judging
├── mentoring/       ← Project, Milestone, ProgressUpdate, feedback
├── sponsor/         ← Sponsor, Award, Certificate, Opportunity
└── notification/    ← Notification, reports
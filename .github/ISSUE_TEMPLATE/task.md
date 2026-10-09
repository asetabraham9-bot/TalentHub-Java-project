---
name: Task
about: A piece of work for the team
labels: ''
---

## What
Add a GitHub Actions CI workflow to automatically build and validate the application on every push and pull request.
<br>

## Why
Automated continuous integration helps detect issues early, ensures the project remains buildable, and increases confidence in code quality before merging changes.
<br>

## Acceptance criteria
- [ ] File `.github/workflows/ci.yml` exists
- [ ] Workflow runs on push and pull request events
- [ ] The application builds successfully in the GitHub Actions environment
- [ ] Workflow results are visible in the GitHub Actions tab
- [ ] Pull requests display CI status checks
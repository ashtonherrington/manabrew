# Roadmap

What is done, in progress, and planned. Update this file in the same PR that
changes an item's status.

Legend: ✅ done · 🚧 in progress · ⏸️ built but disabled · ⬜ not started

## Foundation

| Item | Status | Where / notes |
|---|---|---|
| Repo, license, README | ✅ | |
| Branch protection (`protect-default` ruleset) | ✅ | PR required, no force push or deletion on `main` |
| Required CI checks on `main` | ⬜ | Add `style` + `backend-ci` checks to the ruleset |
| Conventions docs, scoped per module | ✅ | `docs/conventions.md`, `<module>/CONVENTIONS.md` |
| Reviewer skill + feedback log + post-push retro | ✅ | `.claude/skills/reviewer`, `docs/review/feedback-log.md` |
| Reviewer posts inline PR comments; harvest owner verdicts | 🚧 | Not yet exercised on a real PR |
| Skeptic second reviewer (disproves findings before posting) | 🚧 | `.claude/agents/review-skeptic.md`; not yet exercised |
| Automatic pre-push review hook | ⬜ | Proposed, awaiting decision |
| `@claude` GitHub Action | ⬜ | Optional; needs `/install-github-app` by owner |
| Google Java Style enforcement (save, Claude, commit, push, CI) | ✅ | |

## Backend

| Item | Status | Where / notes |
|---|---|---|
| Spring Boot 4 skeleton + health endpoint | ✅ | `backend/` |
| Testcontainers integration test | ✅ | Passing in CI |
| Docker Compose (Postgres + backend) | ✅ | Not yet run locally: Docker Desktop wouldn't start |
| jOOQ code generation | ⬜ | Lands with the first Flyway migration |
| ManaBox CSV import | ⬜ | First feature |
| Scryfall card data | ⬜ | |
| Claude deck suggestions | ⬜ | |
| Monte Carlo goldfish simulation | ⬜ | |

## Frontend

| Item | Status | Where / notes |
|---|---|---|
| React + TypeScript skeleton | ⬜ | Separate PR |

## CI/CD

| Item | Status | Where / notes |
|---|---|---|
| Style check on every push | ✅ | `style.yml` |
| Tests on pushes and PRs to `main` | ✅ | `backend-ci.yml` → `backend-test.yml` |
| Terraform validate + Helm lint | 🚧 | `infra-ci.yml` (this PR) |
| Build/publish image on merge to `main` | ⏸️ | `backend-image.yml`; enable with repo variable `IMAGE_PUBLISH_ENABLED=true` |
| Deploy to QA / production | ⬜ | Needs a cloud provider and cluster |

## Infrastructure

| Item | Status | Where / notes |
|---|---|---|
| Terraform layout (`modules/`, `envs/local,qa,prod`) | 🚧 | Structure only (this PR) |
| Helm hierarchy: library → service → umbrella | 🚧 | Templates for Deployment + Service (this PR) |
| Choose cloud provider (AWS or GCP) | ⬜ | No preference yet |
| Terraform modules: network, cluster, postgres | ⬜ | After provider choice |
| Local Kubernetes (kind/k3d) for chart testing | ⬜ | Optional; see `infra/terraform/README.md` |
| Secrets management per environment | ⬜ | |

## Open decisions

- Cloud provider and managed Postgres offering.
- Whether Terraform has a local role (see `infra/terraform/README.md`).
- Where Claude prompts live and how responses are validated.
- Frontend structure.

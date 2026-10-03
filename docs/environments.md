# Environments

| Environment | Status | Runs where | Deployed by |
|---|---|---|---|
| `local` | **Active** | Docker Compose on a developer machine | `docker compose up --build` |
| `qa` | Planned | Cloud (provider TBD) | Merge to `main` |
| `production` | Planned | Cloud (provider TBD) | Manual approval of a release |

## CI/CD pipeline

```
push / PR ──► style.yml ─────────── spotlessCheck
          └─► backend-ci.yml ─────► backend-test.yml (reusable)

planned:
merge to main ─► deploy-qa.yml ───► backend-test.yml ─► build image ─► deploy (environment: qa)
release ───────► deploy-prod.yml ─► backend-test.yml ─► promote qa image ─► deploy (environment: production, required reviewer)
```

Design choices that keep this extensible:

- **Tests are a reusable workflow** (`backend-test.yml`, `workflow_call`), so
  deploy workflows reuse the exact same gate instead of copying steps.
- **One workflow per module**, filtered by path. When modules split into
  separate services or libraries, each takes its own workflows with it.
- **GitHub Environments** (`qa`, `production`) will hold each environment's
  secrets and protection rules; `production` gets a required reviewer.
- **Build once, promote.** Production deploys the image already tested in QA,
  never a rebuild.

## Configuration per environment

The app reads all settings from environment variables, with local defaults in
`application.yml`:

- `local`: defaults in `application.yml` plus `.env` for Docker Compose.
- `qa` / `production`: Spring profiles (`application-qa.yml`,
  `application-prod.yml`) for non-secret differences; secrets come from the
  cloud provider's secret manager, injected as environment variables. Never
  commit secrets.

## Not yet decided

- Cloud provider (AWS or GCP), container platform, and managed Postgres.
- Whether to split fast unit tests from Testcontainers integration tests
  (JUnit tags) once the suite grows.

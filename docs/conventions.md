# Conventions

How manabrew code is structured and written. These are living documents: when
we settle a pattern in review, it gets recorded, and new code follows it.

## Where rules live

Rules are scoped so each module can later move to its own repository with its
rules attached:

| File | Scope |
|---|---|
| `docs/conventions.md` (this file) | Cross-cutting: every module |
| `<module>/CONVENTIONS.md` | One module, e.g. [`backend/CONVENTIONS.md`](../backend/CONVENTIONS.md) |
| `docs/review/feedback-log.md` | Raw review feedback, each entry tagged with its module |

New rules are proposed by the reviewer skill (`.claude/skills/reviewer/`) from
logged feedback and only added once the owner approves them.

Items marked **(proposed)** are starting points awaiting review.

## Modules

| Module | What | Future shape |
|---|---|---|
| `backend` | Spring Boot API | Splits by feature into services and libraries |

Every module is self-contained: its own build, `Dockerfile` where it runs as a
service, `CONVENTIONS.md`, and CI workflow filtered to its path.

## Formatting enforcement

Java follows the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html),
applied by google-java-format through the Spotless Gradle plugin. One formatter,
enforced at every checkpoint:

| Checkpoint | How |
|---|---|
| Save in VS Code | Spotless Gradle extension (`.vscode/settings.json`) |
| Claude edits a Java file | PostToolUse hook runs `spotlessApply` (`.claude/settings.json`) |
| `git commit` | `.githooks/pre-commit` runs `spotlessCheck` on staged Java |
| `git push` | `.githooks/pre-push` runs `spotlessCheck` |
| Push to any branch / merge to main | `style` GitHub Action runs `spotlessCheck` |

## Workflow

- Small, focused PRs: one skeleton or feature per PR.
- When a pattern is agreed in review, update the matching conventions file in
  the same PR.

## Open questions

- Claude integration: where prompts live (code vs. resource files) and how
  responses are validated.
- Frontend structure (separate PR).

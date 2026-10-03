# Conventions

How manabrew code is structured and written. This is a living document: when we
settle a pattern in review, it gets recorded here, and new code follows it.

Items marked **(proposed)** are starting points awaiting review.

## Backend (Java 21, Spring Boot 4)

### Package layout — package by feature (proposed)

```
com.manabrew
├── ManabrewApplication.java
├── collection/     # ManaBox CSV import, owned cards
├── card/           # Scryfall card data
├── deck/           # decks and Claude-generated deck ideas
├── simulation/     # Monte Carlo goldfish playouts
└── common/         # cross-cutting code only (error handling, config)
```

Each feature package holds its own controller, service, repository, and types.
Classes are package-private unless another feature needs them. Features talk to
each other through a service's public methods, never another feature's repository.

### Layers within a feature (proposed)

| Class | Responsibility |
|---|---|
| `XController` | HTTP only: parse/validate request, call service, map to response. No business logic. |
| `XService` | Business logic and transactions. |
| `XRepository` | SQL via Spring `JdbcClient`. |
| records | Request/response DTOs and domain types are Java `record`s. |

### Persistence

- **Flyway owns the schema.** Changes go through migrations in
  `src/main/resources/db/migration`, named `V<n>__<description>.sql`, applied
  automatically at startup. Never edit a migration that has been merged.
- **jOOQ owns queries.** Type-safe SQL built in Java, no JPA/Hibernate. jOOQ
  classes are generated from the schema the Flyway migrations produce, so a
  renamed column is a compile error rather than a runtime failure.
- Codegen setup arrives with the first migration (first feature PR).

### API (proposed)

- REST endpoints under `/api/v1/...`, JSON in and out.
- Errors use RFC 9457 Problem Details (`spring.mvc.problemdetails.enabled`).
- Validate request bodies with Bean Validation annotations and `@Valid`.

### Style

- Formatting follows the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html),
  applied by google-java-format (2-space indent) through the Spotless Gradle
  plugin. One formatter, enforced at every checkpoint:

  | Checkpoint | How |
  |---|---|
  | Save in VS Code | Spotless Gradle extension (`.vscode/settings.json`) |
  | Claude edits a Java file | PostToolUse hook runs `spotlessApply` (`.claude/settings.json`) |
  | `git commit` | `.githooks/pre-commit` runs `spotlessCheck` on staged Java |
  | `git push` | `.githooks/pre-push` runs `spotlessCheck` |
  | Push to any branch / merge to main | `style` GitHub Action runs `spotlessCheck` |

  Fix violations with `./gradlew spotlessApply`.
- Constructor injection only; no field `@Autowired` in production code.
- Configuration comes from environment variables with local defaults in
  `application.yml`. No secrets in the repository.

### Testing (proposed)

- Integration tests run against real Postgres via Testcontainers, never H2.
- Controller tests use `MockMvcTester` with AssertJ.
- The Testcontainers Postgres image matches `docker-compose.yml`.

## Open questions

- Claude integration: where prompts live (code vs. resource files) and how
  responses are validated.
- Frontend structure (separate PR).

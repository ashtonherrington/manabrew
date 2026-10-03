# Backend conventions

Rules for the `backend` module (Java 21, Spring Boot 4). Cross-cutting rules
that apply to every module live in [`docs/conventions.md`](../docs/conventions.md).

Items marked **(proposed)** are starting points awaiting review.

## Package layout: package by feature (proposed)

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
Classes are package-private unless another feature needs them.

## Split-ready boundaries (proposed)

Features will later become separate services or libraries, so keep them
separable now:

- Features talk to each other only through a service's public methods, never
  another feature's repository or tables.
- Each feature owns its tables. No cross-feature SQL joins; combine data in a
  service instead.
- `common/` stays small and dependency-light, since it is the likeliest to
  become a shared library. Business logic never goes in `common/`.
- `simulation/` is pure computation: no Spring, database, or HTTP dependencies,
  so it can become a plain library.

## Layers within a feature (proposed)

| Class | Responsibility |
|---|---|
| `XController` | HTTP only: parse/validate request, call service, map to response. No business logic. |
| `XService` | Business logic and transactions. |
| `XRepository` | SQL via jOOQ's `DSLContext`. |
| records | Request/response DTOs and domain types are Java `record`s. |

## Persistence

- **Flyway owns the schema.** Changes go through migrations in
  `src/main/resources/db/migration`, named `V<n>__<description>.sql`, applied
  automatically at startup. Never edit a migration that has been merged.
- **jOOQ owns queries.** Type-safe SQL built in Java, no JPA/Hibernate. jOOQ
  classes are generated from the schema the Flyway migrations produce, so a
  renamed column is a compile error rather than a runtime failure.
- Codegen setup arrives with the first migration (first feature PR).

## API (proposed)

- REST endpoints under `/api/v1/...`, JSON in and out.
- Errors use RFC 9457 Problem Details (`spring.mvc.problemdetails.enabled`).
- Validate request bodies with Bean Validation annotations and `@Valid`.

## Code

- Formatting: [Google Java Style](https://google.github.io/styleguide/javaguide.html)
  via google-java-format (see enforcement in `docs/conventions.md`). Fix with
  `./gradlew spotlessApply`.
- Constructor injection only; no field `@Autowired` in production code.
- Configuration comes from environment variables with local defaults in
  `application.yml`. No secrets in the repository.

## Testing (proposed)

- Integration tests run against real Postgres via Testcontainers, never H2.
- Controller tests use `MockMvcTester` with AssertJ.
- The Testcontainers Postgres image matches `docker-compose.yml`.

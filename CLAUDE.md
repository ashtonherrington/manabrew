# manabrew

MTG deck-brewing app: ManaBox collection CSV + Scryfall card data + Claude.

- Follow `docs/conventions.md`. When a pattern is agreed in review, update that
  file in the same PR.
- The owner reviews all backend code and knows Java well: favor plain, explicit
  code over clever abstractions or framework magic.
- Keep PRs small and focused; one skeleton or feature per PR.

## Commands

- One-time setup after cloning: `git config core.hooksPath .githooks`
- Run everything: `docker compose up --build` (copy `.env.example` to `.env` first)
- Backend build + tests: `cd backend && ./gradlew build` (needs Docker running)
- Fix formatting: `cd backend && ./gradlew spotlessApply`
- Health check: http://localhost:8080/actuator/health

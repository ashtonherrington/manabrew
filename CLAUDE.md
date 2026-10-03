# manabrew

MTG deck-brewing app: ManaBox collection CSV + Scryfall card data + Claude.

- Rules: `docs/conventions.md` (all modules) plus `<module>/CONVENTIONS.md` for
  each module you touch, e.g. `backend/CONVENTIONS.md`, `infra/CONVENTIONS.md`.
- Progress: `ROADMAP.md`. Read it at the start of a session to see what's done;
  update an item's status in the same PR that changes it.
- The owner reviews all backend code and knows Java well: favor plain, explicit
  code over clever abstractions or framework magic.
- The codebase will split into separate services and libraries. Keep modules
  self-contained and features separable (see "Split-ready boundaries" in
  `backend/CONVENTIONS.md`).
- When the owner corrects code you wrote, use the `reviewer` skill to log it in
  `docs/review/feedback-log.md`, even if they don't ask. Rules grow from that
  log through retros, with the owner's approval.
- Keep PRs small and focused; one skeleton or feature per PR.

## Commands

- One-time setup after cloning: `git config core.hooksPath .githooks`
- Run everything: `docker compose up --build` (copy `.env.example` to `.env` first)
- Backend build + tests: `cd backend && ./gradlew build` (needs Docker running)
- Fix formatting: `cd backend && ./gradlew spotlessApply`
- Health check: http://localhost:8080/actuator/health

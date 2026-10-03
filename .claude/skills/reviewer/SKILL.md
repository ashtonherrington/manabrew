---
name: reviewer
description: Review code against this repo's conventions, log the owner's corrections, and run retros that propose new rules from that feedback. Use when asked to review a diff, branch, or PR; whenever the owner says code Claude wrote is wrong, unclear, or not how they want it (in chat or as PR review comments) — log it even if they don't ask; and when a post-push hook asks for a retro.
---

# Reviewer

Three modes. The goal is that rules accumulate from real feedback, so the owner
never has to write every rule up front.

The skill is repo-agnostic. It finds rules and the log by convention:

- **Rules:** `docs/conventions.md` (cross-cutting) plus `<module>/CONVENTIONS.md`
  for each module a change touches. A module is a top-level directory with its
  own `CONVENTIONS.md`. Also read the root `CLAUDE.md`.
- **Feedback log:** `docs/review/feedback-log.md`. Its header defines the entry
  format; follow it exactly.

## Mode 1: Review

When asked to review a diff, branch, or PR.

1. Get the change: `git diff main...HEAD` for the current branch, or
   `gh pr diff <n>` for a PR. List the touched modules.
2. Read the cross-cutting rules and the `CONVENTIONS.md` of every touched module.
3. Review for, in priority order:
   1. Correctness bugs: wrong behavior, unhandled failure paths, data loss.
   2. Rule violations: cite the exact rule (`backend/CONVENTIONS.md#Layers`).
   3. Readability for the owner, who reviews all backend code: favor explicit,
      plain code over clever abstractions or framework magic.
   4. Split-readiness: anything coupling modules or features that the
      conventions say must stay separable.
   Skip pure formatting; the formatter owns it.
4. Report findings most-severe first, each with `file:line`, the problem, why it
   matters, and the rule (or "no rule" if it is judgment).
5. Anything you find that no rule covers but likely reflects a lasting
   preference: log it with `source: self-review` (Mode 2) so a retro can weigh it.

## Mode 2: Learn

Whenever the owner corrects code Claude wrote — in chat, or in PR comments
(`gh api repos/{owner}/{repo}/pulls/<n>/comments`) — even if they don't ask
you to log it:

1. Fix the code as asked.
2. Append one entry per distinct correction to the feedback log, using its
   format. Quote the owner's words exactly. Record whether an existing rule
   already covered it (if so, the miss is Claude's, not a missing rule).
3. Say in one line that it was logged. Do not propose rules yet; that is the
   retro's job, after more than one data point.

## Mode 3: Retro

Triggered by the post-push hook, or when the owner asks.

1. Read log entries with `status: open`. If there are none, say so in one line
   and stop.
2. Also pull new PR review comments from the owner on the current branch's PR
   that aren't logged yet; log them first (Mode 2).
3. Group open entries by module and category, and look for the underlying
   preference, not the surface fix.
4. Propose rule changes. For each proposal give:
   - target file and section (module file for module-specific rules,
     `docs/conventions.md` only if it applies to every module)
   - the rule text, written as a specific, checkable instruction
   - the log entries it comes from
   - add, update (show old → new), or remove
5. Thresholds:
   - An explicit owner statement of preference ("always…", "never…", "I want…")
     can become a rule from a single entry.
   - An inferred pattern needs two or more entries.
   - Entries already covered by a rule are execution misses: propose
     sharpening the existing rule's wording instead of adding a duplicate.
   - Prefer updating or merging rules over adding new ones; flag rules that
     feedback contradicts or that no longer pull their weight.
6. Ask the owner which proposals to accept. Never edit conventions files
   without approval.
7. For accepted proposals: edit the conventions file, and set each source
   entry's status (`rule-added: <file#section>`, `rule-updated: ...`). For
   rejected ones, set `no-rule: <owner's reason>` so they aren't re-proposed.
   Update the `<!-- retro: last run ... -->` marker with today's date.
8. Commit rule and log changes on the current branch with a message like
   `Update conventions from review feedback`.

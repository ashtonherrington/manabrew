---
name: reviewer
description: Review code against this repo's conventions (in chat or as inline GitHub PR comments), harvest the owner's accept/reject verdicts and own comments from a PR, log the owner's corrections, and run retros that propose new rules from that feedback. Use when asked to review a diff, branch, or PR; when the owner says they finished reviewing a PR; whenever the owner says code Claude wrote is wrong, unclear, or not how they want it — log it even if they don't ask; and when a post-push hook asks for a retro.
---

# Reviewer

Four modes. The goal is that rules accumulate from real feedback, so the owner
never has to write every rule up front.

```
Review ──► review-skeptic tries to disprove each finding (fresh context)
   │
   └─► (PR) inline [reviewer] comments for survivors ──► owner accepts / rejects /
                                                         restores dropped / adds own
                                                                     │
Learn (chat corrections) ──► feedback log ◄──────── Harvest ◄────────┘
                                  │
                                Retro ──► proposed rule + skeptic changes ──► owner approves
```

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
4. Number findings `F1`, `F2`, … most-severe first. Each has `file:line`, the
   problem, why it matters, and the rule (or `no rule` if it is judgment).
5. **Skeptic pass.** Spawn the `review-skeptic` subagent (fresh context; do not
   pass it your reasoning beyond the findings themselves). Give it the diff
   command or PR number and the numbered findings. It returns
   `CONFIRMED` / `PLAUSIBLE` / `REFUTED` per finding with evidence.
   - Keep `CONFIRMED` and `PLAUSIBLE` findings; show the skeptic's verdict on each.
   - Drop `REFUTED` findings from the main list, but list them under
     "Dropped after verification" with the skeptic's evidence, so the owner can
     overrule the skeptic.
   - Skip the skeptic only when there are zero findings.
6. Deliver them:
   - **For a PR** (default when reviewing a PR): post them as one GitHub review
     with inline comments. See "Posting to a PR" below. Then tell the owner how
     many findings were posted and how to respond.
   - **Otherwise:** report them in chat.
7. Do not fix the code yet on a PR review: the owner's verdicts come first.

### Posting to a PR

Comments post under the owner's GitHub account (the `gh` login), so every
comment starts with the `[reviewer Fn]` tag that marks it as Claude's. Reviews
must use `event: COMMENT`; GitHub forbids approving or requesting changes on
your own PR.

Write the payload to a temp JSON file and send it with `--input`, which avoids
shell-quoting problems:

```json
{
  "commit_id": "<head SHA from: gh pr view <n> --json headRefOid>",
  "event": "COMMENT",
  "body": "[reviewer] <k> findings. Reply `accept` or `reject: <reason>` to each (or react 👍 / 👎), add your own comments anywhere, then tell Claude the review is done.\n\n<details><summary>Dropped after verification (<m>)</summary>\n\n[reviewer F4] <finding> · skeptic: <evidence>. Reply `restore F4: <reason>` to overrule.\n</details>",
  "comments": [
    {
      "path": "backend/src/main/java/com/manabrew/Foo.java",
      "line": 42,
      "side": "RIGHT",
      "body": "[reviewer F1] · severity: bug · rule: backend/CONVENTIONS.md#Layers · skeptic: CONFIRMED\n\n<problem and why it matters>\n\n<suggested fix>"
    }
  ]
}
```

```
gh api repos/{owner}/{repo}/pulls/<n>/reviews --method POST --input <file>
```

`line` must be a line in the PR diff (an added or context line on the new
side). Findings about lines outside the diff go in the review `body` instead,
still tagged `[reviewer Fn]`.

## Mode 2: Harvest

When the owner says they have finished reviewing a PR.

1. Fetch everything on the PR:
   - inline comments and replies: `gh api repos/{owner}/{repo}/pulls/<n>/comments --paginate`
     (replies carry `in_reply_to_id`; reactions are in `reactions["+1"]` / `reactions["-1"]`)
   - review summaries: `gh api repos/{owner}/{repo}/pulls/<n>/reviews`
   - conversation comments: `gh api repos/{owner}/{repo}/issues/<n>/comments`
2. Classify each item:
   - **Claude finding**: a comment starting with `[reviewer Fn]`. Its verdict is
     the owner's reply (`accept…` / `reject…`) or reaction (👍 / 👎); a reply
     wins over a reaction. No response → `unanswered`.
   - **Dropped finding restored**: an owner `restore Fn` reply on the review
     body. The skeptic refuted a real problem; verdict `restored`.
   - **Owner finding**: any other comment by the owner that points at a
     problem, i.e. something Claude's review missed.
   - Ignore pure discussion, questions already answered, and bot noise.
3. Log every accepted, rejected, and owner finding in the feedback log (one
   entry each), with `source: PR #<n> review`, the `finding` and `verdict`
   fields, and the owner's exact words. Skip unanswered findings, but mention
   how many there were.
4. Fix the code for accepted findings and owner findings, commit, and push.
   Reply to each handled comment with what changed (prefix `[reviewer]`).
5. Summarize: accepted / rejected / owner findings / unanswered, then run a
   Retro (Mode 4).

## Mode 3: Learn

Whenever the owner corrects code Claude wrote in chat, even if they don't ask
you to log it:

1. Fix the code as asked.
2. Append one entry per distinct correction to the feedback log, using its
   format, with `verdict: owner-finding`. Quote the owner's words exactly.
   Record whether an existing rule already covered it (if so, the miss is
   Claude's, not a missing rule).
3. Say in one line that it was logged. Do not propose rules yet; that is the
   retro's job.

## Mode 4: Retro

Triggered after a Harvest, by the post-push hook, or when the owner asks.

1. Read log entries with `status: open`. If there are none, say so in one line
   and stop.
2. Group open entries by module and category, and look for the underlying
   preference, not the surface fix.
3. Read the three verdicts differently:
   - **owner-finding**: Claude missed it. Strongest signal for a new or
     sharpened rule.
   - **rejected**: the rule was wrong, too broad, or misapplied. Propose
     narrowing or removing the rule, or add an explicit exception. The owner's
     reason is the key input.
   - **accepted**: the rule works. No change, unless it was a `no rule`
     judgment call; repeated accepted judgment calls become a rule.
   - **Skeptic calibration**: a `rejected` finding the skeptic marked
     `CONFIRMED` means the skeptic is too lenient; a `restored` finding means it
     is too aggressive. Propose edits to `.claude/agents/review-skeptic.md`
     (e.g. a new line of attack, or a case it must not refute) the same way as
     rule changes.
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
   rejected proposals, set `no-rule: <owner's reason>` so they aren't
   re-proposed. Entries that need no rule change (e.g. accepted findings under
   an existing rule) get `no-rule: rule worked`. Update the
   `<!-- retro: last run ... -->` marker with today's date.
8. Commit rule and log changes on the current branch with a message like
   `Update conventions from review feedback`.

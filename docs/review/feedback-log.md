# Review feedback log

Append-only record of review signal: Claude's findings with the owner's
verdicts, findings the owner made that Claude missed, and corrections given in
chat. The reviewer skill reads unprocessed entries during a retro and proposes
rule changes; approved rules go into the matching conventions file.

Entry format (newest last):

```
### <YYYY-MM-DD> <short title>
- module: <backend | infra | cross-cutting | ...>
- source: <chat | PR #n review | self-review>
- where: <file:line or area>
- what was wrong: <the concrete problem, or what Claude claimed if rejected>
- finding: <marker ID like pr5-r1-f2, if it was a Claude finding | none>
- skeptic: <CONFIRMED | PLAUSIBLE | REFUTED | n/a>
- verdict: <accepted | rejected | restored | owner-finding | self-review>
- owner's words: "<exact quote, if any>"
- category: <naming | structure | error-handling | testing | api | persistence | readability | other>
- covered by existing rule: <yes: which | no>
- status: open
```

`status` becomes `rule-added: <file#section>`, `rule-updated: ...`, or
`no-rule: <reason>` once a retro handles the entry.

<!-- retro: last run never -->

## Entries

### 2026-10-03 Named USER in Dockerfile breaks runAsNonRoot
- module: backend
- source: self-review
- where: backend/Dockerfile:14, infra/helm/manabrew-lib/templates/_deployment.tpl
- what was wrong: Dockerfile set `USER app` while the Helm library enforces `runAsNonRoot: true`; Kubernetes rejects pods whose image user isn't numeric.
- finding: none
- skeptic: n/a
- verdict: self-review
- owner's words: ""
- category: other
- covered by existing rule: no
- status: open

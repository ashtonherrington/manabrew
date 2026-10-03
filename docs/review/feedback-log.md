# Review feedback log

Append-only record of corrections to code Claude wrote, from chat or PR review
comments. The reviewer skill reads unprocessed entries during a retro and
proposes rule changes; approved rules go into the matching conventions file.

Entry format (newest last):

```
### <YYYY-MM-DD> <short title>
- module: <backend | cross-cutting | ...>
- source: <chat | PR #n comment | self-review>
- where: <file:line or area>
- what was wrong: <the concrete problem>
- owner's words: "<quote, if the owner gave feedback>"
- category: <naming | structure | error-handling | testing | api | persistence | readability | other>
- covered by existing rule: <yes: which | no>
- status: open
```

`status` becomes `rule-added: <file#section>`, `rule-updated: ...`, or
`no-rule: <reason>` once a retro handles the entry.

<!-- retro: last run never -->

## Entries

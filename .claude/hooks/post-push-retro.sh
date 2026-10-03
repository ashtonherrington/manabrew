#!/bin/sh
# PostToolUse hook: after Claude runs `git push`, ask it to run a reviewer retro
# if the feedback log has open entries. Silent otherwise.
log="$CLAUDE_PROJECT_DIR/docs/review/feedback-log.md"
[ -f "$log" ] || exit 0
open=$(awk '/^## Entries/{e=1} e && /^- status: open/{n++} END{print n+0}' "$log")
[ "$open" -gt 0 ] || exit 0
cat <<EOF
{"hookSpecificOutput":{"hookEventName":"PostToolUse","additionalContext":"Push complete. docs/review/feedback-log.md has $open open feedback entries. Run the reviewer skill in Retro mode now: propose rule changes from them and ask the owner which to accept."}}
EOF

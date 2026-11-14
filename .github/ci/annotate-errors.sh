#!/bin/sh
# Writes build errors from a Gradle log as workflow annotations, readable through the API without the log download (DECISIONS.md 42).
log="$1"
if [ ! -f "$log" ]; then
  echo "::error title=Build failed::No Gradle log: a step before Gradle failed."
  exit 0
fi
ws="${GITHUB_WORKSPACE:-/nonexistent}/"
clean() { sed -e 's/\r$//' -e 's/%/%25/g' -e "s#file://##" -e "s#$ws##"; }
emit() { while IFS= read -r line; do printf '::error title=%s::%s\n' "$1" "$line"; done; }
found=0
errs=$(grep -E '^e: |: error: |Dependency verification failed' "$log" | clean | awk '!s[$0]++' | head -10)
if [ -n "$errs" ]; then printf '%s\n' "$errs" | emit Compiler; found=1; fi
why=$(awk '/^\* What went wrong:/{f=1;next} f{ if ($0 ~ /^\* /) exit; if ($0 != "") { printf "%s | ", $0; n++; if (n >= 4) exit } }' "$log" | clean)
if [ -n "$why" ]; then printf '%s\n' "$why" | emit Gradle; found=1; fi
if [ "$found" = 0 ]; then
  printf '%s\n' "$(tail -n 15 "$log" | clean | tr '\n' '|')" | emit Log-tail
fi
exit 0

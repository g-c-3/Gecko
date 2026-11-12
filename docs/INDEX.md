# Index

Pointers only.

## File map

| File | Holds | Range | Older parts |
|---|---|---|---|
| ROADMAP.md | Phase checklist and detail | Phases 0-11 | none |
| DECISIONS.md | Numbered decisions | 1-14 | none |
| SESSIONS.md | Session log | 1-4 | none |
| TRACK.md | Session/decision/phase to date | Sessions 1-4 | none |
| ARCHITECTURE.md | Layers, network, security checks, CI | n/a | none |

## Standing decisions

| No. | Rule | File |
|---|---|---|
| 1 | No secrets in the public repo | DECISIONS.md |
| 2 | Privacy by default; two allowed connections | DECISIONS.md |
| 3 | GeckoView, exact pins, one adapter file | DECISIONS.md |
| 4 | Version scheme and versionCode formula | DECISIONS.md |
| 6 | One signing key, secrets only | DECISIONS.md |
| 7 | Dependency verification never disabled | DECISIONS.md |
| 13 | Signing material never in artifacts, logs or inputs | DECISIONS.md |

## Where to look

| Task | Go to |
|---|---|
| Build failure | ARCHITECTURE.md, Build and CI |
| New dependency or library | DECISIONS.md 3, 11 |
| New network connection | DECISIONS.md 2; ARCHITECTURE.md, Network |
| Permissions, certificates, manifest, content blocking | ARCHITECTURE.md, Security checks |
| Version or release | DECISIONS.md 4, 5 |
| Signing | DECISIONS.md 6, 13 |
| Toolchain versions | DECISIONS.md 12 |
| Workflow actions, pinning | DECISIONS.md 14 |
| What to build next | ROADMAP.md |
| When something happened | TRACK.md |

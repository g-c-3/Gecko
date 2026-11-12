# Index

Pointers only.

## File map

| File | Holds | Range | Older parts |
|---|---|---|---|
| ROADMAP.md | Phase checklist and detail | Phases 0-13 | none |
| DECISIONS.md | Numbered decisions | 1-25 | none |
| SESSIONS.md | Session log | 1-7 | none |
| TRACK.md | Session/decision/phase to date | Sessions 1-7 | none |
| ARCHITECTURE.md | Layers, network, security checks, CI | n/a | none |

## Standing decisions

| No. | Rule | File |
|---|---|---|
| 1 | No secrets in the public repo | DECISIONS.md |
| 2 | Privacy by default; allowed connections listed in 2 and 23 | DECISIONS.md |
| 3 | GeckoView, exact pins, one adapter file | DECISIONS.md |
| 4 | Version scheme and versionCode formula | DECISIONS.md |
| 6 | One signing key, secrets only | DECISIONS.md |
| 7 | Dependency verification never disabled | DECISIONS.md |
| 13 | Signing material never in artifacts, logs or inputs | DECISIONS.md |
| 15 | Release key origin and its exception; certificate fingerprint | DECISIONS.md |
| 16 | Release is automatic on a new version; tag made by the release | DECISIONS.md |
| 20 | Product direction; basic settings first, Advanced curated | DECISIONS.md |
| 21 | Spaces: six built-in, up to ten, authentication | DECISIONS.md |
| 22 | Space lock is not encryption; logins encrypted per Space | DECISIONS.md |
| 23 | Second approved connection: filter-list updates | DECISIONS.md |
| 25 | Two-step approval; build order; exclusions | DECISIONS.md |

## Where to look

| Task | Go to |
|---|---|
| Build failure | ARCHITECTURE.md, Build and CI |
| New dependency or library | DECISIONS.md 3, 11 |
| New network connection | DECISIONS.md 2, 23; ARCHITECTURE.md, Network |
| Permissions, certificates, manifest, content blocking | ARCHITECTURE.md, Security checks |
| Version or release | DECISIONS.md 4, 5, 16 |
| Signing | DECISIONS.md 6, 13, 15 |
| License | DECISIONS.md 11, 17 |
| Application id, SDK levels | DECISIONS.md 18 |
| Icon, logo | DECISIONS.md 19 |
| Toolchain versions | DECISIONS.md 12 |
| Engine version target | DECISIONS.md 24; ROADMAP.md 2 |
| Spaces, lock, saved logins | DECISIONS.md 21, 22 |
| Feature plan, build order | ROADMAP.md; DECISIONS.md 25 |
| Workflow actions, pinning | DECISIONS.md 14 |
| What to build next | ROADMAP.md |
| When something happened | TRACK.md |

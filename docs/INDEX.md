# Index

Pointers only.

## File map

| File | Holds | Range | Older parts |
|---|---|---|---|
| ROADMAP.md | Phase checklist, detail, quality targets | Phases 0-13 | none |
| DECISIONS.md | Numbered decisions | 1-35 | none |
| SESSIONS.md | Session log | 1-17 | none |
| TRACK.md | Session/decision/phase to date | Sessions 1-17 | none |
| ARCHITECTURE.md | Layers, network, security checks, CI | n/a | none |

## Standing decisions

| No. | Rule | File |
|---|---|---|
| 1 | No secrets in the public repo | DECISIONS.md |
| 2 | Privacy by default; allowed connections in 2 and 23; amended by 29 | DECISIONS.md |
| 4 | Version scheme; versionCode formula (D digit in 27) | DECISIONS.md |
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
| 26 | System WebView; one adapter file; stable APIs only; exact pins | DECISIONS.md |
| 27 | One APK, Kotlin only, no native code | DECISIONS.md |
| 28 | Android 10 minimum; WebView launch gate | DECISIONS.md |
| 29 | Privacy under WebView; opt-outs; private tabs wording | DECISIONS.md |
| 30 | Plain custom-drawn views; size and speed measured | DECISIONS.md |
| 31 | Features under WebView: native, built, reduced | DECISIONS.md |
| 33 | Repository is `g-c-3/weber` | DECISIONS.md |
| 34 | Gate screen has a Play Store button (an intent, not a connection) | DECISIONS.md |
| 35 | Quality targets (start-up, scroll, size) | DECISIONS.md |
| 32 | `androidx.webkit` pin, license, gate by run-time check, Default profile in Phase 2 | DECISIONS.md |

## Where to look

| Task | Go to |
|---|---|
| Build failure | ARCHITECTURE.md, Build and CI |
| New dependency or library | DECISIONS.md 11, 26, 30 |
| New network connection | DECISIONS.md 2, 23, 29; ARCHITECTURE.md, Network |
| Permissions, certificates, manifest, content blocking | ARCHITECTURE.md, Security checks |
| Version or release | DECISIONS.md 4, 16, 27 |
| Signing | DECISIONS.md 6, 13, 15 |
| License | DECISIONS.md 11, 17 |
| Application id, SDK levels | DECISIONS.md 18, 28 |
| Icon, logo | DECISIONS.md 19 |
| Toolchain versions | DECISIONS.md 12; ARCHITECTURE.md, Toolchain |
| Engine, WebView gate | DECISIONS.md 26, 28, 32; ROADMAP.md 2 |
| Size, speed, visual targets | DECISIONS.md 30; ROADMAP.md, Quality targets |
| Spaces, lock, saved logins | DECISIONS.md 21, 22 |
| Feature plan, build order | ROADMAP.md; DECISIONS.md 25, 31 |
| Workflow actions, pinning | DECISIONS.md 14 |
| What to build next | ROADMAP.md |
| When something happened | TRACK.md |

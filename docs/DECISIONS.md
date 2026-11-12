# Decisions

Numbered, never dated. Dates live in TRACK.md.

## 1. Public repo, source and docs only
Rule: no secret of any kind in any file, comment, workflow or commit message. Secrets live only in repository secrets.

## 2. Privacy by default
Rule: no telemetry, analytics, crash reporting, accounts, cloud sync or automated inference. Mozilla components with telemetry code (Glean and others) are disabled or not included; each session that adds one checks it opens no network connection of its own.
Allowed network traffic: pages the user visits, and the tap-only update check (sideload flavor).
Any new connection is flagged first and needs approval, with its data stated.
Browsing data (history, bookmarks, cookies, passwords, downloads, tab state) stays on-device. Private tabs leave nothing on disk.

## 3. Engine
Rule: GeckoView, release channel only. Exact versions pinned in `gradle/libs.versions.toml`; no dynamic versions. Version verified against Mozilla's documentation and Maven listing before any dependency line is written.
All engine calls go through one adapter file. Fewest possible Mozilla Android Components.
Each session checks for a newer stable GeckoView and reports it. A bump is its own task with its own CI check.

## 4. Versioning
Rule: `X.Y.Z`, X unbounded, Y 0 to 9, Z 0 to 99. Start `0.0.1`. Set only in `version.properties`.
`versionCode = (X*10000 + Y*100 + Z)*10 + D`; D = 1 universal, 2 arm64, 0 Play bundle.
Compared number by number, never as text. Every shipped change bumps Z by one. Tag `vX.Y.Z` triggers the release.

## 5. Releases
Rule: two signed APKs per release (arm64, universal). Release notes carry engine version, advisory link for engine updates, and each APK's SHA-256.
Releases are never deleted. A faulty release is marked pre-release; the previous good release stays downloadable.

## 6. Signing
Rule: one key for every release. Keystore and passwords only in repository secrets. The owner keeps a separate backup outside GitHub; losing it means no in-place updates.
Signing is its own phase (1).

## 7. Dependency verification
Rule: `gradle/verification-metadata.xml` is on. A checksum mismatch fails the build. Never disabled to get a build through.

## 8. Flavors
Rule: `sideload` (GitHub releases; update button; install permission) and `play` (neither). Updater code is never compiled into `play`.
Play shows one listing named Ardour. Play-signed and sideloaded builds carry different signatures and cannot update over each other; recorded again when Phase 10 starts.

## 9. Phase 0 scope
Rule: Phase 0 delivers docs, `version.properties` and `.gitignore` only.
Cause: Gradle files need plugin and wrapper versions, which must be verified first. They arrive in Phase 1.

## 10. Doc size
Rule: any of ROADMAP.md, TRACK.md, SESSIONS.md, DECISIONS.md passing about 60 KB is reported at "Go" with a proposed archive. Archive only on approval. Numbering never changes.

## 11. Licensing
Rule: Gecko is MPL 2.0. Each added library's license is recorded here when adopted. Nothing adopted yet.

## 12. Build toolchain (verified in session 2)
Rule: Gradle 9.9.1 (wrapper, distribution checksum pinned), Android Gradle Plugin 9.5.1, JDK 17. Kotlin comes from AGP's built-in Kotlin; no separate Kotlin plugin.
Sources: Gradle version list (services.gradle.org/versions/9), AGP 9.5 release notes (AGP 9.5 needs Gradle 9.6.0 or newer, JDK 17), runner image readme (JDK 17 default, Android platform 36 and 37.0 installed).
Workflow actions pinned by commit SHA when `build-android.yml` is written. Latest tags seen: checkout v7.0.1, setup-java v6.2.0, gradle/actions v6.4.1, upload-artifact v7.0.2, softprops/action-gh-release v3.0.4.
SDK levels are provisional until Phase 2 verifies the engine's requirement.

## 13. Signing material never leaves secrets
Rule: no keystore, password or key in a workflow artifact, log, input or summary.
Cause: artifacts of a public repo can be downloaded by any signed-in GitHub user; workflow inputs and logs are public.

## 14. Workflow hygiene
Rule: every third-party action pinned by full commit SHA with its tag in a comment. Wrapper validated in CI. No caching action (its license and data handling are unverified); builds use the wrapper directly.
Workflows request `contents: read` only.


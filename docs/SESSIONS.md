# Sessions

Numbered, never dated. Dates live in TRACK.md.

## 8
- Built: Phase 2 on GeckoView: engine pinned `159.0.20261102144711`, adapter, one page loads on device (DP1 passed, releases 0.0.4 and 0.0.5). Measured: arm64 188 MB uncompressed, 91.2 MB with compressed libraries; universal 507 MB, 237 MB. Then the engine changed to system WebView (decision 26); nothing of it built yet.
- Fixed: missing checksums (bootstrap rerun); compileSdk 36 to 37.2 and Kotlin 2.2 to 2.4.20 override, both GeckoView requirements; release notes engine line read "none".
- Decisions: 26 to 31. Only Phase 2 (WebView) approved.
- Engine: none pinned for WebView. `androidx.webkit` version not yet chosen. GeckoView `159.0.20261102144711` is in 0.0.4 and 0.0.5 only.
- Next start: Phase 2 per ROADMAP 2: verify and pin `androidx.webkit`; strip GeckoView; minSdk 29; manifest opt-outs; launch gate; adapter; one APK in CI; regenerate checksums; measure size and start-up. Open: DP0 on 0.0.2; DP9 (icon); DP13; approval of the second connection wording is in the instructions update; optional repo rename (the name `Gecko` stays in the updater URL until decided).

## 7
- Built: no code. Product discussion concluded: Spaces (six, up to ten), second approved connection (filter-list updates), feature plan with phases 11 to 13, Basic and Advanced settings. Docs updated.
- Fixed: engine candidate corrected. Stable is 159.0.x; 160.0 is beta, so session 5 and 6 entries naming `160.0.20261104181552` are superseded (decision 24).
- Decisions: 20 to 25. Only Phase 2 approved; rest planned.
- Engine: none pinned. Candidates `geckoview:159.0.20261102144711`, fallback `159.0.20261022150525`; verified through search only (Maven mirror listing, third-party release), Mozilla's Maven not read.
- Next start: Phase 2 (ROADMAP 2). Read `app/build.gradle.kts`, `libs.versions.toml`, manifest first. Open: release 0.0.3 status (GitHub API was rate-limited); DP0 on 0.0.2; DP9; approval of the project-instructions update (second connection, plus the filter-list schedule; owner pastes the current text).

## 6
- Built: adaptive launcher icon from the owner's logo (decision 19); manifest icon reference; version 0.0.3. Docs brought up to date.
- Fixed: nothing.
- Decisions: 19.
- Engine: none pinned. Candidate `geckoview:160.0.20261104181552` (ROADMAP 2).
- Next start: owner-led product discussion on what makes the browser distinct (Phase 11 inputs); no build until it concludes. Then Phase 2. Open: release 0.0.3 result; DP9 (icon) and DP0 on 0.0.2; approval of the project-instructions update.

## 5
- Built: Phase 1 finished. Release key and secrets; `build-android.yml` with `build`, `plan`, `release` jobs; MPL 2.0 `LICENSE`; application id `io.github.gc3.ardour`; version 0.0.2.
- Fixed: first release run tagged the release `main` (tag taken from the branch name). Tag now `v<version>`. Stray tag and release removed.
- Decisions: 15 to 18.
- Engine: none pinned. Stable is 160.0; candidate `geckoview:160.0.20261104181552` (see ROADMAP 2).
- Next start: confirm DP0 on 0.0.2; Phase 2 (pin, regenerate checksums, adapter, privacy check).

## 4
- Verified: `build-android` run 3 green (debug APKs, arm64 and universal assembled). Runs 1 and 2 red: workflow committed before the app files; commit-order cause, no code fault.
- Engine check: current stable GeckoView not verified (search returned outdated pages). Phase 2 verifies it first, from Mozilla's Maven listing.
- Decisions: none.
- Engine: none pinned yet.
- Next start: owner runs `verification-bootstrap`, commits the checksum file; owner answers app id, keystore method, license. Then the release workflow.

## 3
- Built: `app` module (no engine, plain activity), version wiring with ABI splits, `build-android.yml` (debug build), `verification-bootstrap.yml`.
- Fixed: nothing. API forms checked against AGP documentation: `compileSdk { version = release(..) }`, `output.versionCode.set`.
- Decisions: 14.
- Engine: none pinned yet.
- Next start: owner runs both workflows; paste any failing step. Then keystore method, license, app id approval.

## 2
- Built: toolchain pinned (decision 12): Gradle wrapper 9.9.1, `gradle/libs.versions.toml`, root Gradle files.
- Fixed: nothing. Found: docs were uploaded to the repo root; they belong in `docs/`.
- Decisions: 12, 13.
- Engine: none pinned yet.
- Next start: Phase 1 remainder: app module, `build-android.yml`, verification metadata, keystore method (see ROADMAP).

## 1
- Built: Phase 0. Docs (INDEX, ROADMAP, DECISIONS, SESSIONS, TRACK, ARCHITECTURE), `version.properties` (0.0.1), `.gitignore`.
- Fixed: nothing.
- Decisions: 1 to 11.
- Engine: none pinned yet.
- Next start: Phase 1. Verify current Android Gradle Plugin, Kotlin, Gradle and JDK versions first.

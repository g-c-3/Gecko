# Sessions

Numbered, never dated. Dates live in TRACK.md.

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

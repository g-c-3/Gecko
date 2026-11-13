# Sessions

Numbered, never dated. Dates live in TRACK.md.

## 16
- Built: gate screen now says how to update WebView and has an Open Play Store button (decision 34); version 0.0.8. Not run through CI or on a device; the screen cannot be shown on a current phone. Phase 2 marked complete; gate message and Android 10 start moved to the Phase 5 launch test.
- Verified: on Android 10 and higher WebView is the separate app `com.google.android.webview` and Chrome cannot provide it (Chromium WebView documentation). Not verified: how the Play Store handles the link on every phone.
- Decisions: 34.
- Engine: `androidx.webkit` 1.18.0. APK size: 0.0.8 not read.
- Next start: owner approves the revised targets and Phase 3.

## 15
- Measured: 0.0.7 on moto g34 5G (Android 15) from one screen recording: three cold starts, 0.51 to 0.60 s from first launch frame to page; scroll compared with a reference browser on the same page: both median 16.8 ms, no gap over 34 ms during fast motion in either. Method and limits in ROADMAP Quality targets.
- Decisions: none. Targets proposed, not set.
- Engine: `androidx.webkit` 1.18.0. APK size: 2.88 MB at 0.0.6; 0.0.7 not read.
- Not measured: WebView version, memory, a second page, the gate message.
- Next start: owner approves the targets and Phase 3; then Phase 3.

## 14
- Built: DP14 fix, not run through CI or on a device: `themes.xml` with light status and navigation bar icons, manifest uses it, version 0.0.7. No new dependency.
- Verified: `windowLightStatusBar` needs API 23, `windowLightNavigationBar` API 27 (platform reference mirrors); both below minSdk 29. Not verified: behaviour under edge-to-edge on device.
- Decisions: none.
- Engine: `androidx.webkit` 1.18.0 (unchanged; newest stable not rechecked this turn). APK size: 2.88 MB at 0.0.6.
- Next start: owner confirms DP14; then start-up and scroll measurement; Phase 3 needs approval. Icons stay dark on dark pages until Phase 9 theming.

## 13
- Built: nothing in code. Owner checked 0.0.6: build #47 green after bootstrap, release 0.0.6 published, APK 2.88 MB, page and a followed link load on device (DP13 in part). Repo renamed to `g-c-3/weber`; old name redirects.
- Found: status-bar icons are light on a light page (DP14). ARCHITECTURE.md still named the old repo in the update URL; fixed.
- Decisions: 33.
- Engine: `androidx.webkit` 1.18.0. APK size 2.88 MB.
- Next start: DP14 fix (verify API form), then start-up and scroll measurement; Phase 3 needs approval. Instructions text still names the old repo in its read URLs.

## 12
- Built: Phase 2 build files, not run through CI or on a device: `androidx.webkit` 1.18.0 pinned, GeckoView and its workarounds removed, minSdk 29, manifest opt-outs, launch gate, one adapter (Default profile, settings, renderer-gone handling), one APK `Ardour-X.Y.Z.apk` (versionCode D = 1), release notes line, version 0.0.6.
- Verified: 1.18.0 newest stable (release notes, 21 October 2026); Apache 2.0; mixed-content and file-URL defaults. Docs, tags v0.0.1 to v0.0.5 and INDEX ranges agree; doc sizes ROADMAP 12.3 KB, TRACK 1.7 KB, SESSIONS 7.4 KB, DECISIONS 16.3 KB. Not verified: size cost, compileSdk, profile-name rules.
- Decisions: 32.
- Engine: `androidx.webkit` 1.18.0 (in the delivered files); GeckoView stays in releases 0.0.4 and 0.0.5. APK size: no new build; 0.0.5 arm64 91.2 MB, universal 237 MB.
- Next start: owner commits the files, runs `verification-bootstrap`, commits the metadata; read the first build's size and any compileSdk error. Then DP13, and on-device start-up and scroll measurement. Open: DP0 on 0.0.2, DP9.

## 11
- Built: no code. Docs read; file tree and INDEX ranges agree; tags v0.0.1 to v0.0.5 match `version.properties` (0.0.5). Doc sizes: ROADMAP 11.1 KB, TRACK 1.5 KB, SESSIONS 6.5 KB, DECISIONS 16.3 KB; all under 60 KB.
- Verified: `androidx.webkit` 1.18.0 still newest stable (release notes page, last updated 4 November); 1.19.0-alpha01 not used. `getCurrentWebViewPackage(context)` form, manifest meta-data names, `setAllowFileAccess` default recorded in ROADMAP 2. Not verified: mixed-content and file-URL settings, license, size cost, compileSdk.
- Decisions: none.
- Engine: none pinned in the repo (GeckoView `159.0.20261102144711` still in 0.0.5); target `androidx.webkit` 1.18.0. APK size: no new build; 0.0.5 arm64 91.2 MB, universal 237 MB.
- Next start: Phase 2 per ROADMAP 2: close the remaining verification items (search per method name, not whole pages), then deliver the build files in one commit order. Open: DP0 on 0.0.2, DP9, DP13.

## 10
- Built: no code. Docs read; file tree and INDEX ranges agree; tags v0.0.1 to v0.0.5 match `version.properties` (0.0.5). Doc sizes: ROADMAP 10.5 KB, TRACK 1.4 KB, SESSIONS 5.6 KB, DECISIONS 16.3 KB; all under 60 KB.
- Verified: `androidx.webkit` 1.18.0 still newest stable (release notes page); 1.19.0-alpha01 not used. Renderer-gone handling recorded in ROADMAP 2. Not verified: `WebSettings` calls, `getCurrentWebViewPackage` signature, license, size cost, compileSdk.
- Decisions: none.
- Engine: none pinned in the repo (GeckoView `159.0.20261102144711` still in 0.0.5); target `androidx.webkit` 1.18.0. APK size: no new build; 0.0.5 arm64 91.2 MB, universal 237 MB.
- Next start: Phase 2 per ROADMAP 2: close the four open verification items, then deliver the build files in one commit order. Open: DP0 on 0.0.2, DP9, DP13.

## 9
- Built: no code. Docs read; file tree and INDEX ranges agree; tags v0.0.1 to v0.0.5 match `version.properties` (0.0.5). Doc sizes all under 60 KB.
- Verified: newest stable `androidx.webkit` is 1.18.0 (developer.android.com release notes); 1.19.0-alpha01 not used. Profile API forms, `setProfile` ordering rule and `MULTI_PROFILE` flag recorded in ROADMAP 2; the gate uses the run-time flag, no WebView version claimed. Not verified: `WebSettings` calls, renderer-gone callback, license, size cost.
- Decisions: none.
- Engine: none pinned in the repo (GeckoView `159.0.20261102144711` still in 0.0.5); target `androidx.webkit` 1.18.0. APK size: no new build; 0.0.5 arm64 91.2 MB, universal 237 MB.
- Next start: Phase 2 per ROADMAP 2: close the remaining unverified items, then deliver the build files in one commit order. Open: DP0 on 0.0.2, DP9, DP13.

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

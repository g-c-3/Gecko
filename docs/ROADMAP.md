# Roadmap

## Checklist

- [x] 0 Repo scaffold and docs
- [x] 1 CI, signing and versioning
- [ ] 2 Hello WebView
- [ ] 3 Settings screen and in-app update
- [ ] 4 Basic browser
- [ ] 5 Dependency update pipeline and launch test
- [ ] 6 Tabs and private tabs
- [ ] 7 History, bookmarks, downloads
- [ ] 8 Content blocking and privacy controls (built by Ardour)
- [ ] 9 Theming and polish
- [ ] 10 Play Store preparation
- [ ] 11 Special features
- [ ] 12 Spaces
- [ ] 13 Customisation

## Detail

Build order: 2, 3, 4, 12, 5, 6, 7, 8, 11, 13, 9, 10 (decision 25). First public release after 8.
Engine: system WebView (decision 26). Quality bar: decision 30; targets below.
Approval: only Phase 2 is approved. Items marked (planned) are approved per phase when it starts.
Settings (decision 20): basic options by default; an Advanced section holds the full range. Fast and smooth is checked on device in every phase.

### 0 Repo scaffold and docs (done)
- `docs/` (six files), `version.properties`, `.gitignore`.
- Gradle files deferred to Phase 1 (decision 9).

### 1 CI, signing and versioning (done)
- Toolchain pinned (decision 12); `app` module; ABI splits (arm64, universal; replaced by one APK, decision 27); version wiring from `version.properties`.
- `build-android.yml`: jobs `build`, `plan`, `release`. Release is automatic on a new version (decision 16).
- `verification-bootstrap.yml`: manual; checksum file committed.
- Signing key and secrets (decision 15). License MPL 2.0 (decision 17). Application id (decision 18).
- DP0: release 0.0.1 installed and opened on device. Release 0.0.2 (new application id): device result pending.
- Launcher icon shipped early as 0.0.3 (decision 19); see Phase 9.

### 2 Hello WebView (approved)
- State: releases 0.0.4 and 0.0.5 run GeckoView `159.0.20261102144711` (decision 24, superseded). DP1 passed on both: a page loads, below the status bar. Sizes: arm64 91.2 MB, universal 237 MB.
- Engine is now system WebView (decision 26). Remove from the repo: GeckoView dependency and `libs.versions.toml` entry, Mozilla Maven in `settings.gradle.kts`, Kotlin 2.4.20 classpath override in the root build file, compileSdk 37.2 and `useLegacyPackaging` in `app/build.gradle.kts`. Let the build decide whether `androidx.webkit` needs any of them.
- Verify the latest stable `androidx.webkit` on developer.android.com; pin it; record its license (decision 11).
- minSdk 29 (decision 28). Manifest: INTERNET (present), Safe Browsing off and metrics opt-out meta-data (decision 29), `allowBackup=false` (confirm).
- Launch gate (decision 28): WebView package version through `WebViewCompat.getCurrentWebViewPackage`; `WebViewFeature.isFeatureSupported` for profiles; full-screen message when unmet. Find the minimum WebView version for the features needed; record it in DECISIONS.md.
- Rewrite `EngineAdapter.kt` for WebView; one screen; one page loads. WebView settings per ARCHITECTURE.md, Security checks. Handle the renderer-gone callback as documented.
- Space profile: bind one profile before any other call on the WebView; verify the API form and feature flag in the documentation.
- CI: one APK `Ardour-X.Y.Z.apk`; versionCode D = 1 (decision 27); release notes line for the WebView requirement; regenerate `verification-metadata.xml` with `verification-bootstrap`, commit, then build.
- Measure on device and report in the session entry: APK size, cold start to first page, scroll smoothness. Set Quality targets from it.
- DP13: first WebView build loads a page; gate message checked if possible; size and start-up read.

### 3 Settings screen and in-app update
- Settings screen: basic list plus Advanced section (decision 20); "Check for updates" (sideload only). About shows the app version and the phone's WebView version.
- Version compare number by number; the one APK (decision 27); SHA-256 check; system installer.
- (planned) Local crash log, shared only on tap.
- DP2: settings opens. DP3: update check and install.

### 4 Basic browser
- Address bar, back, forward, reload, progress. Replace the placeholder start page (`https://example.com`).
- (planned) Pull-to-refresh; find in page; desktop-site switch; text size and zoom; share, print, save as PDF.
- (planned) Reader mode with a warm "twilight" theme.
- (planned) Default browser and links from other apps; Space choice added after Phase 12.
- DP4: navigation works.

### 5 Dependency update pipeline and launch test
- `dependency-update.yml`: scheduled check for a newer stable `androidx.webkit` (stable only); one pull request that changes the pin, bumps the patch version, regenerates verification metadata and builds. Replaces the GeckoView engine pipeline.
- Launch test in CI (emulator installs the APK, loads a local page).
- Credential method for starting the build verified against current GitHub documentation; recorded in DECISIONS.md.

### 6 Tabs and private tabs
- Tab list, switching, closing; private tabs use a temporary profile removed on close and at next launch (decision 29).
- (planned) Recently closed tabs; tab groups and tab search; tab sleep.
- DP5: tabs and private tab behavior.

### 7 History, bookmarks, downloads
- On-device only; export on explicit action.
- (planned) Resumable download manager.
- (planned) Address-bar suggestions from local history only.
- (planned) Saved logins, encrypted per Space; cache clearing keeps them; separate clear-logins action (decisions 21, 22). Passkey support (verify WebView support first, decision 31).
- (planned) Import bookmarks and logins from a file; reminder to delete the export.
- (planned) Offline reading pages per Space; auto-clear history older than N days; offline password health check (no breach lookup).
- DP6: each works on device.

### 8 Content blocking and privacy controls (built by Ardour)
- Own content blocking on WebView (decision 31): request blocking, page-element hiding by script injected before page scripts. No request rewriting. Per-site controls; no connection beyond decision 23.
- (planned) Filter-list ad blocking: lists built in, tap and scheduled updates (decision 23). Verify the lists' licenses first.
- (planned) Do Not Track and Global Privacy Control (main-frame headers and a script flag); link-tracker stripping; HTTPS-only upgrade at navigation.
- (planned) Local data saver: image blocking, no autoplay, no web fonts, no prefetch.
- (planned) Site permissions manager and pop-up blocker; per-site cookie auto-delete; local privacy report; site info panel.
- DP7: blocking and controls on device.

### 9 Theming and polish
- Launcher icon done early (decision 19). DP9: icon looks right on the launcher (0.0.3).
- Remaining: light and dark themes, layout; custom-drawn UI and spring animation (decision 30). Status bar clock and icons are light on a light bar in 0.0.4 and 0.0.5; fix here.
- Monochrome icon layer; 512 px store icon (Phase 10).
- (planned) Smooth transitions and graphics; follow the system reduce-animations setting.
- (planned) Picture-in-picture; add to home screen; background audio with media controls; tablet and foldable layouts.
- DP8: appearance on device.

### 10 Play Store preparation
- `play` flavor, App Bundle (one bundle, D = 0), listing, policy forms, name and trademark check. Listing claims state measured results only (decision 30); the engine is Google's WebView (decision 29).
- Record the signature difference again in DECISIONS.md.

### 11 Special features
- (planned) Wipe on leaving a Space; panic button and home-screen shortcut (no prompt).
- (planned) Link routing to a Space; open a link in another Space.
- (planned) Hidden Space.
- (planned) Per-Space privacy level, including best-effort fingerprint reduction at script level (off by default; decision 31).
- (planned) Screenshot and app-switcher preview block in private and hidden Spaces.
- (planned) Encrypted Space backup to a file, on tap.
- (planned) Space shortcuts on the app icon (lock still applies); site time limits per Space.
- DP11: each works on device.

### 12 Spaces
- (planned) Six built-in Spaces, up to ten; add and delete behind device authentication (decision 21).
- (planned) Biometric or phone-password switch; open with default or last-used Space.
- (planned) Private tab per Space; clear data per Space; name, colour and icon.
- (planned) Per-Space settings: search engine, home page, desktop-site default, text size.
- (planned) Auto-lock timeout (hidden Space locks immediately); first-run setup.
- DP10: Spaces, lock and clearing on device.

### 13 Customisation
- (planned) In order: looks, layout (including bottom toolbar), behaviour.
- (planned) Keyword shortcuts and custom search engines; per-Space theme or wallpaper; swipe gestures.
- DP12: customisation on device.

### Quality targets
- Size: about 5 MB for the APK (decision 30); first WebView build confirms or corrects it. Each dependency states its size cost.
- Start-up and frame rate: targets set after the Phase 2 measurement; re-measured every phase on device.
- Visual quality: custom-drawn views and spring motion; judged on device by the owner each phase.

### Later (each needs its own approval)
- Forced dark mode on every page; page translation.
- Anything needing a new connection: web push, add-on store, cookie-banner lists, encrypted DNS, search suggestions, safe-browsing lookups.
- Excluded: data-saver proxy, voice search, cloud sync and backup, crypto wallet.

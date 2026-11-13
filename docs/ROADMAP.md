# Roadmap

## Checklist

- [x] 0 Repo scaffold and docs
- [x] 1 CI, signing and versioning
- [x] 2 Hello WebView
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
- State: release 0.0.6 built green and runs on device (WebView, one APK, gate, adapter). APK 2.88 MB (release page). Releases 0.0.4 and 0.0.5 run GeckoView `159.0.20261102144711` (decision 24, superseded). DP1 passed on both: a page loads, below the status bar. Sizes: arm64 91.2 MB, universal 237 MB.
- Engine is now system WebView (decision 26). Remove from the repo: GeckoView dependency and `libs.versions.toml` entry, Mozilla Maven in `settings.gradle.kts`, Kotlin 2.4.20 classpath override in the root build file, compileSdk 37.2 and `useLegacyPackaging` in `app/build.gradle.kts`. Let the build decide whether `androidx.webkit` needs any of them.
- `androidx.webkit` verified on developer.android.com (session 9): stable 1.18.0; 1.19.0-alpha01 is alpha and not used. Pin 1.18.0 in `libs.versions.toml`; record its license (decision 11), its size cost and the compileSdk it needs from the build.
- API forms verified (session 9): `ProfileStore.getInstance()` with `getOrCreateProfile(name)`, `deleteProfile(name)`, `getAllProfileNames()`; `WebViewCompat.setProfile(webView, name)` and `getProfile(webView)`. All behind `WebViewFeature.MULTI_PROFILE`; without it they throw `UnsupportedOperationException`. `Profile.DEFAULT_PROFILE_NAME` is "Default". `WebViewBuilder` is experimental: not used.
- Rule for the adapter: `setProfile` runs before any other WebView call except attaching to the view hierarchy; `evaluateJavascript` before it throws `IllegalStateException`. Launch gate checks `isFeatureSupported(MULTI_PROFILE)` at run time; no WebView version number is claimed (the reference gives none).
- Verified (session 10, developer.android.com): 1.18.0 is still the newest stable; 1.19.0-alpha is not used. Library minSdk is 24 (no conflict with 29). 1.17.1 removed Kotlin from the library.
- Renderer-gone callback (session 10, "Manage WebView objects" guide, snippets partly read): `WebViewClient.onRenderProcessGone(view, detail)`; `detail.didCrash()` separates a crash from a system kill. To keep the app running: remove the view from its parent, destroy it, drop the reference, return true. Returning false ends the app. Lint (1.17.0) flags a missing override.
- Verified (session 11, "Manage WebView objects" guide): `WebViewCompat.getCurrentWebViewPackage(context)` returns a `PackageInfo`; the guide reads `versionName`. The platform method returns null when no WebView package exists; the gate treats null as unmet. `android.webkit.WebView.EnableSafeBrowsing` false and `android.webkit.WebView.MetricsOptOut` true confirmed as the manifest meta-data names.
- Verified (session 11, Android security guide "Cross-app scripting"): `setAllowFileAccess` defaults to true on API 29 and lower and false on 30 and higher; the guide sets it and `setAllowContentAccess` to false explicitly. Ardour sets both to false explicitly.
- Library history (session 11, release notes): minSdk 24 since 1.16.0; 1.16.0-alpha03 notes say the library keeps compileSdk 33; the required compileSdk for 1.18.0 is not stated. The build decides.
- Verified (session 12): mixed-content default and file-URL defaults (decision 32); `androidx.webkit` license Apache 2.0 (decision 32); newest stable still 1.18.0. Not verified: size cost and required compileSdk (first build), profile-name rules, `setJavaScriptEnabled` default read only from a third-party page.
- Method note: whole-page fetches of the `WebSettings` reference and of the release notes return mostly navigation text or very long text. Search for the named method and read the snippet; fetch a page only when the method name is in the search result.
- minSdk 29 (decision 28). Manifest: INTERNET (present), Safe Browsing off and metrics opt-out meta-data (decision 29), `allowBackup=false` (confirm).
- Launch gate (decision 28): WebView package version through `WebViewCompat.getCurrentWebViewPackage`; `WebViewFeature.isFeatureSupported` for profiles; full-screen message when unmet. Find the minimum WebView version for the features needed; record it in DECISIONS.md.
- Rewrite `EngineAdapter.kt` for WebView; one screen; one page loads. WebView settings per ARCHITECTURE.md, Security checks. Handle the renderer-gone callback as documented.
- Space profile: bind one profile before any other call on the WebView; verify the API form and feature flag in the documentation.
- CI: one APK `Ardour-X.Y.Z.apk`; versionCode D = 1 (decision 27); release notes line for the WebView requirement; regenerate `verification-metadata.xml` with `verification-bootstrap`, commit, then build.
- Measure on device and report in the session entry: APK size, cold start to first page, scroll smoothness. Set Quality targets from it.
- Order after delivery: commit the files (build turns red on checksums, expected), run `verification-bootstrap`, commit the metadata, build green, release 0.0.6.
- DP13: passed on 0.0.7 except the gate message (cannot be triggered on a current WebView). Page and link load; cold start measured (below).
- DP14: passed on 0.0.7 (owner screenshot): clock and status icons dark and readable. Navigation bar not visible in the screenshot, not confirmed.

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
- Launch test in CI (emulator installs the APK, loads a local page). Runs on an Android 10 image and on the current one. Gate check: an emulator with an old or missing WebView must show the gate message and its button (decisions 28, 34). Closes the gate-message item left open in Phase 2.
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
- Size: 2.88 MB measured at 0.0.6 (release page), under the 5 MB aim of decision 30; target set after the shrinking decision. Each dependency states its size cost.
- Start-up and frame rate: measured session 15 on moto g34 5G, Android 15, 0.0.7, screen recording (accuracy about one video frame, 17 ms). Cold start, first launch frame to first page: 0.51, 0.56, 0.60 s (mean 0.56 s); example.com, network and cache unknown. Scroll, same IANA page, same method: Ardour 0.0.7 median frame gap 16.8 ms (5 flicks, 5 of 161 gaps over 34 ms; two later recordings, probably Ardour: 16.9 and 16.8 ms); reference browser 16.8 ms (6 flicks, 5 of 255 over 34 ms). No gap over 34 ms during fast motion in any recording; every longer gap sits where motion slows or the finger changes direction. Ardour matches the reference. Proposed targets (revised, pending approval): cold start to first page 0.50 s or less, mean of 3, and never above 0.60 s in any phase without a decision entry; no white flash, Ardour's own themed window shown before the page; median scroll gap 17 ms or less with no mid-flick gap over 34 ms in 10 flicks, and never worse than the same page in a reference browser on the same phone; APK 5 MB or less through Phase 8. Re-measured every phase.
- Visual quality: custom-drawn views and spring motion; judged on device by the owner each phase.

### Later (each needs its own approval)
- Forced dark mode on every page; page translation.
- Anything needing a new connection: web push, add-on store, cookie-banner lists, encrypted DNS, search suggestions, safe-browsing lookups.
- Excluded: data-saver proxy, voice search, cloud sync and backup, crypto wallet.

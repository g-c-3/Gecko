# Roadmap

## Checklist

- [x] 0 Repo scaffold and docs
- [x] 1 CI, signing and versioning
- [ ] 2 Hello GeckoView
- [ ] 3 Settings screen and in-app update
- [ ] 4 Basic browser
- [ ] 5 Update pipeline
- [ ] 6 Tabs and private tabs
- [ ] 7 History, bookmarks, downloads
- [ ] 8 Content blocking and privacy controls
- [ ] 9 Theming and polish
- [ ] 10 Play Store preparation
- [ ] 11 Special features
- [ ] 12 Spaces
- [ ] 13 Customisation

## Detail

Build order: 2, 3, 4, 12, 5, 6, 7, 8, 11, 13, 9, 10 (decision 25). First public release after 8.
Approval: only Phase 2 is approved. Items marked (planned) are approved per phase when it starts.
Settings (decision 20): basic options by default; an Advanced section holds the full range. Fast and smooth is checked on device in every phase.

### 0 Repo scaffold and docs (done)
- `docs/` (six files), `version.properties`, `.gitignore`.
- Gradle files deferred to Phase 1 (decision 9).

### 1 CI, signing and versioning (done)
- Toolchain pinned (decision 12); `app` module; ABI splits (arm64, universal); version wiring from `version.properties`.
- `build-android.yml`: jobs `build`, `plan`, `release`. Release is automatic on a new version (decision 16).
- `verification-bootstrap.yml`: manual; checksum file committed.
- Signing key and secrets (decision 15). License MPL 2.0 (decision 17). Application id (decision 18).
- DP0: release 0.0.1 installed and opened on device. Release 0.0.2 (new application id): device result pending.
- Launcher icon shipped early as 0.0.3 (decision 19); see Phase 9.

### 2 Hello GeckoView (approved)
- Engine target 159.0.x stable (decision 24). Dropped: `160.0.20261104181552` (beta).
- Candidates, not pinned, Mozilla's Maven not read: `org.mozilla.geckoview:geckoview:159.0.20261102144711` (likely 159.0.1; seen in a third-party release that downloads it from Mozilla's Maven). Fallback `159.0.20261022150525` (159.0; Maven mirror listing). Release-channel artifact id `geckoview` to confirm (the quick start documents `geckoview-<channel>` for beta and nightly). CI resolution confirms.
- Mozilla Maven repository `https://maven.mozilla.org/maven2/` with `includeGroup("org.mozilla.geckoview")` only.
- Java 17 flags (already set). Check the engine's minSdk against decision 18.
- Dependency verification: regenerate `verification-metadata.xml` with `verification-bootstrap`, commit, then build.
- Engine adapter file; one screen; one page loads (`GeckoRuntime.create`, `GeckoSession.open`, `GeckoView.setSession`, `loadUri`; per Mozilla quick start).
- Space id carried by the adapter from the start; verify the context-id setting in the pinned javadoc (decision 21).
- Privacy check (decision 2): disable telemetry and data upload prefs (`datareporting.healthreport.uploadEnabled`, `toolkit.telemetry.enabled`) through `GeckoPreferenceController`; verify its signature in the pinned javadoc. List every other background connection the engine makes by default (tracker-list updates, push, safe browsing) and switch each off or flag it.
- Decide UI approach (Android views or Compose) in DECISIONS.md.
- DP1: page loads on device.

### 3 Settings screen and in-app update
- Settings screen: basic list plus Advanced section (decision 20); "Check for updates" (sideload only).
- Version compare number by number; device-matched APK; SHA-256 check; system installer.
- (planned) Local crash log, shared only on tap.
- DP2: settings opens. DP3: update check and install.

### 4 Basic browser
- Address bar, back, forward, reload, progress.
- (planned) Pull-to-refresh; find in page; desktop-site switch; text size and zoom; share, print, save as PDF.
- (planned) Reader mode with a warm "twilight" theme.
- (planned) Default browser and links from other apps; Space choice added after Phase 12.
- DP4: navigation works.

### 5 Update pipeline
- `engine-update.yml`: scheduled check, pull request, advisory link, build.
- Launch test in CI (emulator installs the APK, loads a local page).
- Credential method for starting the build verified against current GitHub documentation; recorded in DECISIONS.md.

### 6 Tabs and private tabs
- Tab list, switching, closing; private tabs write nothing to disk.
- (planned) Recently closed tabs; tab groups and tab search; tab sleep.
- DP5: tabs and private tab behavior.

### 7 History, bookmarks, downloads
- On-device only; export on explicit action.
- (planned) Resumable download manager.
- (planned) Address-bar suggestions from local history only.
- (planned) Saved logins, encrypted per Space; cache clearing keeps them; separate clear-logins action (decisions 21, 22). Passkey support.
- (planned) Import bookmarks and logins from a file; reminder to delete the export.
- (planned) Offline reading pages per Space; auto-clear history older than N days; offline password health check (no breach lookup).
- DP6: each works on device.

### 8 Content blocking and privacy controls
- Engine content blocking; per-site controls; no connection beyond decision 23.
- (planned) Filter-list ad blocking: lists built in, tap and scheduled updates (decision 23). Verify engine support and licenses first.
- (planned) Do Not Track and Global Privacy Control; link-tracker stripping; HTTPS-only.
- (planned) Local data saver: image blocking, no autoplay, no web fonts, no prefetch.
- (planned) Site permissions manager and pop-up blocker; per-site cookie auto-delete; local privacy report; site info panel.
- DP7: blocking and controls on device.

### 9 Theming and polish
- Launcher icon done early (decision 19). DP9: icon looks right on the launcher (0.0.3).
- Remaining: light and dark themes, layout. Placeholder screen text sits under the status bar; replaced in Phase 2.
- Monochrome icon layer; 512 px store icon (Phase 10).
- (planned) Smooth transitions and graphics; follow the system reduce-animations setting.
- (planned) Picture-in-picture; add to home screen; background audio with media controls; tablet and foldable layouts.
- DP8: appearance on device.

### 10 Play Store preparation
- `play` flavor, App Bundle, listing, policy forms, name and trademark check.
- Record the signature difference again in DECISIONS.md.

### 11 Special features
- (planned) Wipe on leaving a Space; panic button and home-screen shortcut (no prompt).
- (planned) Link routing to a Space; open a link in another Space.
- (planned) Hidden Space.
- (planned) Per-Space privacy level, including strict fingerprint protection (off by default).
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

### Later (each needs its own approval)
- Forced dark mode on every page; page translation.
- Anything needing a new connection: web push, add-on store, cookie-banner lists, encrypted DNS, search suggestions, safe-browsing lookups.
- Excluded: data-saver proxy, voice search, cloud sync and backup, crypto wallet.

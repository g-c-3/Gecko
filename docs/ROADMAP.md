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
- [ ] 11 Distinguishing features

## Detail

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

### 2 Hello GeckoView
- Starts after the product discussion (see Phase 11).
- Engine candidate, not pinned yet: `org.mozilla.geckoview:geckoview:160.0.20261104181552` (Firefox for Android 160.0 stable). Evidence: Firefox release notes; version string seen in third-party reports. Not read from Mozilla's Maven (unreachable from the sandbox). Confirm by adding the pin and letting CI resolve it.
- Mozilla Maven repository `https://maven.mozilla.org/maven2/` with `includeGroup("org.mozilla.geckoview")` only.
- Java 17 flags (already set). Check the engine's minSdk against decision 18.
- Dependency verification: regenerate `verification-metadata.xml` with `verification-bootstrap`, commit, then build.
- Engine adapter file; one screen; one page loads (`GeckoRuntime.create`, `GeckoSession.open`, `GeckoView.setSession`, `loadUri`; per Mozilla quick start).
- Privacy check (decision 2): disable telemetry and data upload prefs (`datareporting.healthreport.uploadEnabled`, `toolkit.telemetry.enabled`) through `GeckoPreferenceController`; verify its signature in the 160 javadoc. List every other background connection the engine makes by default and switch each off or flag it.
- Decide UI approach (Android views or Compose) in DECISIONS.md.
- DP1: page loads on device.

### 3 Settings screen and in-app update
- Settings screen; "Check for updates" (sideload only).
- Version compare number by number; device-matched APK; SHA-256 check; system installer.
- DP2: settings opens. DP3: update check and install.

### 4 Basic browser
- Address bar, back, forward, reload, progress.
- DP4: navigation works.

### 5 Update pipeline
- `engine-update.yml`: scheduled check, pull request, advisory link, build.
- Launch test in CI (emulator installs the APK, loads a local page).
- Credential method for starting the build verified against current GitHub documentation; recorded in DECISIONS.md.

### 6 Tabs and private tabs
- Tab list, switching, closing; private tabs write nothing to disk.
- DP5: tabs and private tab behavior.

### 7 History, bookmarks, downloads
- On-device only; export on explicit action.
- DP6: each works on device.

### 8 Content blocking and privacy controls
- Engine content blocking; per-site controls; no new connections.
- DP7: blocking and controls on device.

### 9 Theming and polish
- Launcher icon done early (decision 19). DP9: icon looks right on the launcher (0.0.3).
- Remaining: light and dark themes, layout. Placeholder screen text sits under the status bar; replaced in Phase 2.
- Monochrome icon layer; 512 px store icon (Phase 10).
- DP8: appearance on device.

### 10 Play Store preparation
- `play` flavor, App Bundle, listing, policy forms, name and trademark check.
- Record the signature difference again in DECISIONS.md.

### 11 Distinguishing features
- Decided by the owner. A product discussion is held before any further build; its outcome is recorded in DECISIONS.md and may reorder phases 3 to 9.

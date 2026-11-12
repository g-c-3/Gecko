# Roadmap

## Checklist

- [x] 0 Repo scaffold and docs
- [ ] 1 CI, signing and versioning
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

### 1 CI, signing and versioning
- Verify and pin: Android Gradle Plugin, Kotlin, Gradle, JDK.
- Gradle files, wrapper, minimal `app` module that builds (no engine).
- `versionName` and `versionCode` from `version.properties` (decision 4).
- `build-android.yml`: build, sign, release on tag; arm64 and universal APKs; SHA-256 in notes.
- `gradle/verification-metadata.xml`.
- Keystore: created by the owner, stored as repository secrets, backed up outside GitHub.
- Owner decision: app license.
- Risk: the Gradle wrapper jar is a binary; delivered as a file.

### 2 Hello GeckoView
- Verify current stable GeckoView and its API first (decision 3).
- Engine adapter file; one screen; one page loads.
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
- Launch test in CI.
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
- Light and dark themes, icons, layout.
- DP8: appearance on device.

### 10 Play Store preparation
- `play` flavor, App Bundle, listing, policy forms, name and trademark check.
- Record the signature difference again in DECISIONS.md.

### 11 Distinguishing features
- Decided by the owner.

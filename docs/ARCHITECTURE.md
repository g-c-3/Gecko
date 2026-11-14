# Architecture

Status: releases from 0.0.6 run system WebView (decisions 26, 32).

## Layers
- `app/`: Kotlin Android module. Plain Android views, custom drawn (DECISIONS.md 30). No Compose, no Mozilla components, no native code (27).
- Engine adapter: one file; the only code that calls `android.webkit` or `androidx.webkit` WebView APIs. An API change is fixed there.
- Engine: the phone's Android System WebView. Library `androidx.webkit`, exact pin in `gradle/libs.versions.toml`. Launch gate (28): WebView version and features checked at every launch.
- Spaces (DECISIONS.md 21): a list of Spaces with ids; each tab's WebView is bound to its Space's profile before any other call on it. Private tabs: temporary profile (29).

## Flavors
- `sideload`: GitHub releases, update button, install permission.
- `play`: Play Store; no updater code, no install permission.

## Resources
- Launcher icon: adaptive, in `app/src/main/res` (`mipmap-anydpi`, `drawable-nodpi`, `values`); DECISIONS.md 19.

## Network
Allowed connections:
1. Pages the user visits.
2. Update check: one GET to `https://api.github.com/repos/g-c-3/weber/releases/latest`, on button tap only, `sideload` only. The one APK is downloaded over HTTPS from that release, SHA-256 verified against release notes, then handed to the system installer. Nothing sent but the requests. Installed through a `PackageInstaller` session; the user confirms in Android (decision 36).
3. Filter-list updates (DECISIONS.md 23): lists built into each release; tap button and a scheduled update (at most daily, Wi-Fi only, switch in Settings). Fetched from the GitHub release host only, SHA-256 verified. The host sees the internet address and request time. Not yet built (Phase 8).

The WebView component's own traffic is Google's and outside Ardour's control (29).
A 403 with a rate-limit message shows "try again later".
A further connection needs approval first (DECISIONS.md 2).

## On-device data
History, bookmarks, cookies, passwords, downloads, tab state, per Space. Never leaves the device except on the owner's explicit export. Private tabs: removed on close (29).
Saved logins: encrypted with a key in the Android key store, one per Space (DECISIONS.md 22). The Space lock is an app-level lock, not file encryption.

## Security checks
Before changing permissions, certificates, content blocking or the manifest:
- WebView settings: file access off, content access off, mixed content never allowed, no universal access from file URLs. JavaScript is on (a browser needs it). No native object exposed to pages except through a message listener with origin rules. Each setting verified against the documentation when written (Phase 2).
- No certificate-check bypass: SSL errors always cancel.
- No cleartext-traffic exceptions.
- No permission grant beyond what a user prompt allows.
- Manifest: INTERNET; `allowBackup=false`; meta-data `EnableSafeBrowsing` false and `MetricsOptOut` true (29, Phase 2).
- Any added library: license (11), size cost (30), no connection of its own.
- Launch gate present and not bypassable (28).
- Advanced settings (DECISIONS.md 20): curated; none weakens the engine, bypasses certificate checks or allows cleartext traffic.
- Manifest items planned later (checked first): biometric, foreground service for media, picture-in-picture, default-browser intent filters, screenshot block.

## Toolchain
Gradle 9.9.1, AGP 9.5.1, JDK 17 (DECISIONS.md 12). Exact pins in `gradle/wrapper/gradle-wrapper.properties` and `gradle/libs.versions.toml`.
The GeckoView additions (Kotlin 2.4.20 override, compileSdk 37.2, `useLegacyPackaging`) are removed in the 0.0.6 files; compileSdk 36 is provisional (32).
`gradlew` loses its executable bit when uploaded through the GitHub web UI; workflows run it as `sh gradlew` or after `chmod +x`.

## Build and CI
- Gradle (Kotlin DSL), run only by GitHub Actions.
- `build-android.yml`, jobs:
  - `build`: debug APK on every push, pull request and manual run. Gradle output goes to `gradle.log`; on failure `.github/ci/annotate-errors.sh` writes the errors as annotations (decision 42).
  - `plan`: on push to `main`; version from the run number (decision 39); releases only if tag `vX.Y.Z` does not exist. Pushes touching only `docs/` or `*.md` do not run.
  - `release`: after `build` and `plan`; signs with repository secrets `KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`; verifies the certificate SHA-256 (DECISIONS.md 15); publishes the release and tag (DECISIONS.md 16). One APK `Ardour-X.Y.Z.apk` (27); release notes carry the WebView requirement and the `androidx.webkit` pin read from `libs.versions.toml`.
- Signing in Gradle reads `KEYSTORE_PATH`, `KEYSTORE_PASSWORD`, `KEY_ALIAS` from the environment; without them release APKs stay unsigned.
- `verification-bootstrap.yml`: manual; writes `verification-metadata.xml` as an artifact (checksums only). Rerun and recommit when dependencies change. A new dependency turns the next build red until then.
- `dependency-update.yml`: scheduled; pull request that changes the `androidx.webkit` pin, regenerates verification metadata, builds. Not yet created (Phase 5).
- Filter-list packaging in CI: not yet created (Phase 8).
- Launch test (emulator installs the APK, loads a local page): not yet created (Phase 5).
- Dependency verification on; a mismatch fails the build.
- Failure triage: owner pastes the failing step only; name the cause, fix the smallest thing.

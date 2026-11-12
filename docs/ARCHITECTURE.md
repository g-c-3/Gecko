# Architecture

## Layers
- `app/`: Kotlin Android module. UI over Mozilla Android Components, or Compose where simpler (decided in DECISIONS.md when Phase 2 starts).
- Engine adapter: one file; the only code that calls GeckoView. An engine API change is fixed there.
- Engine: GeckoView, version in `gradle/libs.versions.toml`.
- Spaces (DECISIONS.md 21): a list of Spaces with ids; each tab carries its Space's engine context id.

## Flavors
- `sideload`: GitHub releases, update button, install permission.
- `play`: Play Store; no updater code, no install permission.

## Resources
- Launcher icon: adaptive, in `app/src/main/res` (`mipmap-anydpi`, `drawable-nodpi`, `values`); DECISIONS.md 19.

## Network
Allowed connections:
1. Pages the user visits.
2. Update check: one GET to `https://api.github.com/repos/g-c-3/Gecko/releases/latest`, on button tap only, `sideload` only. APK downloaded over HTTPS from that release, SHA-256 verified against release notes, then handed to the system installer. Nothing sent but the requests.
3. Filter-list updates (DECISIONS.md 23): lists built into each release; tap button and a scheduled update (at most daily, Wi-Fi only, switch in Settings). Fetched from the GitHub release host only, SHA-256 verified. The host sees the internet address and request time. Not yet built (Phase 8).

A 403 with a rate-limit message shows "try again later".
A further connection needs approval first (DECISIONS.md 2).

## On-device data
History, bookmarks, cookies, passwords, downloads, tab state, per Space. Never leaves the device except on the owner's explicit export. Private tabs write nothing to disk.
Saved logins: encrypted with a key in the Android key store, one per Space (DECISIONS.md 22). The Space lock is an app-level lock, not file encryption.

## Security checks
Before changing permissions, certificates, content blocking or the manifest:
- No weakened engine settings.
- No certificate-check bypass.
- No cleartext-traffic exceptions.
- No permission grant beyond what a user prompt allows.
- Manifest: no permissions yet; `allowBackup=false`.
- Any added Mozilla component: confirm it opens no connection of its own (telemetry disabled or absent).
- GeckoView (Phase 2): telemetry and data-upload prefs off through `GeckoPreferenceController`; every other default background connection listed and switched off or flagged (including tracker-list updates and push).
- Advanced settings (DECISIONS.md 20): curated; no option weakens the engine, bypasses certificate checks or allows cleartext traffic.
- Manifest items planned later (checked first): biometric, foreground service for media, picture-in-picture, default-browser intent filters, screenshot block.

## Toolchain
Gradle 9.9.1, AGP 9.5.1, JDK 17 (DECISIONS.md 12). Exact pins in `gradle/wrapper/gradle-wrapper.properties` and `gradle/libs.versions.toml`.
`gradlew` loses its executable bit when uploaded through the GitHub web UI; workflows run it as `sh gradlew` or after `chmod +x`.

## Build and CI
- Gradle (Kotlin DSL), run only by GitHub Actions.
- `build-android.yml`, jobs:
  - `build`: debug APKs on every push, pull request and manual run.
  - `plan`: on push to `main`; reads `version.properties`; releases only if tag `vX.Y.Z` does not exist.
  - `release`: after `build` and `plan`; signs with repository secrets `KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`; verifies the certificate SHA-256 (DECISIONS.md 15); publishes the release and tag (DECISIONS.md 16).
- Signing in Gradle reads `KEYSTORE_PATH`, `KEYSTORE_PASSWORD`, `KEY_ALIAS` from the environment; without them release APKs stay unsigned.
- `verification-bootstrap.yml`: manual; writes `verification-metadata.xml` as an artifact (checksums only). Rerun and recommit when dependencies change.
- `engine-update.yml`: scheduled; pull request that changes the pin, bumps the patch version, regenerates verification metadata, links Mozilla's advisory, builds. Not yet created (Phase 5).
- Filter-list packaging in CI: not yet created (Phase 8).
- Launch test (emulator installs the APK, loads a local page): not yet created (Phase 5).
- Dependency verification on; a mismatch fails the build.
- Failure triage: owner pastes the failing step only; name the cause, fix the smallest thing.

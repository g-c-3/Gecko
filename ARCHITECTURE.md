# Architecture

## Layers
- `app/`: Kotlin Android module. UI over Mozilla Android Components, or Compose where simpler (decided in DECISIONS.md when Phase 2 starts).
- Engine adapter: one file; the only code that calls GeckoView. An engine API change is fixed there.
- Engine: GeckoView, version in `gradle/libs.versions.toml`.

## Flavors
- `sideload`: GitHub releases, update button, install permission.
- `play`: Play Store; no updater code, no install permission.

## Network
Allowed connections:
1. Pages the user visits.
2. Update check: one GET to `https://api.github.com/repos/g-c-3/Gecko/releases/latest`, on button tap only, `sideload` only. APK downloaded over HTTPS from that release, SHA-256 verified against release notes, then handed to the system installer. Nothing sent but the requests.

A 403 with a rate-limit message shows "try again later".
A third connection needs approval first (DECISIONS.md 2).

## On-device data
History, bookmarks, cookies, passwords, downloads, tab state. Never leaves the device except on the owner's explicit export. Private tabs write nothing to disk.

## Security checks
Before changing permissions, certificates, content blocking or the manifest:
- No weakened engine settings.
- No certificate-check bypass.
- No cleartext-traffic exceptions.
- No permission grant beyond what a user prompt allows.
- Any added Mozilla component: confirm it opens no connection of its own (telemetry disabled or absent).

## Build and CI
- Gradle (Kotlin DSL), run only by GitHub Actions.
- `build-android.yml`: build, sign, test, release on tag `vX.Y.Z`. Launch test: emulator installs the APK and loads a local test page.
- `engine-update.yml`: scheduled; pull request that changes the pin, bumps the patch version, regenerates verification metadata, links Mozilla's advisory, builds.
- Dependency verification on; a mismatch fails the build.
- Failure triage: owner pastes the failing step only; name the cause, fix the smallest thing.
- Not yet created (Phase 1 and 5).

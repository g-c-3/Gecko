# Decisions

Numbered, never dated. Dates live in TRACK.md.

## 1. Public repo, source and docs only
Rule: no secret of any kind in any file, comment, workflow or commit message. Secrets live only in repository secrets.

## 2. Privacy by default
Rule: no telemetry, analytics, crash reporting, accounts, cloud sync or automated inference. Mozilla components with telemetry code (Glean and others) are disabled or not included; each session that adds one checks it opens no network connection of its own.
Allowed network traffic: pages the user visits; the tap-only update check (sideload flavor); filter-list updates (23).
Any new connection is flagged first and needs approval, with its data stated.
Browsing data (history, bookmarks, cookies, passwords, downloads, tab state) stays on-device. Private tabs leave nothing on disk.

## 3. Engine (superseded by 26; the adapter rule lives on there)
Rule: GeckoView, release channel only. Exact versions pinned in `gradle/libs.versions.toml`; no dynamic versions. Version verified against Mozilla's documentation and Maven listing before any dependency line is written.
All engine calls go through one adapter file. Fewest possible Mozilla Android Components.
Each session checks for a newer stable GeckoView and reports it. A bump is its own task with its own CI check.

## 4. Versioning (D digit and two-APK parts superseded by 27)
Rule: `X.Y.Z`, X unbounded, Y 0 to 9, Z 0 to 99. Start `0.0.1`. Set only in `version.properties`.
`versionCode = (X*10000 + Y*100 + Z)*10 + D`; D = 1 universal, 2 arm64, 0 Play bundle.
Compared number by number, never as text. Every shipped change bumps Z by one. Tag `vX.Y.Z` triggers the release.

## 5. Releases (two-APK part superseded by 27)
Rule: two signed APKs per release (arm64, universal). Release notes carry engine version, advisory link for engine updates, and each APK's SHA-256.
Releases are never deleted. A faulty release is marked pre-release; the previous good release stays downloadable.

## 6. Signing
Rule: one key for every release. Keystore and passwords only in repository secrets. The owner keeps a separate backup outside GitHub; losing it means no in-place updates.
Signing is its own phase (1).

## 7. Dependency verification
Rule: `gradle/verification-metadata.xml` is on. A checksum mismatch fails the build. Never disabled to get a build through.

## 8. Flavors
Rule: `sideload` (GitHub releases; update button; install permission) and `play` (neither). Updater code is never compiled into `play`.
Play shows one listing named Ardour. Play-signed and sideloaded builds carry different signatures and cannot update over each other; recorded again when Phase 10 starts.

## 9. Phase 0 scope
Rule: Phase 0 delivers docs, `version.properties` and `.gitignore` only.
Cause: Gradle files need plugin and wrapper versions, which must be verified first. They arrive in Phase 1.

## 10. Doc size
Rule: any of ROADMAP.md, TRACK.md, SESSIONS.md, DECISIONS.md passing about 60 KB is reported at "Go" with a proposed archive. Archive only on approval. Numbering never changes.

## 11. Licensing
Rule: each added library's license is recorded here when adopted. The app's own code is MPL 2.0 (17); no engine code is in the app after 26.
GeckoView (MPL 2.0) was in releases 0.0.4 and 0.0.5 and is removed (26).
`androidx.webkit`: Apache 2.0 per Android's library listing; confirm when adopted in Phase 2. Apache 2.0 is compatible with a public MPL 2.0 repo.

## 12. Build toolchain (verified in session 2)
Rule: Gradle 9.9.1 (wrapper, distribution checksum pinned), Android Gradle Plugin 9.5.1, JDK 17. Kotlin comes from AGP's built-in Kotlin; no separate Kotlin plugin.
Sources: Gradle version list (services.gradle.org/versions/9), AGP 9.5 release notes (AGP 9.5 needs Gradle 9.6.0 or newer, JDK 17), runner image readme (JDK 17 default, Android platform 36 and 37.0 installed).
Workflow actions pinned by commit SHA when `build-android.yml` is written. Latest tags seen: checkout v7.0.1, setup-java v6.2.0, gradle/actions v6.4.1, upload-artifact v7.0.2, softprops/action-gh-release v3.0.4.
SDK levels are provisional until Phase 2 verifies the engine's requirement.

## 13. Signing material never leaves secrets
Rule: no keystore, password or key in a workflow artifact, log, input or summary.
Cause: artifacts of a public repo can be downloaded by any signed-in GitHub user; workflow inputs and logs are public.

## 14. Workflow hygiene
Rule: every third-party action pinned by full commit SHA with its tag in a comment. Wrapper validated in CI. No caching action (its license and data handling are unverified); builds use the wrapper directly.
Workflows request `contents: read` only.

## 15. Release key made outside GitHub (exception to 6 and 13)
Rule: one RSA 4096 key, alias `ardour`, valid 100 years, generated once outside GitHub and delivered to the owner as files. The owner loaded it into repository secrets (`KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`). The owner keeps the keystore file and its password as the backup outside GitHub.
Cause: owner decision; the key could not be generated on a phone.
Risk: the key and password passed through a chat transcript. Rotate before the Play listing if that is a concern; nothing signed is in use beyond the owner's device.
Certificate SHA-256 (public): `0d11cb47bebabb3197d38fed88e6c2f3c5cc5863e3931cdb6a4e976eda591c03`. The release job fails if an APK carries another certificate.
Standing for 6 and 13: keystore and passwords never in the repo, workflow artifacts, logs or inputs.

## 16. Automatic release
Rule: a push to `main` whose `version.properties` holds a version with no tag `vX.Y.Z` builds, signs and publishes that release. The release creates the tag on the pushed commit. This replaces "a tag triggers the release" in 4 and 5.
Release job: only job with `contents: write` and with secrets; keystore file removed after the build; APK names `Ardour-X.Y.Z-arm64.apk` and `Ardour-X.Y.Z-universal.apk`; notes carry engine, certificate SHA-256 and each APK's SHA-256. No third-party release action; GitHub's own `gh` command.
A code change without a version bump releases nothing.
Cause of the first defect: the tag was taken from the branch name. Fixed: `v<version>`. The mistaken release and tag `main` were removed; an exception to "never deleted", since nobody had installed it.

## 17. App license
Rule: the app's own code is MPL 2.0 (matches Gecko). `LICENSE` at the repo root, text copied from Mozilla's geckodriver repository. No per-file headers.
GeckoView is MPL 2.0; recorded under 11 when adopted in Phase 2.

## 18. Application id and SDK levels
Rule: application id `io.github.gc3.ardour`. The source package and `namespace` stay `io.github.g_c_3.ardour` (internal only).
Release 0.0.1 carries the old id `io.github.g_c_3.ardour`; it cannot update to 0.0.2 and is superseded. It stays downloadable.
minSdk is 29 from 28. SDK levels: compileSdk follows what `androidx.webkit` needs, checked in Phase 2.
The id cannot change after a Play listing exists.

## 19. Launcher icon
Rule: adaptive icon `mipmap-anydpi/ic_launcher.xml`. Foreground `drawable-nodpi/ic_launcher_foreground.png` (432 px, transparent, the flame "A" only, no wordmark). Background color `#242630`. Referenced by `android:icon`. No legacy raster icons (minSdk 26); no monochrome layer yet.
Source: logo supplied by the owner, made with an external image tool; the owner keeps the original file.
Usage rights and the trademark check are settled before the Play listing (Phase 10). The 512 px store icon is made then from the same logo.
Shipped as 0.0.3.

## 20. Product direction
Rule: Ardour is a private, light, customisable browser built around Spaces (21). Settings open with basic options only; an Advanced section holds the full range. Fast and smooth is a target of every phase, checked on device.
Advanced options are curated: none weakens the engine, bypasses certificate checks or allows cleartext traffic. A raw engine preference editor is not included unless approved.
Features are planned in ROADMAP.md; each phase is approved when it starts (25).

## 21. Spaces
Rule: a Space is an isolated browsing area: own cookies, cache, site storage, saved logins, history, private tabs. The term "Space" is used, not "account", to avoid a clash with 2.
Six built-in Spaces. Space 1 is the default and can be neither deleted nor hidden. Built-in Spaces can be renamed and given a colour and icon. Up to ten in total: Spaces 7 to 10 are added by the user and can be deleted.
Switching Space, adding or deleting a Space, and the setting "open with default or last-used Space" need device authentication (biometric or phone password). Deleting erases the Space's data after a confirmation.
Clearing cache and site data per Space keeps saved logins; a separate action clears logins.
Spaces are stored as a list with ids, never as fixed numbers. A Space holds no data until first opened.
Engine: one WebView profile per Space, bound before any other call on that WebView (26); the API form is verified against the documentation in Phase 2.

## 22. Space lock and saved logins
Rule: the lock is an app-level lock, not file encryption; Settings says so. Saved logins are encrypted with a key in Android's secure key store, one per Space.
Authentication uses `androidx.biometric` (Apache 2.0) or the platform equivalent; the license is recorded under 11 when adopted.
Shortcuts to a Space still require authentication. The panic shortcut wipes and locks without a prompt.

## 23. Second approved connection: filter-list updates
Rule: ad-filter lists are built into each release, so blocking works with no connection. Updates: a tap-only button, and a scheduled background update at most once a day, on Wi-Fi only, with a Settings switch that turns it off completely.
Lists come only from the GitHub release host used by the update check, built and checksummed by CI. The app verifies SHA-256 before use.
Data: the host sees the device's internet address and the request time, at regular intervals. Nothing else is sent: no token, no identifier, no page visited.
Amends 2. Phase 8 verifies the license of the lists against MPL 2.0 (11, 17) before building; a blocker returns here for approval. Blocking is built by Ardour on WebView (31).

## 24. Engine target (superseded by 26; GeckoView 159.0.20261102144711 was pinned in 0.0.4 and 0.0.5)
Rule: Phase 2 pins GeckoView 159.0.x (stable). 160.0 is beta; the earlier candidate `160.0.20261104181552` is dropped.
Candidates and fallback: ROADMAP.md, Phase 2. CI resolution confirms the pin.
Evidence: Firefox for Android release notes (stable 159.0.1; 160.0 in beta), Maven mirror listing, a third-party release naming a 159.0 build. Mozilla's Maven not read directly (unreachable from the sandbox).

## 25. Feature plan and approval
Rule: two-step approval. Only Phase 2 is approved; other features are planned in ROADMAP.md and approved per phase.
Build order: 2, 3, 4, 12, 5, 6, 7, 8, 11, 13, 9, 10. First public release after Phase 8.
Not included: data-saver proxy, voice search, cloud sync and backup, crypto wallet.
Needs its own approval: forced dark mode on every page, page translation, web push, add-on store, cookie-banner lists, encrypted DNS, search suggestions, safe-browsing lookups.
Similar Gecko-based browsers exist. No code is copied from them; one is AGPL-3.0, which conflicts with 17.

## 26. Engine: system WebView
Rule: Ardour is built on Android System WebView through `androidx.webkit`. GeckoView is dropped. Supersedes 3 and 24. Owner decision.
Cause: GeckoView's floor is about 91 MB (arm64, libraries compressed, release 0.0.5; its main library alone is about 65 MB compressed). WebView keeps the app to a few MB.
Kept from 3: every engine call goes through one adapter file; exact pins in `gradle/libs.versions.toml`, no dynamic versions; each API and version checked against the documentation before use; each session reports the newest stable `androidx.webkit`.
Added: stable APIs only (experimental APIs may disappear after one library release). Security posture is the phone's WebView version; Settings shows it.
Accepted costs: the engine and its updates are Google's; behaviour varies by phone and WebView version; features Gecko had natively are built by Ardour or reduced (31).
Removed in Phase 2: the GeckoView dependency, Mozilla's Maven repository, the Kotlin 2.4.20 classpath override, compileSdk 37.2 and compressed-library packaging, all added for GeckoView; the build decides what `androidx.webkit` needs.
Releases 0.0.4 and 0.0.5 carry GeckoView and stay downloadable. The first WebView release installs over them (same id and key, higher versionCode).
Evidence: `androidx.webkit` release notes and API reference (profiles since 1.9.0; need multi-process mode); sizes measured from the 0.0.4 and 0.0.5 APKs.

## 27. One APK
Rule: one signed APK per release, `Ardour-X.Y.Z.apk`, for every phone. Kotlin and Java only: no native code, so no ABI splits. Native code needs approval and returns here.
`versionCode = (X*10000 + Y*100 + Z)*10 + D`; D = 1 sideload APK, 0 Play bundle; 2 retired (was arm64). The code only increases: 0.0.5 arm64 was 52, 0.0.6 gives 61.
Release notes carry the WebView requirement (28), certificate SHA-256 and the APK's SHA-256. The updater downloads that one APK; no device matching.
Supersedes the two-APK parts of 4, 5 and 16.

## 28. Minimum phone and WebView gate
Rule: minSdk 29 (Android 10); amends 18. At every launch the app reads the WebView package version and checks the features it needs (profiles). If unmet it shows a full-screen message to update Android System WebView and loads nothing. No code for old engines.
Minimum WebView version: set in Phase 2 from the documentation of the features Ardour needs; recorded here then.
Cause: Chrome 139 reportedly requires Android 10 or newer, so WebView on Android 8 and 9 stays on an old version. Source: news reports of Google's announcement, which conflict in detail; not read directly. Confirm in Phase 2.

## 29. Privacy under WebView
Rule: Ardour's own code sends nothing (2 holds). Manifest opt-outs, both documented by Android: Safe Browsing off (`android.webkit.WebView.EnableSafeBrowsing` false) and WebView diagnostic reporting off (`android.webkit.WebView.MetricsOptOut` true). A Safe Browsing switch in Settings is a new connection and needs approval (25).
Limit: the WebView component is Google's. Its own traffic (updates, component data) is outside Ardour's control and not audited. The listing and Settings never claim otherwise.
Private tabs: a temporary profile, deleted when the tab closes and at next launch. Wording is "removed on close", not "never written to disk".
Encrypted DNS: only the phone's Private DNS setting.
Amends 2: its Mozilla-components clause no longer applies.

## 30. UI toolkit and quality bar
Rule: plain Android views, custom drawn, with spring-based animation. No Jetpack Compose, no Mozilla components. Premium look and motion are the goal; size and speed are measured, never only claimed.
Size target about 5 MB, confirmed by the first WebView build. Every added dependency states its size cost; each session entry reports the APK size. Start-up and frame-rate targets are set after the first on-device measurement (ROADMAP, Quality targets). Public claims state measured results only.
Amends 20: "light" means small and lean in fact.

## 31. Features under WebView
Rule: planned features (ROADMAP) stay, built on WebView.
Native or simple: tabs, Spaces, find in page, desktop site, text size, print, pull-to-refresh, fullscreen video and picture-in-picture, permissions, popup blocking, history, bookmarks, downloads, screenshot block, panic wipe.
Built by Ardour: content blocking (request blocking; page-element hiding by script injected before page scripts), reader mode, saved logins, private tabs, resumable downloads, privacy report, tab sleep, data saver.
Reduced: fingerprint protection (script level, best effort, off by default); Do Not Track and Global Privacy Control (main-frame headers and a script flag, not a header on every request); private tabs (29); passkeys (verify WebView support before promising).
Not used: rewriting requests (needs Ardour's own fetch; breaks caching and cookies).
Amends 23.

## 32. Adoption of `androidx.webkit` 1.18.0 (Phase 2)
Rule: pinned exactly in `gradle/libs.versions.toml`. Newest stable per developer.android.com release notes (1.18.0, 21 October 2026); no stable above it seen. License: Apache 2.0 per Maven metadata; confirms 11. The library brings androidx annotation and core; size cost is read from the first build.
compileSdk 36 is provisional; the build shows whether the library needs more.
Launch gate (amends 28): no minimum WebView version number is claimed. The gate is the run-time check that a WebView package exists and `WebViewFeature.MULTI_PROFILE` is supported.
Phase 2 binds the Default profile. Named Space profiles wait for Phase 12, after the profile-name rules are verified.
Code shrinking (R8) is not enabled until the first size measurement.
WebView settings verified: mixed content defaults to never-allow for apps targeting API 21 and higher (platform source); file-URL access defaults false since API 16, universal access deprecated from API 30 (developer.android.com, unsafe file inclusion). All set explicitly.

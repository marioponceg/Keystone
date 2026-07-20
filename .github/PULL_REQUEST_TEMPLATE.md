## What

<!-- What does this PR change? One design unit per PR (see AGENTS.md). -->

## Why

<!-- Motivation and context. Reference the relevant design decision in AGENTS.md if applicable. -->

## Verification

<!-- How was this verified beyond CI, if applicable (manual testing on device/emulator, golden review, etc.)? -->

- [ ] `./gradlew build` passes locally (compile + unit tests + Android Lint)
- [ ] `./gradlew detekt` passes locally
- [ ] `./gradlew koverVerify` passes locally (core modules ≥ 90% line coverage)
- [ ] `./gradlew verifyRoborazziDebug` passes locally (screenshot goldens; regenerate deliberately with `recordRoborazziDebug`)
- [ ] New or changed behavior is covered by tests (screenshot tests for anything visual)
- [ ] PR title follows Conventional Commits (it becomes the squash commit on `main`)

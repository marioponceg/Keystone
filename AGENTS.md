# AGENTS.md

Guidance for AI agents (and humans) working in this repository. This is the source of truth for
project conventions; `CLAUDE.md` simply points here. The full v0.1 design rationale lives in
`docs/superpowers/specs/2026-07-19-keystone-v0.1-design.md`.

## Project

Keystone is an Android companion app for World of Warcraft Mythic+, built as an open-source
**portfolio piece demonstrating senior-level engineering practices** and the final piece of the
portfolio: it consumes the three published libraries under `io.github.marioponceg` — Conduit
(networking), Quill (structured logging) and Foundry (Compose design system) — all published to
Maven Central at 0.1.0. Its primary audience is technical reviewers and recruiters reading the
commit history, PRs, and code.

## Settled design decisions — do not re-litigate, implement as stated

- **v0.1 scope**: search a character (name + realm + region) → Mythic+ profile (season score,
  best runs per dungeon, weekly affixes) + a local list of recent searches. Data source is the
  public, auth-free Raider.IO API. No backend, no login, no metrics. Firebase/Crashlytics
  (including a future `QuillSink` to Crashlytics), Battle.net OAuth and the official Blizzard API
  are future versions.
- **Raider.IO as the v0.1 data source**: two auth-free endpoints (`characters/profile` and
  `mythic-plus/affixes`), no API key, no rate-limit handling beyond standard error mapping.
- **Three-module clean architecture**: `core-domain` (pure Kotlin/JVM: models, use cases,
  repository contracts, no Android/third-party runtime deps beyond coroutines — designed for a
  future KMP/iOS migration), `core-data` (pure Kotlin/JVM, possible because Conduit is JVM-pure:
  Raider.IO client, DTOs, mappers, repository implementations for the remote side only), `app`
  (`com.android.application`: Compose + Foundry, ViewModels, Navigation3, Hilt wiring, Quill
  setup, and the DataStore implementation of `RecentSearchesRepository` — keeping both core
  modules Android-free).
- **MVVM with strict UDF over formal MVI (settled, with rationale)**: one immutable `UiState` per
  screen exposed via `StateFlow`, user events as a sealed interface, one-shot effects separated
  from state. This is Google's official architecture guidance and the pattern implemented by Now
  in Android — the recognized modern standard — and it already captures MVI's core benefits
  (single source of truth, unidirectional flow, per-variant renderable states feeding previews and
  screenshot tests). Formal MVI (Orbit/MVIKotlin or a hand-rolled reducer pipeline) buys structural
  determinism and team homogeneity at a fixed per-screen ceremony cost — worth it with highly
  concurrent state sources, large teams, or auditable state history requirements; none apply to
  two screens in a solo portfolio app. Revisit if a future version adds screens with genuinely
  composite concurrent state (e.g. OAuth flows).
- **Navigation3** with a `NavBackStack` and serializable keys (`HomeKey`, `CharacterDetailKey`).
- **Hilt** for DI across the `app` module.
- **`PreviewParameterProvider` rule**: Compose `@Preview`s always use `PreviewParameterProvider`;
  the same providers feed the Roborazzi screenshot tests, one per `UiState` variant, light and
  dark.
- **Save-recents-on-success**: a recent search is saved only when the character profile loads
  successfully (not on submit), so typos never pollute the recents list.
- **Never published**: the app is not published to Maven Central — no BCV, no maven-publish.

Any design decision **not** listed above must be raised with the maintainer before implementing.

## Module structure

```
Keystone/
├── build-logic/    # convention plugins (keystone.jvm.library)
├── core-domain/    # pure Kotlin/JVM: models, use cases, repository contracts
├── core-data/      # pure Kotlin/JVM: Raider.IO client (Conduit), DTOs, mappers, repo impls
└── app/            # com.android.application: Compose + Foundry, ViewModels, Navigation3,
                    # Hilt wiring, Quill setup, DataStore impl of RecentSearchesRepository
```

- `core-domain` and `core-data` apply the `keystone.jvm.library` convention plugin (Kotlin/JVM,
  detekt, Kover with a 90% minimum coverage rule, JUnit 5 platform, JVM toolchain 21).
- `app` is configured directly (a convention plugin for a single consumer is overhead).
- Wire each new module into `settings.gradle.kts` (`include(...)`).

## Build & toolchain

- AGP 9.2.1 with **built-in Kotlin** — never apply `org.jetbrains.kotlin.android`; the Compose
  compiler plugin (`org.jetbrains.kotlin.plugin.compose`, version = bundled Kotlin) is still
  applied on the `app` module.
- JVM toolchain 21 on the pure-Kotlin/JVM modules (`core-domain`, `core-data`); Java 17
  source/target compatibility on the `app` module. minSdk 26, compileSdk 37.
- Dependency versions live only in `gradle/libs.versions.toml` — never inline version strings in
  build files. Convention plugins live in `build-logic`.
- Detekt fails the build on any unresolved issue (`buildUponDefaultConfig = true`, no `maxIssues`
  key needed since detekt 1.23's default). Config: `config/detekt/detekt.yml`.
- Configuration cache is enabled.

## Verification commands — run before considering any task done

```sh
./gradlew build detekt koverVerify verifyRoborazziDebug
```

- `build` compiles everything and runs all unit tests + Android Lint.
- `detekt` runs static analysis + formatting (ktlint rules via detekt-formatting).
- `koverVerify` enforces the 90% minimum coverage rule on `core-domain` and `core-data`.
- `verifyRoborazziDebug` runs screenshot tests against committed goldens once screens exist;
  golden images live in `app/src/test/screenshots/`, regenerated deliberately with
  `./gradlew recordRoborazziDebug` — review the diff before committing.
- A task is not done until these commands pass locally; CI runs the same commands on every PR.

## Commit & branching conventions

- **Conventional Commits** for every commit: `feat:`, `fix:`, `chore:`, `docs:`, `test:`,
  `refactor:`, `ci:`, `build:` …
- **Trunk-based workflow**: `main` is protected by a repository ruleset — PR required (no direct
  pushes), force-pushes and deletion blocked, CI and PR-title checks must pass. All work happens
  on short-lived feature branches (`feature/<topic>`) merged via PR.
- **Small, reviewable PRs** — one design unit per PR, each including its own tests (screenshot
  tests for anything visual). PR descriptions explain the *why*.
- **PRs are squash-merged.** The PR title becomes the commit on `main`, so it must follow
  Conventional Commits — CI validates this (`.github/workflows/pr-title.yml`).
- **Stacked PRs** (a PR whose base is another feature branch, as multi-task plans produce): once
  the base PR is squash-merged, **rebase the stacked branch onto `main` and force-push with
  `--force-with-lease` before merging it**. GitHub retargets the stacked PR to `main` by itself —
  the repository has *Automatically delete head branches* enabled, and deleting the base branch
  retargets every open PR pointing at it — but the retargeted diff still replays the base PR's
  files, because the squash commit on `main` is not the commits the stacked branch was built on.
  The rebase drops those already-upstream commits and restores the one-design-unit diff the PR is
  supposed to show. Merging a stacked PR whose base branch still exists writes the squash commit
  onto that branch instead of `main`; PRs #12 and #16 exist only to recover work lost that way.

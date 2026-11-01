# KMovie

A movie browser for Android built with Jetpack Compose and Clean Architecture, using [The Movie Database (TMDB)](https://www.themoviedb.org/) API.

- **Trending** movies of the day
- **Browse by genre** with infinite scroll (Paging 3)
- **Movie details** with rating, runtime, genres, overview and cast
- Loading, error and retry states on every screen; survives rotation and process death

## Tech stack

| Area | Libraries |
|---|---|
| UI | Jetpack Compose, Material 3, Navigation Compose (type-safe routes), Coil |
| Architecture | MVVM, Clean Architecture, Hilt |
| Async | Kotlin Coroutines and Flow, Paging 3 |
| Network | Retrofit, OkHttp, kotlinx.serialization |
| Testing | JUnit, kotlinx-coroutines-test, Paging testing, MockWebServer, Compose UI tests |
| Quality | ktlint, Android Lint, GitHub Actions, R8 |

## Architecture

```
:domain   (pure Kotlin, no Android)    models · MovieRepository interface · use cases
   ▲
:app
 ├─ data   DTOs · mappers · Retrofit API · paging source · MovieRepositoryImpl
 ├─ di     Hilt modules (network, repository bindings)
 └─ ui     screens (Route + stateless Screen) · ViewModels exposing a single UiState
```

- **`:domain` is a separate pure-Kotlin module**, so the compiler stops it from depending on Android, Retrofit or serialization code.
- **Dependencies point inward:** `data` implements the `MovieRepository` interface defined in `domain`, and `ui` only talks to use cases.
- **DTOs are kept separate from domain models.** Mappers turn nullable API fields into safe, non-null domain values.
- **Errors are typed.** Repositories return `Result<T>` whose failure is always a `DataError` (`Network`, `Unauthorized`, `NotFound`, `Unknown`), and the UI turns that into a message with a Retry button.
- **Screens are split into `XRoute` + `XScreen`.** `XRoute` connects the ViewModel; `XScreen` only takes state and callbacks, so it can be previewed and UI-tested without Hilt.

## Setup

1. Get a free API key at <https://www.themoviedb.org/settings/api>.
2. Add it to `local.properties` in the project root (this file is git-ignored):
   ```properties
   TMDB_API_KEY=your_key_here
   ```
3. Open the project in Android Studio and run the `app` configuration.

The key is passed to `BuildConfig` at build time and added to each request by an OkHttp interceptor, so it never appears in source code. On CI it is read from the `TMDB_API_KEY` environment variable.

> An API key bundled in an app can still be extracted from the APK. Production apps should call TMDB through their own backend; for a client-only demo app, keeping the key out of version control is the practical baseline.

## Running checks

```bash
./gradlew ktlintCheck :app:lintDebug          # code style and Android Lint
./gradlew :domain:test :app:testDebugUnitTest # unit tests (no device or API key needed)
./gradlew :app:connectedDebugAndroidTest      # Compose UI tests (device or emulator)
```

Unit tests cover the use cases, mappers, API-key interceptor, repository (against MockWebServer: JSON mapping, HTTP errors, duplicate results), paging source, and both ViewModels (loading/error/retry, genre switching, process-death restore through `SavedStateHandle`).

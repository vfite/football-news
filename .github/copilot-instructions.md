# Copilot Instructions — Football News (FootballUA)

## Build & Test Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run a single unit test class
./gradlew test --tests "com.vfite.football.ExampleUnitTest"

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest
```

## Architecture Overview

This is a single-module Android app (`:app`) whose primary surface is a **home screen widget** — there is no main UI beyond `MainActivity` (a launcher stub) and `NewsActivity`. Almost all logic lives under `widget/`.

### Layer structure inside `widget/`

```
widget/
  system/    — Android framework classes (AppWidgetProvider, BroadcastReceiver, RemoteViewsService)
  ui/        — LatestNewsViewModel (plain class, NOT AndroidViewModel)
  renderer/  — IRemoteViewRenderer implementations that produce RemoteViews
  domain/    — Use cases (Load*, Refresh*)
  data/      — DataSources (in-memory), SyncService, WorkManager job
```

### Data flow for widget update

1. `FootballWidgetProvider.onUpdate()` reads `LatestNewsViewModel.mode` (NEWS or MATCHES)
2. Retrieves the correct `IRemoteViewRenderer` from DI by tag (`WidgetMode` enum)
3. Calls `renderer.render(widgetId)` → returns `RemoteViews`
4. User button taps go to `LatestNewsBroadcastReceiver` → mutates `LatestNewsViewModel` → calls `widgetService.invalidateWidget()`
5. Background sync is triggered via `RemoteSyncService` which enqueues a `SyncNewsJob` (WorkManager `CoroutineWorker`)

### Dependency Injection

Uses **Kodein DI** with a global container (`DI.global`). Three modules are registered in `FootballApplication`:

| Module | Key bindings |
|---|---|
| `networkModule` | `Moshi`, `Retrofit`, `ApiInterface`, `IRemoteViewRenderer` (tagged by `WidgetMode`) |
| `domainModule` | `SyncService`, `LatestNewsDataSource`, `MatchesDataSource`, use cases, `LatestNewsViewModel` |
| `systemModule` | `IWidgetService` → `SystemWidgetService` |

Components outside `Application` (e.g. `SyncNewsJob`, `FootballWidgetProvider`, `LatestNewsBroadcastReceiver`) resolve dependencies directly via `DI.global.direct.instance()`.

## Key Conventions

### FeedResult — result wrapper
All data operations return `FeedResult<T>`, a sealed class with `Success` and `Error`. Use the chainable extensions instead of `when` where possible:
```kotlin
feedResult
    .onSuccess { data -> /* handle */ }
    .onError { e -> /* handle */ }
```

### SyncState — widget sync state machine
`SyncService.state` is a `SyncState` sealed class: `Synced | Syncing | Failed | None`. Renderers switch on this state to show the loading/failure/content layout.

### IRemoteViewRenderer — tagged DI binding
Renderers are bound in DI with a `WidgetMode` tag. To add a new widget mode, bind a new `IRemoteViewRenderer` with its tag in `networkModule` and handle it in `LatestNewsViewModel.toggleMode()`.

### API URLs via gradle.properties
`API_URL` and `NEWS_URL` are injected as `BuildConfig` fields. Override them in `gradle.properties` (not committed). Defaults fall back to `https://football.com/` placeholders.

### Data sources are in-memory only
`RealLatestNewsDataSource` and `RealMatchesDataSource` hold data in memory. There is no local database. `FileStorage` exists but is unused in the current flow.

### Logging
Use `Timber.d(...)` / `Timber.e(...)` throughout — never `Log.*` directly.

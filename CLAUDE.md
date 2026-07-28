# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Architecture Overview

This is a multi-module project whose primary surface is a **home screen widget** — there is no main UI beyond `MainActivity` (a launcher stub) and `NewsActivity`.

- **`:app`** — Android application module. Owns the widget system classes, DI wiring, and view-layer rendering.
- **`:shared-domain`** — Kotlin Multiplatform library module (Android + iOS targets) added to make the domain/data/networking layer reusable outside `:app`. 
- All platform-agnostic business logic lives here now; `:app` only contains Android-specific glue.

### What lives where

| Package (root: `com.vfite.football`) | Module | Contents |
|---|---|---|
| `api/` | `:shared-domain` (commonMain) | `ApiClient` (raw Ktor calls), `ApiRepository` (interface), `KtorApiRepository` (impl), `HttpClientEngine` (`expect`) |
| `api/` | `:shared-domain` (androidMain/iosMain) | `actual fun createHttpClientEngine()` — OkHttp on Android, Darwin on iOS |
| `data/`, `data/models/` | `:shared-domain` | `LatestNewsDataSource` / `MatchesDataSource` interfaces + in-memory `Real*` impls, feed/item models |
| `domain/` | `:shared-domain` | `FeedResult`, `SyncService` (interface + `SyncState`), use cases (`Load*`, `Refresh*`) |
| `widget/IWidgetService.kt` | `:shared-domain` | Interface only — implementation is Android-specific |
| `di/` (`SharedDomainModule.kt`, `ApiModule.kt`) | `:shared-domain` | Kodein modules binding the above; `apiModule(baseUrl)` builds the `HttpClient`/`ApiClient`/`ApiRepository` graph, `sharedDomainModule()` binds data sources + use cases |
| `widget/system/`, `widget/renderer/`, `widget/ui/`, `widget/jobs/`, `widget/data/RemoteSyncService.kt` | `:app` | Android framework classes, `IRemoteViewRenderer` impls, `LatestNewsViewModel`, WorkManager job, `SyncService` impl |
| `di/` (`networkModule.kt`, `domainModule.kt`, `systemService.kt`) | `:app` | Kodein modules binding Android-specific pieces (renderers, `SyncService` impl, `IWidgetService` impl, `LatestNewsViewModel`) |

Rule of thumb: if new code has no Android framework dependency (pure data/business logic, or something an iOS target would also need), it belongs in `:shared-domain`'s `commonMain`. 
Widget rendering, `Context`-dependent services, and WorkManager jobs stay in `:app`.

### Data flow for widget update

1. `FootballWidgetProvider.onUpdate()` reads `LatestNewsViewModel.mode` (NEWS or MATCHES)
2. Retrieves the correct `IRemoteViewRenderer` from DI by tag (`WidgetMode` enum)
3. Calls `renderer.render(widgetId)` → returns `RemoteViews`
4. User button taps go to `LatestNewsBroadcastReceiver` → mutates `LatestNewsViewModel` → calls `widgetService.invalidateWidget()`
5. Background sync is triggered via `RemoteSyncService` (`SyncService` impl) which enqueues a `SyncNewsJob` (WorkManager `CoroutineWorker`)

## Key Conventions

### FeedResult — result wrapper
All data operations return `FeedResult<T>` (`:shared-domain`, `domain/FeedResult.kt`), a sealed class with `Success` and `Error`. Use the chainable extensions instead of `when` where possible:
```kotlin
feedResult
    .onSuccess { data -> /* handle */ }
    .onError { e -> /* handle */ }
```

### SyncState — widget sync state machine
`SyncService.state` (interface in `:shared-domain`, implemented by `RemoteSyncService` in `:app`) is a `SyncState` sealed class: `Synced | Syncing | Failed | None`. Renderers switch on this state to show the loading/failure/content layout.

### IRemoteViewRenderer — tagged DI binding
Renderers are bound in DI with a `WidgetMode` tag. To add a new widget mode, bind a new `IRemoteViewRenderer` with its tag in `networkModule` and handle it in `LatestNewsViewModel.toggleMode()`.

### Ktor HTTP client (shared-domain)
`ApiClient` wraps a Ktor `HttpClient` configured with `ContentNegotiation` (kotlinx.serialization JSON, `ignoreUnknownKeys = true`) and `Logging`.

### Data sources are in-memory only
`RealLatestNewsDataSource` and `RealMatchesDataSource` hold data in memory. 
There is no local database. 

### Logging
Use `Timber.d(...)` / `Timber.e(...)` throughout — never `Log.*` directly.

---
name: architecture-overview
description: >
  Concise reference for the Football News app's domain layer and widget data flows.
  Use this skill when working on any code inside widget/, domain/, di/, or api/ packages,
  or when tracing how data moves from network to RemoteViews.
---

## Domain abstractions

### FeedResult\<T\>

All data operations return `FeedResult<T>` — a sealed class in `domain/FeedResult.kt`.

```kotlin
sealed class FeedResult<out T> {
    class Success<T>(val feed: T) : FeedResult<T>()
    class Error(val e: Exception)  : FeedResult<Nothing>()
}
```

Chain results without nested `when` blocks using the extensions:

```kotlin
result
    .onSuccess { data -> /* happy path */ }
    .onError   { e    -> /* error path */ }
```

### SyncState

`SyncService.state` is a sealed class (`widget/data/SyncService.kt`):

| State      | Meaning                                      |
|------------|----------------------------------------------|
| `None`     | Initial — nothing requested yet              |
| `Syncing`  | WorkManager job is running                   |
| `Synced`   | Last sync completed successfully             |
| `Failed`   | Last sync completed with an error            |

Renderers switch on `SyncState` to decide which layout to show (loading spinner / failure / content).

### Key interfaces

| Interface             | Implementation          | Scope       | Role                                                      |
|-----------------------|-------------------------|-------------|-----------------------------------------------------------|
| `LatestNewsDataSource`| `RealLatestNewsDataSource` | in-memory | Read/write news items; triggers `syncNews()` when empty  |
| `MatchesDataSource`   | `RealMatchesDataSource`    | in-memory | Read/write today's match items                           |
| `SyncService`         | `RemoteSyncService`        | singleton | Enqueue WorkManager jobs; own `SyncState` flow           |
| `IWidgetService`      | `SystemWidgetService`      | singleton | Call `invalidateWidget()` to trigger `AppWidgetProvider.onUpdate()` |
| `IRemoteViewRenderer` | `LatestNewsRenderer` / `MatchDayRenderer` | provider (tagged) | Build `RemoteViews` for a given `WidgetMode` |

---

## Use case catalogue

All use cases live in `widget/domain/`.

| Use case                 | Suspend? | Inputs                            | Returns                  | Side-effects                                              |
|--------------------------|----------|-----------------------------------|--------------------------|-----------------------------------------------------------|
| `LoadLatestNewsUseCase`  | no       | `forceLoad: Boolean`              | `FeedResult<NewsFeed>`   | Calls `syncService.syncNews()` if data source is empty    |
| `LoadDayMatchesUseCase`  | no       | `forceLoad: Boolean`              | `List<MatchItem>`        | None (pure read)                                          |
| `RefreshDataUseCase`     | **yes**  | –                                 | `FeedResult<NewsData>`   | Saves news + matches to data sources; calls `invalidateWidget()` |
| `RefreshMatchesUseCase`  | **yes**  | –                                 | `FeedResult<MatchesData>`| Saves matches to data source; calls `invalidateWidget()`  |

`Refresh*` use cases are only called from `SyncNewsJob` (background). `Load*` use cases are called from `LatestNewsViewModel` (widget render path).

---

## Data flows

### 1. Read flow — widget render

Triggered by `IWidgetService.invalidateWidget()` or the system widget update timer.

```
FootballWidgetProvider.onUpdate(appWidgetIds)
  └─ reads LatestNewsViewModel.mode            ← current WidgetMode (NEWS | MATCHES)
  └─ DI.global.instance<IRemoteViewRenderer>(mode)
       ├─ LatestNewsRenderer  (WidgetMode.NEWS)
       │    └─ reads LatestNewsViewModel.state (SyncState)
       │         ├─ Syncing / None → show spinner layout
       │         ├─ Failed         → show failure layout + retry button
       │         └─ Synced         → LatestNewsViewModel.getCurrentItem()
       │                               └─ LoadLatestNewsUseCase.execute(false)
       │                                    └─ LatestNewsDataSource.loadNews()
       │                                         └─ if empty → SyncService.syncNews()  [triggers sync flow]
       └─ MatchDayRenderer    (WidgetMode.MATCHES)
            └─ LatestNewsViewModel.getMatches()
                 └─ LoadDayMatchesUseCase.execute(false)
                      └─ MatchesDataSource.loadMatches()
  └─ appWidgetManager.updateAppWidget(widgetId, remoteViews)
```

### 2. Sync flow — background data refresh

Triggered by user button tap in the widget → `BroadcastReceiver`.

```
LatestNewsBroadcastReceiver.onReceive(intent)
  └─ ACTION_SYNC         → LatestNewsViewModel.syncNews()
  │                           └─ SyncService.syncNews()
  │                                └─ RemoteSyncService.createWorker(SYNC_NEWS_MODE)
  └─ ACTION_SYNC_MATCHES → LatestNewsViewModel.syncMatches()
  │                           └─ SyncService.syncMatches()
  │                                └─ RemoteSyncService.createWorker(MATCHES_NEWS_MODE)
  └─ ACTION_SHOW_MATCHES → LatestNewsViewModel.toggleMode()
                            └─ IWidgetService.invalidateWidget()  [→ read flow]

WorkManager (SyncNewsJob.doWork)
  ├─ SYNC_NEWS_MODE    → RefreshDataUseCase.execute()
  │                        ├─ ApiInterface.getNewsFeed()         → LatestNewsDataSource.saveNews()
  │                        ├─ ApiInterface.getMatchesFeed(date)  → MatchesDataSource.saveMatches()
  │                        └─ IWidgetService.invalidateWidget()  [→ read flow]
  └─ MATCHES_NEWS_MODE → RefreshMatchesUseCase.execute()
                           ├─ ApiInterface.getMatchesFeed(date)  → MatchesDataSource.saveMatches()
                           └─ IWidgetService.invalidateWidget()  [→ read flow]

SyncState transition (both paths):
  Syncing  →  Synced  (on success)
  Syncing  →  Failed  (on error)
```

`SyncState` is held in a `MutableStateFlow` inside `RemoteSyncService`.
State changes propagate to `LatestNewsRenderer` on the next widget render cycle (triggered by `invalidateWidget()`).

---

## Adding new use cases or data sources

1. Define an interface in `widget/data/` (e.g. `StandingsDataSource`).
2. Provide an in-memory implementation in the same file.
3. Bind both in `di/domainModule.kt` using `bind<Interface>() with singleton { Impl() }`.
4. Create the use case class in `widget/domain/`; inject via constructor.
5. Bind the use case in `domainModule` (use `provider` for stateless, `singleton` for stateful).
6. If a new `WidgetMode` is needed, add it to `WidgetMode` enum, bind a new `IRemoteViewRenderer` tagged with it in `networkModule`, and handle the toggle in `LatestNewsViewModel.toggleMode()`.

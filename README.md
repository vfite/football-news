# Football-news

Football-news is a widget-magazine that brings you the latest news from Ukrainian and world football. 
With the Football-news widget on your home screen, you're always up to date — no need to open an app.

The widget has two main tabs:
- **News** — scroll through the latest football headlines
- **Live** — follow live scores for top matches as they happen

## Screenshots

<p float="left">
  <img src="screenshots/widget-news.jpg" width="250" alt="Widget showing a news item">
  <img src="screenshots/widget-live-scores.jpg" width="250" alt="Widget showing a news item">
  <img src="screenshots/widget-loading.jpg" width="250" alt="Widget in loading state">
  <img src="screenshots/widget-error.jpg" width="250" alt="Widget showing an error state">
</p>

## Architecture

The app is a home-screen **App Widget**, with a thin companion app used only to host the launch surface and deep-link into news items.

- **Domain layer** — use cases (`LoadLatestNewsUseCase`, `LoadDayMatchesUseCase`, `RefreshDataUseCase`, `RefreshMatchesUseCase`) fetch and expose data as a `FeedResult<T>` (`Success`/`Error`). Background refresh is driven by `WorkManager` jobs (`SyncNewsJob`) kicked off through `RemoteSyncService`, which tracks progress as `SyncState` (`Syncing`/`Synced`/`Failed`).
- **UI layer** — `LatestNewsViewModel` is the single source of truth for widget state (current news item, matches, News/Matches mode). `IRemoteViewRenderer` implementations turn that state into `RemoteViews`, rendered by `FootballWidgetProvider` on update and by `MatchesRemoteService`/`MatchesProviderFactory` for the scrollable matches list. `LatestNewsBroadcastReceiver` handles widget button actions (refresh, next/previous, toggle mode) and feeds back into the ViewModel.
- **Data flow**: user action or scheduled sync → `SyncService` enqueues a `WorkManager` job → use case hits the API → result updates `SyncState`/`LatestNewsViewModel` → widget is invalidated → renderers rebuild `RemoteViews` for `AppWidgetManager`.

Dependency injection uses **Kodein-DI** via global modules, since widget components (`AppWidgetProvider`, `RemoteViewsService`) aren't constructor-injectable and resolve dependencies from `DI.global` directly.

**On the `feature/KMP-intro` branch, the domain/data/networking layer above is being migrated into a `:shared-domain` Kotlin Multiplatform module (Android + iOS, on Ktor), leaving `:app` with only Android-specific widget and DI glue.**


# FootballUA

A home-screen widget that shows the latest Ukrainian football news and today's matches, fetched from a remote feed.

## Language

### Content

**News**:
The latest batch of news items from the news feed, shown one at a time in the widget.
_Avoid_: Articles, posts

**Today's Matches**:
The matches scheduled for the current calendar date. Matches for any other date are never shown.
_Avoid_: Match day, fixtures, matches list

### Caching

**Snapshot**:
The complete set of News or Today's Matches from the last successful refresh, kept on the device so it survives the app being killed. A new snapshot replaces the old one entirely; nothing from older refreshes is kept.
_Avoid_: Cache entry, history, local copy

**Refresh**:
Fetching News or Today's Matches from the remote feed and, on success, replacing that Snapshot. It is the only way a Snapshot ever changes.
_Avoid_: Reload, fetch, update

**Last-known content**:
The current Snapshot, always shown together with the time it was fetched; if the latest Refresh failed, it is additionally marked as failed. The remote feed remains the source of truth.
_Avoid_: Offline data, cached data, stale data

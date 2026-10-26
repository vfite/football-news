# Widget reads the cache once per render; nothing observes Room

The widget reads the current Snapshot directly from the repositories on each draw instead of observing Room Flows. 
We considered a long-lived observer started from the widget lifecycle (`onEnabled` / `Application.onCreate`), 
but the process is killed often and `onEnabled` doesn't re-run after process death, 
so every cold wake would still need a blocking first read; 
and the only writer is `refresh()`, whose completion `SyncNewsJob` already signals. 
Revisit if a second writer to the database appears.

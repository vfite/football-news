# Room cache: open decisions and facts

**Purpose:** close the last open items before implementing the Room KMP cache for News and Today's Matches, so `docs/specs/room-cache.md` can drop its "unconfirmed" list.

**From:** Claude, **To:** Vadim (Android dev), **How your answers will be used:** confirmed decisions go into the spec; facts adjust the implementation plan and tests.

## Context

The design is settled in `docs/specs/room-cache.md` and `docs/adr/0001-widget-reads-cache-per-render.md`, using the terms in `CONTEXT.md`. Q17–Q21 below were recommended but never confirmed. The test seam is proposed but not agreed. A few real-world facts can't be read from the code. Each decision shows the recommendation; "yes" is a full answer.

## How to answer

About 15 minutes. Write under each `>`. "Don't know" or "check later" is fine; flag it rather than skip.

## Decisions (Q17–Q21)

### Q17: Should each platform build the Room builder and pass it into a shared `databaseModule(builder)`, with the shared side setting the driver, dispatcher and migration?

_Recommended: yes. It mirrors `apiModule(baseUrl)` and keeps `Context` out of `:shared-domain`._

>

### Q18: Should the refresh for a missing Snapshot be started in the `onUpdate` path, after reading and only when not already `Syncing`, instead of in the ViewModel/render code?

_Recommended: yes. Drawing stays free of side effects._

>

### Q19: Should the news image load synchronously with a ~3 s timeout inside `goAsync()`, drawing without an image on timeout, and `isVideoLoaded` be deleted?

_Recommended: yes. Reading per draw breaks the redraw-after-load trick, which would loop forever._

>

### Q19b: If not 3 s, what timeout?

_Why this matters: `goAsync()` has a budget of about 10 s for the whole read, image and render._

>

### Q20: Should `currentItem` reset to 0 after a successful News Refresh and be clamped to the Snapshot size on every read?

_Recommended: yes._

>

### Q21: Should `:shared-domain` get an `expect`/`actual` logger (Timber on Android, `NSLog`/`println` on iOS) that replaces the `println`s?

_Recommended: yes. Alternatives are Kermit or Napier as a dependency._

>

## Testing

### Do you agree that the repositories are the only test seam: a real in-memory Room database plus a fake `ApiRepository`?

_Why this matters: widget glue (`goAsync`, Glide, the loop guard) would then be checked by hand only._

>

### Where should the repository tests run: the iOS simulator test target (no `Context` needed) or an Android host test with Robolectric?

_Why this matters: the iOS target needs a Mac/Xcode toolchain in CI; Robolectric adds a dependency and slower tests._

>

### Is it OK to make the "today" date injectable (e.g. a clock or date-provider parameter) so tests can move midnight?

>

### Is there a CI setup these tests must run in? If so, which?

>

## Feed API facts

### What timezone is "today" in for the matches endpoint: the device's, Kyiv's, or UTC?

_Why this matters: the today-only rule compares the stored request date with the device's date; a mismatch would hide valid matches around midnight._

>

### Is an empty `items` list a valid response (no matches today), or does the API return an error then?

_Why this matters: an empty list is stored as "fetched but empty". An error would leave the old Snapshot and the failed marker._

>

### Are news `id` and match `gameId` stable and unique within one response?

_Why this matters: they become Room primary keys. Duplicates inside one response would fail the insert._

>

### Can `gameId` be null in practice? It is nullable in `MatchItem`.

_Why this matters: if it can be null, matches need a generated primary key instead._

>

### Roughly how many news items and matches does one response contain?

>

## Device behaviour

### On your test devices, how often does the system's `onUpdate` actually fire with `updatePeriodMillis = 2h`?

_Why this matters: this is now the only automatic refresh. If it's much rarer in practice, the "updated HH:mm" ages badly._

>

### Is a refresh roughly every 2 hours (longer during Doze) acceptable on match days?

_Why this matters: if not, the decision to skip a periodic worker should be revisited._

>

### Have you seen the widget's process killed while the widget stays on screen? Roughly how soon after the last interaction?

>

## Build setup

### Are you willing to add KSP and the Room Gradle plugin to `:shared-domain` on Kotlin 2.0.0, or upgrade Kotlin first?

_Why this matters: KSP and Room versions must match the Kotlin version; an upgrade may also touch Ktor and Kodein._

>

### Is the extra iOS binary size from bundling SQLite (`sqlite-bundled`) acceptable?

>

### Where should the exported Room schema live, and should it be committed?

_Recommended: `shared-domain/schemas`, committed._

>

## Anything else?

Anything not asked here that the implementation should know?

>

package com.vfite.football.data

import com.vfite.football.data.db.Feed
import com.vfite.football.data.db.FeedInfoEntity
import com.vfite.football.data.db.SnapshotDao
import com.vfite.football.data.db.toEntity
import com.vfite.football.data.db.toModel
import com.vfite.football.data.models.MatchItem
import com.vfite.football.data.models.Snapshot
import com.vfite.football.domain.FeedResult
import kotlinx.datetime.Instant

interface MatchesDataSource {

    /**
     * The stored Matches Snapshot if it was requested for [date]; null when never fetched,
     * fetched for another date, or unreadable. Never throws.
     */
    suspend fun loadMatches(date: String): Snapshot<MatchItem>?

    /** Replaces the stored Matches Snapshot with [items] requested for [date] and fetched at [fetchedAt]. Never throws; a storage failure is returned as [FeedResult.Error]. */
    suspend fun saveMatches(date: String, items: List<MatchItem>, fetchedAt: Instant): FeedResult<Unit>
}

class RoomMatchesDataSource(
    private val dao: SnapshotDao
) : MatchesDataSource {

    override suspend fun loadMatches(date: String): Snapshot<MatchItem>? = storageCall("loadMatches", fallback = null) {
        dao.loadMatches(date)?.let { (info, items) ->
            Snapshot(
                items = items.map { it.toModel() },
                fetchedAt = Instant.fromEpochMilliseconds(info.fetchedAtMillis)
            )
        }
    }

    override suspend fun saveMatches(date: String, items: List<MatchItem>, fetchedAt: Instant): FeedResult<Unit> = storageResult("saveMatches") {
        dao.replaceMatches(
            info = FeedInfoEntity(
                feed = Feed.MATCHES,
                fetchedAtMillis = fetchedAt.toEpochMilliseconds(),
                requestedDate = date
            ),
            items = items.mapIndexed { position, item -> item.toEntity(position) }
        )
    }
}

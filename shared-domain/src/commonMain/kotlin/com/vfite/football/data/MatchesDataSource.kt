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
import kotlinx.datetime.LocalDate

interface MatchesDataSource {

    suspend fun loadMatches(date: LocalDate): Snapshot<MatchItem>?

    suspend fun saveMatches(date: LocalDate, items: List<MatchItem>, fetchedAt: Instant): FeedResult<Unit>
}

class RoomMatchesDataSource(
    private val dao: SnapshotDao
) : MatchesDataSource {

    override suspend fun loadMatches(date: LocalDate): Snapshot<MatchItem>? = storageCall("loadMatches", fallback = null) {
        dao.loadMatches(date)?.let { (info, items) ->
            Snapshot(
                items = items.map { it.toModel() },
                fetchedAt = Instant.fromEpochMilliseconds(info.fetchedAtMillis)
            )
        }
    }

    override suspend fun saveMatches(date: LocalDate, items: List<MatchItem>, fetchedAt: Instant): FeedResult<Unit> = storageResult("saveMatches") {
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

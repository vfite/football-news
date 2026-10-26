package com.vfite.football.data

import com.vfite.football.data.db.Feed
import com.vfite.football.data.db.FeedInfoEntity
import com.vfite.football.data.db.SnapshotDao
import com.vfite.football.data.db.toEntity
import com.vfite.football.data.db.toModel
import com.vfite.football.data.models.NewsItem
import com.vfite.football.data.models.Snapshot
import com.vfite.football.domain.FeedResult
import kotlinx.datetime.Instant

interface LatestNewsDataSource {

    suspend fun loadNews(): Snapshot<NewsItem>?

    suspend fun saveNews(items: List<NewsItem>, fetchedAt: Instant): FeedResult<Unit>
}

class RoomLatestNewsDataSource(
    private val dao: SnapshotDao
) : LatestNewsDataSource {

    override suspend fun loadNews(): Snapshot<NewsItem>? = storageCall("loadNews", fallback = null) {
        dao.loadNews()?.let { (info, items) ->
            Snapshot(
                items = items.map { it.toModel() },
                fetchedAt = Instant.fromEpochMilliseconds(info.fetchedAtMillis)
            )
        }
    }

    override suspend fun saveNews(items: List<NewsItem>, fetchedAt: Instant): FeedResult<Unit> = storageResult("saveNews") {
        dao.replaceNews(
            info = FeedInfoEntity(
                feed = Feed.NEWS,
                fetchedAtMillis = fetchedAt.toEpochMilliseconds(),
                requestedDate = null
            ),
            items = items.mapIndexed { position, item -> item.toEntity(position) }
        )
    }
}

package com.vfite.football.data

import com.vfite.football.data.models.MatchItem
import com.vfite.football.data.models.NewsItem
import com.vfite.football.data.models.Snapshot
import com.vfite.football.domain.FeedResult
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

class InMemoryNewsDataSource : LatestNewsDataSource {
    private var snapshot: Snapshot<NewsItem>? = null

    override suspend fun loadNews(): Snapshot<NewsItem>? = snapshot

    override suspend fun saveNews(items: List<NewsItem>, fetchedAt: Instant): FeedResult<Unit> {
        snapshot = Snapshot(items, fetchedAt)
        return FeedResult.Success(Unit)
    }
}

class InMemoryMatchesDataSource : MatchesDataSource {
    private var date: LocalDate? = null
    private var snapshot: Snapshot<MatchItem>? = null

    override suspend fun loadMatches(date: LocalDate): Snapshot<MatchItem>? =
        snapshot.takeIf { this.date == date }

    override suspend fun saveMatches(date: LocalDate, items: List<MatchItem>, fetchedAt: Instant): FeedResult<Unit> {
        this.date = date
        snapshot = Snapshot(items, fetchedAt)
        return FeedResult.Success(Unit)
    }
}

package com.vfite.football.data

import com.vfite.football.data.models.Snapshot
import com.vfite.football.domain.FeedResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class NewsRepositoryTest {

    private val api = FakeApiRepository()
    private val clock = FakeClock(Instant.parse("2026-10-14T10:15:00Z"))

    private fun repository(dataSource: LatestNewsDataSource = InMemoryNewsDataSource()) =
        DefaultNewsRepository(api, dataSource, clock, Dispatchers.Unconfined)

    @Test
    fun neverFetchedNewsHasNoSnapshot() = runTest {
        assertEquals(Snapshot.notFetched(), repository().getNews())
    }

    @Test
    fun successfulRefreshStoresItemsWithFetchTime() = runTest {
        val repository = repository()
        api.newsResult = FeedResult.Success(newsData(newsItem(1), newsItem(2)))

        repository.refresh()

        val snapshot = repository.getNews()
        assertEquals(listOf(1L, 2L), snapshot.items.map { it.id })
        assertEquals(clock.now, snapshot.fetchedAt)
    }

    @Test
    fun refreshWithEmptyListStoresEmptySnapshot() = runTest {
        val repository = repository()
        api.newsResult = FeedResult.Success(newsData())

        repository.refresh()

        assertEquals(Snapshot(items = emptyList(), fetchedAt = clock.now), repository.getNews())
    }

    @Test
    fun failedRefreshKeepsPreviousSnapshot() = runTest {
        val repository = repository()
        api.newsResult = FeedResult.Success(newsData(newsItem(1)))
        repository.refresh()
        val fetchedAt = clock.now
        clock.now = Instant.parse("2026-10-14T12:15:00Z")
        api.newsResult = FeedResult.Error(Exception("offline"))

        val result = repository.refresh()

        assertIs<FeedResult.Error>(result)
        val snapshot = repository.getNews()
        assertEquals(listOf(1L), snapshot.items.map { it.id })
        assertEquals(fetchedAt, snapshot.fetchedAt)
    }

    @Test
    fun failedFirstRefreshLeavesNoSnapshot() = runTest {
        val repository = repository()
        api.newsResult = FeedResult.Error(Exception("offline"))

        repository.refresh()

        assertEquals(Snapshot.notFetched(), repository.getNews())
    }
}

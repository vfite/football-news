package com.vfite.football.data

import com.vfite.football.data.models.Snapshot
import com.vfite.football.domain.FeedResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class MatchesRepositoryTest {

    private val api = FakeApiRepository()
    private val clock = FakeClock(Instant.parse("2026-10-14T10:15:00Z"))

    private fun repository(dataSource: MatchesDataSource = InMemoryMatchesDataSource()) =
        DefaultMatchesRepository(api, dataSource, clock, { TimeZone.UTC }, Dispatchers.Unconfined)

    @Test
    fun neverFetchedMatchesHaveNoSnapshot() = runTest {
        assertEquals(Snapshot.notFetched(), repository().getTodayGames())
    }

    @Test
    fun storedMatchesFromYesterdayAreNotReturned() = runTest {
        val dataSource = InMemoryMatchesDataSource()
        dataSource.saveMatches(
            date = "2026-10-13",
            items = listOf(matchItem(7)),
            fetchedAt = Instant.parse("2026-10-13T18:00:00Z")
        )

        assertEquals(Snapshot.notFetched(), repository(dataSource).getTodayGames())
    }

    @Test
    fun refreshRequestsTodaysMatches() = runTest {
        api.matchesResult = FeedResult.Success(matchesData())

        repository().refresh()

        assertEquals(listOf("2026-10-14"), api.requestedMatchDates)
    }

    @Test
    fun successfulRefreshStoresTodaysMatchesWithFetchTime() = runTest {
        val repository = repository()
        api.matchesResult = FeedResult.Success(matchesData(matchItem(7), matchItem(8)))

        repository.refresh()

        val snapshot = repository.getTodayGames()
        assertEquals(listOf(7, 8), snapshot.items.map { it.gameId })
        assertEquals(clock.now, snapshot.fetchedAt)
    }

    @Test
    fun refreshWithNoMatchesTodayStoresEmptySnapshot() = runTest {
        val repository = repository()
        api.matchesResult = FeedResult.Success(matchesData())

        repository.refresh()

        assertEquals(Snapshot(items = emptyList(), fetchedAt = clock.now), repository.getTodayGames())
    }

    @Test
    fun failedRefreshKeepsPreviousSnapshot() = runTest {
        val repository = repository()
        api.matchesResult = FeedResult.Success(matchesData(matchItem(7)))
        repository.refresh()
        api.matchesResult = FeedResult.Error(Exception("offline"))

        val result = repository.refresh()

        assertIs<FeedResult.Error>(result)
        assertEquals(listOf(7), repository.getTodayGames().items.map { it.gameId })
    }
}

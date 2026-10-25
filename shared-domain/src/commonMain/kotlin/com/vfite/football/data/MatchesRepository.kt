package com.vfite.football.data

import com.vfite.football.api.ApiRepository
import com.vfite.football.data.models.MatchesData
import com.vfite.football.data.models.MatchItem
import com.vfite.football.data.models.Snapshot
import com.vfite.football.domain.FeedResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

interface MatchesRepository {

    /**
     * The current Today's Matches Snapshot; a not-fetched (empty) Snapshot when matches were
     * never fetched or the stored ones belong to another day. Never hits the network.
     */
    suspend fun getTodayGames(): Snapshot<MatchItem>

    /** Fetches today's matches from the API and, on success only, replaces the Snapshot. */
    suspend fun refresh(): FeedResult<MatchesData>
}

class DefaultMatchesRepository(
    private val apiRepository: ApiRepository,
    private val matchesDataSource: MatchesDataSource,
    private val clock: Clock = Clock.System,
    private val timeZone: () -> TimeZone = { TimeZone.currentSystemDefault() },
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : MatchesRepository {

    override suspend fun getTodayGames(): Snapshot<MatchItem> = withContext(ioDispatcher) {
        matchesDataSource.loadMatches(date = today()) ?: Snapshot.notFetched()
    }

    override suspend fun refresh(): FeedResult<MatchesData> = withContext(ioDispatcher) {
        println("MatchesRepository refresh")
        val date = today()
        val matchesResponse = apiRepository.getMatchesFeed(date)
        if (matchesResponse is FeedResult.Success) {
            val saved = matchesDataSource.saveMatches(
                date = date,
                items = matchesResponse.feed.data.items,
                fetchedAt = clock.now()
            )
            if (saved is FeedResult.Error) {
                return@withContext saved
            }
        }
        matchesResponse
    }

    private fun today(): String = clock.todayIn(timeZone()).toString()
}

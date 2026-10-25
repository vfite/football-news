package com.vfite.football.domain

import com.vfite.football.data.MatchesRepository
import com.vfite.football.data.NewsRepository
import com.vfite.football.data.models.NewsData
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class RefreshDataUseCase(
    val newsRepository: NewsRepository,
    val matchesRepository: MatchesRepository
) {

    suspend fun execute(): FeedResult<NewsData> = coroutineScope {
        val newsResponse = async { newsRepository.refresh() }
        val matchesResponse = async { matchesRepository.refresh() }
        awaitAll(newsResponse, matchesResponse)
        newsResponse.await()
    }
}

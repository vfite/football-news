package com.vfite.football.domain

import com.vfite.football.api.ApiRepository
import com.vfite.football.data.models.NewsData
import com.vfite.football.util.getCurrentDate
import com.vfite.football.widget.IWidgetService
import com.vfite.football.data.LatestNewsDataSource
import com.vfite.football.data.MatchesDataSource

class RefreshDataUseCase(
    val apiRepository: ApiRepository,
    val latestNewsDataSource: LatestNewsDataSource,
    val matchesDataSource: MatchesDataSource,
    val widgetService: IWidgetService
) {

    suspend fun execute(): FeedResult<NewsData> {
        val newsResponse = apiRepository.getNewsFeed()
        val matchesResponse = apiRepository.getMatchesFeed(getCurrentDate())
        if (newsResponse is FeedResult.Success) {
            latestNewsDataSource.saveNews(newsResponse.feed.data)
        }
        if (matchesResponse is FeedResult.Success) {
            matchesDataSource.saveMatches(matchesResponse.feed.data.items)
        }
        widgetService.invalidateWidget()
        return newsResponse
    }
}

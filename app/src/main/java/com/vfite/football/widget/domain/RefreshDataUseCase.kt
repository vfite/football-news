package com.vfite.football.widget.domain

import com.vfite.football.api.ApiInterface
import com.vfite.football.api.handleApi
import com.vfite.football.domain.FeedResult
import com.vfite.football.models.NewsData
import com.vfite.football.widget.IWidgetService
import com.vfite.football.widget.data.LatestNewsDataSource
import com.vfite.football.widget.data.MatchesDataSource
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RefreshDataUseCase(
    val apiInterface: ApiInterface,
    val latestNewsDataSource : LatestNewsDataSource,
    val matchesDataSource: MatchesDataSource,
    val widgetService: IWidgetService
) {

    suspend fun execute() : FeedResult<NewsData> {
        val newsResponse = handleApi {  apiInterface.getNewsFeed() }
        val matchesResponse = handleApi { apiInterface.getMatchesFeed(getCurrentDate()) }
        if (newsResponse is FeedResult.Success) {
            latestNewsDataSource.saveNews(newsResponse.feed.data)
        }
        if (matchesResponse is FeedResult.Success) {
            matchesDataSource.saveMatches(matchesResponse.feed.data.items)
        }

        widgetService.invalidateWidget()
        return newsResponse
    }

    private fun getCurrentDate() : String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
}
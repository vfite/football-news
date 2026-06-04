package com.vfite.football.widget.domain

import com.vfite.football.api.ApiInterface
import com.vfite.football.api.handleApi
import com.vfite.football.domain.FeedResult
import com.vfite.football.models.MatchesData
import com.vfite.football.util.getCurrentDate
import com.vfite.football.widget.IWidgetService
import com.vfite.football.widget.data.MatchesDataSource

class RefreshMatchesUseCase(
    val apiInterface: ApiInterface,
    val matchesDataSource: MatchesDataSource,
    val widgetService: IWidgetService
) {

    suspend fun execute(): FeedResult<MatchesData> {
        val matchesResponse = handleApi { apiInterface.getMatchesFeed(getCurrentDate()) }
        if (matchesResponse is FeedResult.Success) {
            matchesDataSource.saveMatches(matchesResponse.feed.data.items)
        }

        widgetService.invalidateWidget()
        return matchesResponse
    }
}
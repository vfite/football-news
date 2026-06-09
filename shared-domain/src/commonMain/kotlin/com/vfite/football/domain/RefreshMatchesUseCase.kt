package com.vfite.football.domain

import com.vfite.football.api.ApiRepository
import com.vfite.football.data.models.MatchesData
import com.vfite.football.util.getCurrentDate
import com.vfite.football.widget.IWidgetService
import com.vfite.football.data.MatchesDataSource

class RefreshMatchesUseCase(
    val apiRepository: ApiRepository,
    val matchesDataSource: MatchesDataSource,
    val widgetService: IWidgetService
) {

    suspend fun execute(): FeedResult<MatchesData> {
        val matchesResponse = apiRepository.getMatchesFeed(getCurrentDate())
        if (matchesResponse is FeedResult.Success) {
            matchesDataSource.saveMatches(matchesResponse.feed.data.items)
        }
        widgetService.invalidateWidget()
        return matchesResponse
    }
}

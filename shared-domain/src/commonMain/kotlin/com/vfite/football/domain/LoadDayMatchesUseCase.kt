package com.vfite.football.domain

import com.vfite.football.data.models.MatchItem
import com.vfite.football.data.MatchesDataSource

class LoadDayMatchesUseCase(
    val matchesDataSource: MatchesDataSource,
    val syncService: SyncService
) {

    fun execute(forceLoad: Boolean): List<MatchItem> {
        return matchesDataSource.loadMatches(forceLoad)
    }
}

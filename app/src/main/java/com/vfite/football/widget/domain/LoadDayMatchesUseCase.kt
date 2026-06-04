package com.vfite.football.widget.domain

import com.vfite.football.models.MatchItem
import com.vfite.football.widget.data.MatchesDataSource
import com.vfite.football.widget.data.SyncService
import timber.log.Timber

class LoadDayMatchesUseCase(
    val matchesDataSource: MatchesDataSource,
    val syncService: SyncService
) {

    fun execute(forceLoad: Boolean) : List<MatchItem> {
        Timber.d("execute")
        return matchesDataSource.loadMatches(forceLoad)
    }
}
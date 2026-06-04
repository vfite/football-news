package com.vfite.football.widget.data

import com.vfite.football.models.MatchItem
import timber.log.Timber

interface MatchesDataSource {
    suspend fun saveMatches(list: List<MatchItem>)
    fun loadMatches(forceLoad: Boolean): List<MatchItem>

}

class RealMatchesDataSource : MatchesDataSource {

    private var matches: MutableList<MatchItem> = mutableListOf()

    override suspend fun saveMatches(list: List<MatchItem>) {
        Timber.d("save matches : $list")
        matches.clear()
        matches.addAll(list)
    }

    override fun loadMatches(forceLoad: Boolean): List<MatchItem> {
        return matches
    }

}


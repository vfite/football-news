package com.vfite.football.data

import com.vfite.football.data.models.MatchItem

interface MatchesDataSource {
    suspend fun saveMatches(list: List<MatchItem>)
    fun loadMatches(forceLoad: Boolean): List<MatchItem>
}

class RealMatchesDataSource : MatchesDataSource {

    private var matches: MutableList<MatchItem> = mutableListOf()

    override suspend fun saveMatches(list: List<MatchItem>) {
        matches.clear()
        matches.addAll(list)
    }

    override fun loadMatches(forceLoad: Boolean): List<MatchItem> {
        return matches
    }
}

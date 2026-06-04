package com.vfite.football.api

import com.vfite.football.models.MatchesData
import com.vfite.football.models.NewsData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("news/data/?do=main&page=1&limit=20")
    suspend fun getNewsFeed(): Response<NewsData>

    @GET("games/data/?do=day")
    suspend fun getMatchesFeed(@Query("date") date: String = "2024-08-18"): Response<MatchesData>
}
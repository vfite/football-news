package com.vfite.football.api

import com.vfite.football.data.models.MatchesData
import com.vfite.football.data.models.NewsData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

/**
 * Thin wrapper around the Ktor [HttpClient] for the football data endpoints.
 * Throws on network/HTTP errors; callers are expected to translate failures
 * into [com.vfite.football.domain.FeedResult] (see [KtorApiRepository]).
 */
class ApiClient(
    private val httpClient: HttpClient,
    private val baseUrl: String
) {

    suspend fun getNewsFeed(): NewsData =
        httpClient.get("${baseUrl}news/data/") {
            parameter("do", "main")
            parameter("page", 1)
            parameter("limit", 20)
        }.body()

    suspend fun getMatchesFeed(date: String): MatchesData =
        httpClient.get("${baseUrl}games/data/") {
            parameter("do", "day")
            parameter("date", date)
        }.body()
}

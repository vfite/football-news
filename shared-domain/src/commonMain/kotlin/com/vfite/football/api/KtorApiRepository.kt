package com.vfite.football.api

import com.vfite.football.data.models.MatchesData
import com.vfite.football.data.models.NewsData
import com.vfite.football.domain.FeedResult
import io.ktor.client.plugins.ResponseException
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException

/**
 * [ApiRepository] implementation backed by Ktor's [ApiClient]. Replaces the
 * old Retrofit-based `RetrofitApiRepository` + `handleApi` in `:app`.
 */
class KtorApiRepository(
    private val apiClient: ApiClient
) : ApiRepository {

    override suspend fun getNewsFeed(): FeedResult<NewsData> =
        safeCall { apiClient.getNewsFeed() }

    override suspend fun getMatchesFeed(date: String): FeedResult<MatchesData> =
        safeCall { apiClient.getMatchesFeed(date) }

    private suspend fun <T : Any> safeCall(execute: suspend () -> T): FeedResult<T> {
        return try {
            FeedResult.Success(execute())
        } catch (e: UnresolvedAddressException) {
            FeedResult.Error(Exception(e.message, e))
        } catch (e: SerializationException) {
            FeedResult.Error(Exception(e.message, e))
        } catch (e: Exception) {
            FeedResult.Error(Exception(e.message, e))
        }
    }
}

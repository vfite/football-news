package com.vfite.football.api

import com.vfite.football.domain.FeedResult
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

suspend fun <T : Any> handleApi(
    execute: suspend () -> Response<T>
): FeedResult<T> {
    return try {
        val response = execute()
        val body = response.body()
        if (response.isSuccessful && body != null) {
            FeedResult.Success(body)
        } else {
            FeedResult.Error(Exception (response.message()))
        }
    } catch (e: HttpException) {
        FeedResult.Error(IOException(e.message()))
    } catch (e: Exception) {
        FeedResult.Error(IOException(e.localizedMessage))
    }
}
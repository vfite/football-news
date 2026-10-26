package com.vfite.football.data

import com.vfite.football.domain.FeedResult
import kotlinx.coroutines.CancellationException

internal suspend fun <T> storageCall(name: String, fallback: T, block: suspend () -> T): T {
    return try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        fallback
    }
}

internal suspend fun storageResult(name: String, block: suspend () -> Unit): FeedResult<Unit> {
    return try {
        block()
        FeedResult.Success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        FeedResult.Error(e)
    }
}

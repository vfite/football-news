package com.vfite.football.data

import com.vfite.football.domain.FeedResult
import kotlinx.coroutines.CancellationException

/**
 * Storage counterpart of the API's `safeCall`: a locked, corrupt or unopenable database
 * must never throw out of a data source (an undeclared exception is fatal for Swift callers).
 */
internal suspend fun <T> storageCall(name: String, fallback: T, block: suspend () -> T): T {
    return try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        println("Storage $name failed: $e")
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
        println("Storage $name failed: $e")
        FeedResult.Error(e)
    }
}

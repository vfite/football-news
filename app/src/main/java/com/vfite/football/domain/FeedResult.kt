package com.vfite.football.domain


sealed class FeedResult <out T> {
    class Error(val e : Exception) : FeedResult<Nothing>()
    class Success<T>(val feed: T): FeedResult<T>()
}

fun <T> FeedResult<T>.onSuccess( block : (T) -> Unit): FeedResult<T> {
    if (this is FeedResult.Success) {
        block(this.feed)
    }
    return this
}

fun <T> FeedResult<T>.onError(block :(Exception) -> Unit): FeedResult<T> {
    if (this is FeedResult.Error) {
        block(this.e)
    }
    return this
}
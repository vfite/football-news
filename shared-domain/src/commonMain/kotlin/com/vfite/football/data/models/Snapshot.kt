package com.vfite.football.data.models

import kotlinx.datetime.Instant

/**
 * A stored feed (News or Today's Matches): the items of the latest successful Refresh and when it was fetched.
 * [fetchedAt] is null when the feed was never fetched (for Matches: not fetched today); then [items] is empty.
 * A fetched feed can still have no items ("nothing today").
 */
data class Snapshot<T>(
    val items: List<T>,
    val fetchedAt: Instant?
) {
    val isFetched: Boolean
        get() = fetchedAt != null

    companion object {
        fun <T> notFetched(): Snapshot<T> = Snapshot(items = emptyList(), fetchedAt = null)
    }
}

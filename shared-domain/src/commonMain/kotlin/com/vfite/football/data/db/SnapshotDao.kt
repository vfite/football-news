package com.vfite.football.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
abstract class SnapshotDao {

    @Query("SELECT * FROM feed_info WHERE feed = :feed")
    abstract suspend fun feedInfo(feed: Feed): FeedInfoEntity?

    @Query("SELECT * FROM news_items ORDER BY position")
    abstract suspend fun newsItems(): List<NewsItemEntity>

    @Query("SELECT * FROM match_items WHERE date = :date ORDER BY position")
    abstract suspend fun matchItems(date: String): List<MatchItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun putFeedInfo(info: FeedInfoEntity)

    @Insert
    abstract suspend fun insertNewsItems(items: List<NewsItemEntity>)

    @Insert
    abstract suspend fun insertMatchItems(items: List<MatchItemEntity>)

    @Query("DELETE FROM news_items")
    abstract suspend fun deleteNewsItems()

    @Query("DELETE FROM match_items")
    abstract suspend fun deleteMatchItems()

    /** Reads the info row and items together so a concurrent Refresh can't produce a mix. */
    @Transaction
    open suspend fun loadNews(): Pair<FeedInfoEntity, List<NewsItemEntity>>? {
        val info = feedInfo(Feed.NEWS) ?: return null
        return info to newsItems()
    }

    /**
     * The Matches info row and the matches played on [date], or null when matches were never
     * fetched for [date]. The info row's date keeps "fetched today, no games" (empty list)
     * apart from "not fetched today" (null), which the widget turns into a Refresh.
     */
    @Transaction
    open suspend fun loadMatches(date: String): Pair<FeedInfoEntity, List<MatchItemEntity>>? {
        val info = feedInfo(Feed.MATCHES)?.takeIf { it.requestedDate == date } ?: return null
        return info to matchItems(date)
    }

    /** Replaces the whole News Snapshot atomically. */
    @Transaction
    open suspend fun replaceNews(info: FeedInfoEntity, items: List<NewsItemEntity>) {
        deleteNewsItems()
        insertNewsItems(items)
        putFeedInfo(info)
    }

    /** Replaces the whole Matches Snapshot atomically. */
    @Transaction
    open suspend fun replaceMatches(info: FeedInfoEntity, items: List<MatchItemEntity>) {
        deleteMatchItems()
        insertMatchItems(items)
        putFeedInfo(info)
    }
}

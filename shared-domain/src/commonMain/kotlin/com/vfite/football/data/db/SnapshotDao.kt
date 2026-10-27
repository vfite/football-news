package com.vfite.football.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.datetime.LocalDate

@Dao
abstract class SnapshotDao {

    @Query("SELECT * FROM feed_info WHERE feed = :feed")
    abstract suspend fun feedInfo(feed: Feed): FeedInfoEntity?

    @Query("SELECT * FROM news_items ORDER BY position")
    abstract suspend fun newsItems(): List<NewsItemEntity>

    @Query("SELECT * FROM match_items WHERE date = :date ORDER BY position")
    abstract suspend fun matchItems(date: LocalDate): List<MatchItemEntity>

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

    @Transaction
    open suspend fun loadNews(): Pair<FeedInfoEntity, List<NewsItemEntity>>? {
        val info = feedInfo(Feed.NEWS) ?: return null
        return info to newsItems()
    }

    @Transaction
    open suspend fun loadMatches(date: LocalDate): Pair<FeedInfoEntity, List<MatchItemEntity>>? {
        val info = feedInfo(Feed.MATCHES)?.takeIf { it.requestedDate == date } ?: return null
        return info to matchItems(date)
    }

    @Transaction
    open suspend fun replaceNews(info: FeedInfoEntity, items: List<NewsItemEntity>) {
        deleteNewsItems()
        insertNewsItems(items)
        putFeedInfo(info)
    }

    @Transaction
    open suspend fun replaceMatches(info: FeedInfoEntity, items: List<MatchItemEntity>) {
        deleteMatchItems()
        insertMatchItems(items)
        putFeedInfo(info)
    }
}

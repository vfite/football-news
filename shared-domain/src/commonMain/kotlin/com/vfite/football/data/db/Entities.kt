package com.vfite.football.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * One row per news item of the current News Snapshot. Rows get a generated key and
 * keep the API order in [position], so duplicate ids in a response can't fail the insert.
 */
@Entity(tableName = "news_items")
data class NewsItemEntity(
    @PrimaryKey(autoGenerate = true) val rowId: Long = 0,
    val position: Int,
    val id: Long,
    val itemTitle: String,
    val imageWide: String,
    val imageSmall: String,
    val isImportant: Long,
    val isVideo: Long,
    val category: String,
    val postDate: String,
    val commentsCount: Long,
    val commentsCountTop: Long
)

/**
 * One row per match of the current Matches Snapshot; `gameId` is nullable in the API, so rows get a generated key.
 * Indexed by [date] because today's games are read with `WHERE date = :date`.
 */
@Entity(tableName = "match_items", indices = [Index("date")])
data class MatchItemEntity(
    @PrimaryKey(autoGenerate = true) val rowId: Long = 0,
    val position: Int,
    val gameId: Int?,
    val champId: Int?,
    val team1Id: Int?,
    val team2Id: Int?,
    val tourId: Int?,
    val tourName: String?,
    val team1Score: String,
    val team2Score: String,
    val team1PenaltyScore: String?,
    val team2PenaltyScore: String?,
    val isEnded: Int?,
    val statusId: String?,
    val date: String?,
    val time: String,
    val team1ImageUrl: String?,
    val team2ImageUrl: String?,
    val team1Name: String?,
    val team2Name: String?,
    val champName: String?
)

/** The feeds a Snapshot is stored for; Room stores the enum by name. */
enum class Feed {
    NEWS,
    MATCHES
}

/**
 * One row per feed. Its presence means the feed was fetched at least once, so
 * "never fetched" (no row) differs from "fetched but empty" (row, no items).
 */
@Entity(tableName = "feed_info")
data class FeedInfoEntity(
    @PrimaryKey val feed: Feed,
    val fetchedAtMillis: Long,
    /** The date the matches were requested for; null for News. */
    val requestedDate: String?
)

package com.vfite.football.data.db

import com.vfite.football.data.models.MatchItem
import com.vfite.football.data.models.NewsItem

internal fun NewsItem.toEntity(position: Int) = NewsItemEntity(
    position = position,
    id = id,
    itemTitle = itemTitle,
    imageWide = imageWide,
    imageSmall = imageSmall,
    isImportant = isImportant,
    isVideo = isVideo,
    category = category,
    postDate = postDate,
    commentsCount = commentsCount,
    commentsCountTop = commentsCountTop
)

internal fun NewsItemEntity.toModel() = NewsItem(
    id = id,
    itemTitle = itemTitle,
    imageWide = imageWide,
    imageSmall = imageSmall,
    isImportant = isImportant,
    isVideo = isVideo,
    category = category,
    postDate = postDate,
    commentsCount = commentsCount,
    commentsCountTop = commentsCountTop
)

internal fun MatchItem.toEntity(position: Int) = MatchItemEntity(
    position = position,
    gameId = gameId,
    champId = champId,
    team1Id = team1Id,
    team2Id = team2Id,
    tourId = tourId,
    tourName = tourName,
    team1Score = team1Score,
    team2Score = team2Score,
    team1PenaltyScore = team1PenaltyScore,
    team2PenaltyScore = team2PenaltyScore,
    isEnded = isEnded,
    statusId = statusId,
    date = date,
    time = time,
    team1ImageUrl = team1ImageUrl,
    team2ImageUrl = team2ImageUrl,
    team1Name = team1Name,
    team2Name = team2Name,
    champName = champName
)

internal fun MatchItemEntity.toModel() = MatchItem(
    gameId = gameId,
    champId = champId,
    team1Id = team1Id,
    team2Id = team2Id,
    tourId = tourId,
    tourName = tourName,
    team1Score = team1Score,
    team2Score = team2Score,
    team1PenaltyScore = team1PenaltyScore,
    team2PenaltyScore = team2PenaltyScore,
    isEnded = isEnded,
    statusId = statusId,
    date = date,
    time = time,
    team1ImageUrl = team1ImageUrl,
    team2ImageUrl = team2ImageUrl,
    team1Name = team1Name,
    team2Name = team2Name,
    champName = champName
)

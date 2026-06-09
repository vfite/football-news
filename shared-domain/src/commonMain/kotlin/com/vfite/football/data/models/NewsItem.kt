package com.vfite.football.data.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class NewsItem(
    val id: Long,
    val itemTitle: String,
    val imageWide: String,
    val imageSmall: String,
    val isImportant: Long,
    val isVideo: Long,
    val category: String,
    val postDate: String,
    val commentsCount: Long,
    val commentsCountTop: Long,
    @Transient
    var isVideoLoaded: Boolean = false
)

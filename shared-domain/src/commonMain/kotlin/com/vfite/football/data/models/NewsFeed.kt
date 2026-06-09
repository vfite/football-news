package com.vfite.football.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewsData(
    val data: NewsFeed
)

@Serializable
data class NewsFeed(
    val pages: Pages,
    val dateFrom: String? = null,
    val dateTo: String? = null,
    val teams: List<String>,

    @SerialName("items")
    var items: List<NewsItem>
)

@Serializable
data class Pages(
    val currentPage: Long,
    val totalPages: Long,
    val limit: Long
)

fun empty(): NewsFeed {
    return NewsFeed(
        pages = Pages(0, 0, 0),
        items = emptyList(),
        teams = emptyList()
    )
}

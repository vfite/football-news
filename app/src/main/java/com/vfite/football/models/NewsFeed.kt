package com.vfite.football.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class NewsData(
    val data: NewsFeed
)

@JsonClass(generateAdapter = true)
data class NewsFeed(
    val pages: Pages,
    val dateFrom: String? = null,
    val dateTo: String? = null,
    val teams: List<String>,

    @Json(name ="items")
    var items: List<NewsItem>
)

data class Pages(
    val currentPage: Long,
    val totalPages: Long,
    val limit: Long
)

fun empty(): NewsFeed {
    return NewsFeed(
        pages = Pages(0,0,0),
        items = emptyList(),
        teams = emptyList()
    )
}
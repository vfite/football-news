package com.vfite.football.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class MatchesData(
    val data: MatchesFeed
)

@JsonClass(generateAdapter = true)
data class MatchesFeed(
    val date: String? = null,

    @Json(name ="items")
    var items: List<MatchItem>
)
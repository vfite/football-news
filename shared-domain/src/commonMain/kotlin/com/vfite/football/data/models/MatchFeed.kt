package com.vfite.football.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MatchesData(
    val data: MatchesFeed
)

@Serializable
data class MatchesFeed(
    val date: String? = null,

    @SerialName("items")
    var items: List<MatchItem>
)

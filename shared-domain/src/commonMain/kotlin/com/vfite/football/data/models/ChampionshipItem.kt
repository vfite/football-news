package com.vfite.football.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChampionshipItem(
    val champId: Int,
    val champName: String,
    val champImageUrl: String,
    val countryId: Int,
    val countryName: String,

    @SerialName("items")
    var items: List<MatchItem>
)

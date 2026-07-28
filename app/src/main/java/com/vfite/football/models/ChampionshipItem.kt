package com.vfite.football.models

import com.squareup.moshi.Json

data class ChampionshipItem(
    val champId: Int,
    val champName: String,
    val champImageUrl: String,
    val countryId: Int,
    val countryName: String,

    @Json(name ="items")
    var items: List<MatchItem>
)
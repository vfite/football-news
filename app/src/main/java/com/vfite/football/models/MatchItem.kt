package com.vfite.football.models

data class MatchItem(
    var gameId: Int? = null,
    var champId: Int? = null,
    var team1Id: Int? = null,
    var team2Id: Int? = null,
    var tourId: Int? = null,
    var tourName: String? = null,
    var team1Score: String,
    var team2Score: String,
    var team1PenaltyScore: String? = null,
    var team2PenaltyScore: String? = null,
    var isEnded: Int? = null,
    var statusId: String? = null,
    var date: String? = null,
    var time: String,
    var team1ImageUrl: String? = null,
    var team2ImageUrl: String? = null,
    var team1Name: String? = null,
    var team2Name: String? = null,
    var champName: String? = null
) {
    val score = team1Score.takeIf { teamScore -> !teamScore.contains('-') }
        ?.let {
            "${team1Score} : ${team2Score}"
        } ?: run {
        time
    }

    val isInProgress : Boolean = !team1Score.contains('-') && isEnded == 0
}
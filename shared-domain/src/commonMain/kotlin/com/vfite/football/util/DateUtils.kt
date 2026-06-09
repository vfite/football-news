package com.vfite.football.util

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

fun getCurrentDate(): String {
    return Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
}

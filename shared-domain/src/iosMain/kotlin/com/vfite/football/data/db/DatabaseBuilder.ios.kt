package com.vfite.football.data.db

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSFileManager

fun createDatabaseBuilder(appGroupId: String): RoomDatabase.Builder<FootballDatabase> {
    val containerPath = NSFileManager.defaultManager
        .containerURLForSecurityApplicationGroupIdentifier(appGroupId)
        ?.path
    checkNotNull(containerPath) { "No App Group container for '$appGroupId'; check the target's entitlements" }
    return Room.databaseBuilder<FootballDatabase>(name = "$containerPath/$DATABASE_NAME")
}

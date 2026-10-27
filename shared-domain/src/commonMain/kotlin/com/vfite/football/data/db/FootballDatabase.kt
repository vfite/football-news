package com.vfite.football.data.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters

@Database(
    entities = [NewsItemEntity::class, MatchItemEntity::class, FeedInfoEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(LocalDateConverter::class)
@ConstructedBy(FootballDatabaseConstructor::class)
abstract class FootballDatabase : RoomDatabase() {
    abstract fun snapshotDao(): SnapshotDao
}

// Room's KSP processor generates the actual implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object FootballDatabaseConstructor : RoomDatabaseConstructor<FootballDatabase> {
    override fun initialize(): FootballDatabase
}

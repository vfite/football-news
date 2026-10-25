package com.vfite.football.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

/** Android's Room builder for the shared database; pass it to `databaseModule(builder)`. */
fun createDatabaseBuilder(context: Context): RoomDatabase.Builder<FootballDatabase> {
    val appContext = context.applicationContext
    return Room.databaseBuilder<FootballDatabase>(
        context = appContext,
        name = appContext.getDatabasePath(DATABASE_NAME).absolutePath
    )
}

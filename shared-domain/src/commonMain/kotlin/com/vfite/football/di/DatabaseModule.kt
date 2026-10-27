package com.vfite.football.di

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.vfite.football.data.LatestNewsDataSource
import com.vfite.football.data.MatchesDataSource
import com.vfite.football.data.RoomLatestNewsDataSource
import com.vfite.football.data.RoomMatchesDataSource
import com.vfite.football.data.db.FootballDatabase
import com.vfite.football.data.db.SnapshotDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

fun databaseModule(builder: RoomDatabase.Builder<FootballDatabase>) = DI.Module("DatabaseModule") {

    bind<FootballDatabase>() with singleton {
        builder
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            // The cache is fully re-fetchable: a schema change just starts with an empty cache.
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    bind<SnapshotDao>() with singleton {
        instance<FootballDatabase>().snapshotDao()
    }

    bind<LatestNewsDataSource>() with singleton {
        RoomLatestNewsDataSource(dao = instance())
    }

    bind<MatchesDataSource>() with singleton {
        RoomMatchesDataSource(dao = instance())
    }
}

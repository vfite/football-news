package com.vfite.football

import android.app.Application
import com.vfite.football.data.db.createDatabaseBuilder
import com.vfite.football.di.apiModule
import com.vfite.football.di.databaseModule
import com.vfite.football.di.networkModule
import com.vfite.football.di.domainModule
import com.vfite.football.di.sharedDomainModule
import com.vfite.football.di.systemModule
import org.kodein.di.DI
import org.kodein.di.conf.global
import timber.log.Timber

class FootballApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        setupKodein()
    }

    private fun setupKodein() {
        with(DI.global) {
            addImport(networkModule(
                context = this@FootballApplication,
                newsUrl = BuildConfig.NEWS_URL
                )
            )
            addImport(apiModule(baseUrl = BuildConfig.API_URL))
            addImport(systemModule(this@FootballApplication))
            addImport(domainModule(this@FootballApplication))
            addImport(databaseModule(createDatabaseBuilder(this@FootballApplication)))
            addImport(sharedDomainModule())
        }
    }
}
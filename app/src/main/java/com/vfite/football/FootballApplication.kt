package com.vfite.football

import android.app.Application
import com.vfite.football.di.networkModule
import com.vfite.football.di.domainModule
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
                apiUrl = BuildConfig.API_URL,
                newsUrl = BuildConfig.NEWS_URL
                )
            )
            addImport(domainModule(this@FootballApplication))
            addImport(systemModule(this@FootballApplication))
        }
    }
}
package com.vfite.football.di

import android.content.Context
import com.vfite.football.widget.ui.LatestNewsViewModel
import com.vfite.football.widget.domain.LoadLatestNewsUseCase
import com.vfite.football.widget.data.LatestNewsDataSource
import com.vfite.football.widget.data.MatchesDataSource
import com.vfite.football.widget.data.RealLatestNewsDataSource
import com.vfite.football.widget.data.RealMatchesDataSource
import com.vfite.football.widget.data.RemoteSyncService
import com.vfite.football.widget.data.SyncService
import com.vfite.football.widget.domain.LoadDayMatchesUseCase
import com.vfite.football.widget.domain.RefreshDataUseCase
import com.vfite.football.widget.domain.RefreshMatchesUseCase
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider
import org.kodein.di.singleton

fun domainModule(context: Context) = DI.Module("DomainModule") {

    bind<SyncService>() with singleton{
        RemoteSyncService(
            context = context,
            widgetService = instance()
        )
    }

    bind<LatestNewsDataSource>() with singleton {
        RealLatestNewsDataSource()
    }

    bind<MatchesDataSource>() with singleton {
        RealMatchesDataSource()
    }

    bind<RefreshDataUseCase>() with singleton {
        RefreshDataUseCase(
            apiInterface = instance(),
            latestNewsDataSource = instance(),
            matchesDataSource = instance(),
            widgetService = instance()
        )
    }

    bind<RefreshMatchesUseCase>() with singleton {
        RefreshMatchesUseCase(
            apiInterface = instance(),
            matchesDataSource = instance(),
            widgetService = instance()
        )
    }

    bind<LoadLatestNewsUseCase>() with provider{
        LoadLatestNewsUseCase(
            newsDataSource = instance(),
            syncService = instance()
        )
    }

    bind<LoadDayMatchesUseCase>() with provider{
        LoadDayMatchesUseCase(
            matchesDataSource = instance(),
            syncService = instance()
        )
    }

    bind<LatestNewsViewModel>() with singleton {
        LatestNewsViewModel(
            syncService = instance(),
            loadLatestNewsUseCase = instance(),
            loadDayMatchesUseCase = instance()
        )
    }
}

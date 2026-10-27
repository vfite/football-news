package com.vfite.football.di

import com.vfite.football.data.LatestNewsDataSource
import com.vfite.football.data.MatchesDataSource
import com.vfite.football.data.RealLatestNewsDataSource
import com.vfite.football.data.RealMatchesDataSource
import com.vfite.football.domain.LoadDayMatchesUseCase
import com.vfite.football.domain.LoadLatestNewsUseCase
import com.vfite.football.domain.RefreshDataUseCase
import com.vfite.football.domain.RefreshMatchesUseCase
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider
import org.kodein.di.singleton

/**
 * Binds the platform-agnostic data sources and use cases from `:shared-domain`.
 * `ApiRepository`, `SyncService` and `IWidgetService` implementations are Android-specific
 * and must be bound elsewhere (e.g. app's DI modules) before importing this module.
 */
fun sharedDomainModule() = DI.Module("SharedDomainModule") {

    bind<LatestNewsDataSource>() with singleton {
        RealLatestNewsDataSource()
    }

    bind<MatchesDataSource>() with singleton {
        RealMatchesDataSource()
    }

    bind<RefreshDataUseCase>() with singleton {
        RefreshDataUseCase(
            apiRepository = instance(),
            latestNewsDataSource = instance(),
            matchesDataSource = instance(),
            widgetService = instance()
        )
    }

    bind<RefreshMatchesUseCase>() with singleton {
        RefreshMatchesUseCase(
            apiRepository = instance(),
            matchesDataSource = instance(),
            widgetService = instance()
        )
    }

    bind<LoadLatestNewsUseCase>() with provider {
        LoadLatestNewsUseCase(
            newsDataSource = instance(),
            syncService = instance()
        )
    }

    bind<LoadDayMatchesUseCase>() with provider {
        LoadDayMatchesUseCase(
            matchesDataSource = instance(),
            syncService = instance()
        )
    }
}

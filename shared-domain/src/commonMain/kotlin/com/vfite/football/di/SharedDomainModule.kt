package com.vfite.football.di

import com.vfite.football.data.DefaultMatchesRepository
import com.vfite.football.data.DefaultNewsRepository
import com.vfite.football.data.MatchesRepository
import com.vfite.football.data.NewsRepository
import com.vfite.football.domain.RefreshDataUseCase
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

fun sharedDomainModule() = DI.Module("SharedDomainModule") {

    bind<NewsRepository>() with singleton {
        DefaultNewsRepository(
            apiRepository = instance(),
            newsDataSource = instance()
        )
    }

    bind<MatchesRepository>() with singleton {
        DefaultMatchesRepository(
            apiRepository = instance(),
            matchesDataSource = instance()
        )
    }

    bind<RefreshDataUseCase>() with singleton {
        RefreshDataUseCase(
            newsRepository = instance(),
            matchesRepository = instance()
        )
    }
}

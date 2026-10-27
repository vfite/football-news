package com.vfite.football.di

import android.content.Context
import com.vfite.football.widget.ui.LatestNewsViewModel
import com.vfite.football.widget.data.RemoteSyncService
import com.vfite.football.domain.SyncService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

// Data sources (LatestNewsDataSource, MatchesDataSource), repositories
// (NewsRepository, MatchesRepository) and RefreshDataUseCase are bound in
// :shared-domain's sharedDomainModule().
// ApiRepository is bound in :shared-domain's apiModule().
const val APPLICATION_SCOPE_TAG = "APPLICATION_SCOPE_TAG"

fun domainModule(context: Context) = DI.Module("DomainModule") {

    // Lives as long as the process; used for work that must outlive a single broadcast.
    bind<CoroutineScope>(APPLICATION_SCOPE_TAG) with singleton {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    bind<SyncService>() with singleton {
        RemoteSyncService(
            context = context,
            widgetService = instance()
        )
    }

    bind<LatestNewsViewModel>() with singleton {
        LatestNewsViewModel(
            syncService = instance(),
            newsRepository = instance(),
            matchesRepository = instance(),
            applicationScope = instance(APPLICATION_SCOPE_TAG)
        )
    }
}

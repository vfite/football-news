package com.vfite.football.di

import android.content.Context
import com.vfite.football.widget.ui.LatestNewsViewModel
import com.vfite.football.widget.data.RemoteSyncService
import com.vfite.football.domain.SyncService
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

// Data sources (LatestNewsDataSource, MatchesDataSource) and use cases
// (LoadLatestNewsUseCase, LoadDayMatchesUseCase, RefreshDataUseCase,
// RefreshMatchesUseCase) are bound in :shared-domain's sharedDomainModule().
// ApiRepository is bound in :shared-domain's apiModule().
fun domainModule(context: Context) = DI.Module("DomainModule") {

    bind<SyncService>() with singleton {
        RemoteSyncService(
            context = context,
            widgetService = instance()
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

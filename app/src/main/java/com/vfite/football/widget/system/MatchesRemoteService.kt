package com.vfite.football.widget.system

import android.content.Intent
import android.widget.RemoteViewsService
import com.vfite.football.widget.ui.LatestNewsViewModel
import org.kodein.di.DI
import org.kodein.di.conf.global
import org.kodein.di.direct
import org.kodein.di.instance

class MatchesRemoteService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {

        val latestNewsViewModel: LatestNewsViewModel = DI.global.direct.instance()
        return MatchesProviderFactory(
            this.applicationContext,
            intent,
            latestNewsViewModel
        )
    }
}
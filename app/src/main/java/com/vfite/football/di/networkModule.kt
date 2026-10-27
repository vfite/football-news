package com.vfite.football.di

import android.content.Context
import com.vfite.football.widget.WidgetMode
import com.vfite.football.widget.renderer.IRemoteViewRenderer
import com.vfite.football.widget.renderer.LatestNewsRenderer
import com.vfite.football.widget.renderer.MatchItemRenderer
import com.vfite.football.widget.renderer.MatchDayRenderer
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

const val NEWS_URL_TAG = "NEWS_URL_TAG"

// ApiInterface/Retrofit/ApiRepository bindings have moved to
// :shared-domain's apiModule(baseUrl). This module only wires
// Android-view-specific renderer bindings.
fun networkModule(
    context: Context,
    newsUrl: String
) = DI.Module("BaseNetwork") {

    bind<String>(NEWS_URL_TAG) with provider {
        newsUrl
    }

    bind<IRemoteViewRenderer>(WidgetMode.NEWS) with provider {
        LatestNewsRenderer(context, instance(), instance(), newsUrl)
    }

    bind<IRemoteViewRenderer>(WidgetMode.MACTHES) with provider {
        MatchDayRenderer(context, instance())
    }

    bind<MatchItemRenderer>() with provider {
        MatchItemRenderer(context)
    }
}
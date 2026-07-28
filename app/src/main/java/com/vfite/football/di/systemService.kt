package com.vfite.football.di

import android.content.Context
import com.vfite.football.widget.system.FootballWidgetProvider
import com.vfite.football.widget.IWidgetService
import com.vfite.football.widget.system.SystemWidgetService
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

fun systemModule(context: Context) = DI.Module("systemModule") {

    bind<IWidgetService>() with singleton {
        SystemWidgetService(
            context = context,
            widgetClass = FootballWidgetProvider::class.java
        )
    }
}

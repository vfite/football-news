package com.vfite.football.widget.renderer

import android.widget.RemoteViews

fun interface IRemoteViewRenderer {

    fun render(widgetId: Int): RemoteViews
}
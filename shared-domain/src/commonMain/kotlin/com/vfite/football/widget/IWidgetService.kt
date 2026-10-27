package com.vfite.football.widget

enum class WidgetMode {
    NEWS,
    MACTHES
}

interface IWidgetService {

    companion object {
        const val WIDGET_MODE = "WidgetMode"
    }

    fun toggleWidget(mode: WidgetMode)
    fun invalidateWidget()
}

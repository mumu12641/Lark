package io.github.mumu12641.lark.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import io.github.mumu12641.lark.R

class LarkWidgetProvider : AppWidgetProvider() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        appWidgetIds?.forEach { appWidgetId ->
            val views: RemoteViews = RemoteViews(
                context?.packageName,
                R.layout.lark_widget
            ).apply {

            }
            appWidgetManager?.updateAppWidget(appWidgetId, views)
        }
    }
}
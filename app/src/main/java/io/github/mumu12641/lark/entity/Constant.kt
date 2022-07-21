package io.github.mumu12641.lark.entity

import io.github.mumu12641.lark.BaseApplication.Companion.context
import io.github.mumu12641.lark.R

object Route{
    val ROUTE_HOME = context.getString(R.string.app_name)
    val ROUTE_LOCAL = context.getString(R.string.local_text)
    val ROUTE_HISTORY = context.getString(R.string.history_text)
    val ROUTE_DOWNLOAD = context.getString(R.string.download_text)
    val ROUTE_CLOUD = context.getString(R.string.cloud_text)
}

object Load{
    const val LOADING = 0
    const val SUCCESS = 1
    const val ERROR = 2
    const val NONE = 3
}

const val LocalSongListId = 1L
const val LikeSongListId = 2L
const val HistorySongListId = 3L
package io.github.mumu12641.lark.entity

import io.github.mumu12641.lark.BaseApplication.Companion.context
import io.github.mumu12641.lark.R

object Route {
    val ROUTE_HOME = context.getString(R.string.app_name)
    val ROUTE_LOCAL = context.getString(R.string.local_text)
    val ROUTE_HISTORY = context.getString(R.string.history_text)
    val ROUTE_DOWNLOAD = context.getString(R.string.download_text)
    val ROUTE_CLOUD = context.getString(R.string.cloud_text)
    val ROUTE_USER = context.getString(R.string.user_message_text)
    val ROUTE_SONG_LIST_DETAILS = context.getString(R.string.song_list_details_text)
    val ROUTE_PLAY_PAGE = "Play Page"
}

object Load {
    const val LOADING = 0
    const val SUCCESS = 1
    const val ERROR = 2
    const val NONE = 3
}

const val LocalSongListId = 1L
const val LikeSongListId = 2L
const val HistorySongListId = 3L


const val CHANGE_PLAY_LIST = "CHANGE_PLAY_LIST"
const val CHANGE_PLAT_LIST_SHUFFLE = -1L

const val ACTION_PAUSE = "PAUSE"
const val ACTION_NEXT = "NEXT"
const val ACTION_PREVIOUS = "PREVIOUS"
const val ACTION_PLAY = "PLAY"

val INIT_SONG = Song(
    0L, "最伟大的作品", "周杰伦", "11", "11", 100
)
val INIT_SONG_LIST = SongList(
    0L,
    "12",
    "12",
    0,
    "12",
    "12",
    2
)
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
    const val ROUTE_PLAY_PAGE = "Play Page"
    val ROUTE_ARTIST_PAGE = context.getString(R.string.singer_text)
    const val ROUTE_ARTIST_DETAIL_PAGE = "Artist detail"
    val ROUTE_SETTING = context.getString(R.string.setting_text)
    val ROUTE_DISPLAY = context.getString(R.string.display_text)
    val ROUTE_ABOUT = context.getString(R.string.about_text)
    val ROUTE_SEARCH = context.getString(R.string.search_text)
    val ROUTE_SUGGESTION = context.getString(R.string.suggestion_text)
}

object Load {
    const val LOADING = 0
    const val SUCCESS = 1
    const val ERROR = 2
    const val NONE = 3
}

sealed class LoadState(val msg: String) {
    class Loading(msg: String = "") : LoadState(msg)
    class Success(msg: String = "") : LoadState(msg)
    class Fail(msg: String) : LoadState(msg)
    class None(msg: String = "") : LoadState(msg)
}

const val LocalSongListId = 1L
const val LikeSongListId = 2L
const val HistorySongListId = 3L

// PlaybackService Custom Command
const val CHANGE_PLAY_LIST = "CHANGE_PLAY_LIST"
const val ADD_SONG_TO_LIST = "ADD_SONG_TO_LIST"
const val SEEK_TO_SONG = "SEEK_TO_SONG"
const val CHANGE_PLAT_LIST_SHUFFLE = -1L

const val ACTION_PAUSE = "PAUSE"
const val ACTION_NEXT = "NEXT"
const val ACTION_PREVIOUS = "PREVIOUS"
const val ACTION_PLAY = "PLAY"

// song buffer
const val NOT_NEED_BUFFER = 0
const val NEED_BUFFER = 1
const val NOT_BUFFERED = 1
const val BUFFERED = 2
const val EMPTY_URI = "not buffered yet"

const val PREFILL_SONGLIST_TYPE = 1
const val CREATE_SONGLIST_TYPE = 2
const val ARTIST_SONGLIST_TYPE = 3

val INIT_SONG = Song(0L, "最伟大的作品", "周杰伦", "11", "11", 100)
val EMPTY_SONG = Song(0L, "empty", "empty", "", "", 0)
val INIT_SONG_LIST = SongList(0L, "", "", 0, "", "", 2)

// tabRow
val NEXT_TO_PLAY_PAGE = 0
val LYRICS_PAGE = 1
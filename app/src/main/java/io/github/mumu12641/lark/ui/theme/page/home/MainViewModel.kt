package io.github.mumu12641.lark.ui.theme.page.home

import android.content.ComponentName
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mumu12641.lark.BaseApplication.Companion.context
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.ARTIST_SONGLIST_TYPE
import io.github.mumu12641.lark.entity.CHANGE_PLAY_LIST
import io.github.mumu12641.lark.entity.PlaylistSongCrossRef
import io.github.mumu12641.lark.entity.SongList
import io.github.mumu12641.lark.room.DataBaseUtils
import io.github.mumu12641.lark.service.MediaPlaybackService
import io.github.mumu12641.lark.service.MediaServiceConnection
import io.github.mumu12641.lark.service.MediaServiceConnection.Companion.EMPTY_PLAYBACK_STATE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    val currentPlayMetadata by lazy { mediaServiceConnection.playMetadata }

    val currentPlayState by lazy { mediaServiceConnection.playState }

    val currentPlaySongs by lazy { mediaServiceConnection.playList }

    val currentSongList by lazy { mediaServiceConnection.currentSongList }

    val allSongList = DataBaseUtils.queryAllSongList().map {
        it.filter { songList ->
            songList.type in 1 until ARTIST_SONGLIST_TYPE
        }
    }
    val artistSongList = DataBaseUtils.queryAllSongList().map {
        it.filter {  songList ->
            songList.type == ARTIST_SONGLIST_TYPE
        }
    }

    fun addSongList(songList: SongList) {
        viewModelScope.launch(Dispatchers.IO) {
            DataBaseUtils.insertSongList(songList)
        }
    }

    private fun checkPlayState(): Boolean = currentPlayState.value != EMPTY_PLAYBACK_STATE

    fun onPlay() {
        if (checkPlayState()) {
            mediaServiceConnection.transportControls.play()
        }
    }

    fun onPause() {
        if (checkPlayState()) {
            mediaServiceConnection.transportControls.pause()
        }
    }

    fun onSkipToNext() {
        if (checkPlayState()) {
            mediaServiceConnection.transportControls.skipToNext()
        }
    }

    fun onSkipToPrevious() {
        if (checkPlayState()) {
            mediaServiceConnection.transportControls.skipToPrevious()
        }
    }

    fun onSeekTo(position: Long) {
        if (checkPlayState()) {
            mediaServiceConnection.transportControls.seekTo(position)
        }
    }
    init {
        refreshArtist()
    }
    fun refreshArtist() {
        viewModelScope.launch(Dispatchers.IO) {
            val songs = DataBaseUtils.queryAllSong()
            for (i in songs) {
                if (DataBaseUtils.isSongListExist(i.songSinger, ARTIST_SONGLIST_TYPE) ) {
                    val songListId = DataBaseUtils.querySongListId(
                        i.songSinger,
                        ARTIST_SONGLIST_TYPE
                    )
                    if (!DataBaseUtils.isRefExist(songListId,i.songId)) {
                        DataBaseUtils.insertRef(
                            PlaylistSongCrossRef(
                                songListId, i.songId
                            )
                        )
                    }
                } else {
                    DataBaseUtils.insertSongList(
                        SongList(
                            0L, i.songSinger, "xxx", 0, context.getString(
                                R.string.no_description_text
                            ), "111", ARTIST_SONGLIST_TYPE
                        )
                    )
                }
            }
        }
    }


    companion object {
        val mediaServiceConnection: MediaServiceConnection = MediaServiceConnection.getInstance(
            context,
            ComponentName(context, MediaPlaybackService::class.java)
        )

        fun playMedia(songListId: Long, songId: Long) {
            Log.d("TAG", "playMedia: $songListId + $songId")
            val bundle = Bundle()
            bundle.apply {
                putLong("songListId", songListId)
                putLong("songId", songId)
            }
            mediaServiceConnection.transportControls.sendCustomAction(CHANGE_PLAY_LIST, bundle)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCleared() {
        super.onCleared()
        Log.d("TAG", "onCleared: ")
        mediaServiceConnection.disConnected()
    }
}
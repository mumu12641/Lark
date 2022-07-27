package io.github.mumu12641.lark.ui.theme.page.home

import android.content.ComponentName
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.mumu12641.lark.MainActivity.Companion.context
import io.github.mumu12641.lark.entity.CHANGE_PLAY_LIST
import io.github.mumu12641.lark.entity.SongList
import io.github.mumu12641.lark.room.DataBaseUtils
import io.github.mumu12641.lark.service.MediaPlaybackService
import io.github.mumu12641.lark.service.MediaServiceConnection
import io.github.mumu12641.lark.service.MediaServiceConnection.Companion.EMPTY_PLAYBACK_STATE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val currentPlayMetadata by lazy { mediaServiceConnection.playMetadata }

    val currentPlayState by lazy { mediaServiceConnection.playState }

//    private val mediaServiceConnection: MediaServiceConnection = MediaServiceConnection.getInstance(
//        context,
//        ComponentName(context, MediaPlaybackService::class.java)
//    )

    val allSongList = DataBaseUtils.queryAllSongList().map {
        it.filter { songList ->
            songList.type > 0
        }
    }

    fun addSongList(songList: SongList) {
        viewModelScope.launch(Dispatchers.IO) {
            DataBaseUtils.insertSongList(songList)
        }
    }

    private fun checkPlayState():Boolean = currentPlayState.value != EMPTY_PLAYBACK_STATE

    fun onPlay(){
        if (checkPlayState()){
            mediaServiceConnection.transportControls.play()
        }
    }
    fun onPause(){
        if (checkPlayState()){
            mediaServiceConnection.transportControls.pause()
        }
    }
    fun onSkipToNext(){
        if (checkPlayState()){
            mediaServiceConnection.transportControls.skipToNext()
        }
    }
    fun onSkipToPrevious(){
        if (checkPlayState()){
            mediaServiceConnection.transportControls.skipToPrevious()
        }
    }


    companion object{
        private val mediaServiceConnection: MediaServiceConnection = MediaServiceConnection.getInstance(
            context,
            ComponentName(context, MediaPlaybackService::class.java)
        )

        fun playMedia(songListId:Long,songId:Long) {
            Log.d("TAG", "playMedia: $songListId + $songId")
            val bundle = Bundle()
            bundle.apply {
                putLong("songListId", songListId)
                putLong("songId", songId)
            }
            mediaServiceConnection.transportControls.sendCustomAction(CHANGE_PLAY_LIST, bundle)
        }
    }

//    fun playMedia(songListId:Long,songId:Long) {
//
//        Log.d("TAG", "playMedia: $songListId + $songId")
//
//        val bundle = Bundle()
//        bundle.apply {
//            putLong("songListId", songListId)
//            putLong("songId", songId)
//        }
//
//        mediaServiceConnection.transportControls.sendCustomAction(CHANGE_PLAY_LIST, bundle)
//    }
}
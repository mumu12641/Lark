package io.github.mumu12641.lark.ui.theme.page.home

import android.app.Activity
import android.content.ComponentName
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.mumu12641.lark.MainActivity.Companion.context
import io.github.mumu12641.lark.entity.SongList
import io.github.mumu12641.lark.room.DataBaseUtils
import io.github.mumu12641.lark.service.MediaPlaybackService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainViewModel:ViewModel() {

    val allSongList = DataBaseUtils.queryAllSongList().map { it ->
        it.filter {
            songList -> songList .type > 0
        }
    }

    fun addSongList(songList: SongList){
        viewModelScope.launch (Dispatchers.IO) {
            DataBaseUtils.insertSongList(songList)
        }
    }

    fun playMedia(){
        if (mediaController.playbackState.state == PlaybackStateCompat.STATE_PLAYING){
            mediaController.transportControls.pause()
        }else {
            mediaController.transportControls.play()
        }
    }

    private lateinit var mediaBrowser:MediaBrowserCompat
    private var connectionCallbacks:MediaBrowserCompat.ConnectionCallback
    private lateinit var mediaController:MediaControllerCompat
    private var controllerCallback = object : MediaControllerCompat.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {

        }
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {

        }
    }

    init {
        connectionCallbacks = object : MediaBrowserCompat.ConnectionCallback() {
            override fun onConnected() {
                Log.d("TAG", "onConnected ")
                mediaBrowser.sessionToken.also { token ->
                    mediaController = MediaControllerCompat(
                        context,
                        token
                    )
                    MediaControllerCompat.setMediaController(context as Activity, mediaController)
                    mediaController.registerCallback(controllerCallback)
                }
            }

            override fun onConnectionSuspended() {

            }


            override fun onConnectionFailed() {

            }
        }
        mediaBrowser = MediaBrowserCompat(
            context,
            ComponentName(context,MediaPlaybackService::class.java),
            connectionCallbacks,
            null
        )
    }


}
package io.github.mumu12641.lark.service

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.tencent.mmkv.MMKV
import io.github.mumu12641.lark.entity.Song
import io.github.mumu12641.lark.room.DataBaseUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MediaServiceConnection(context: Context,componentName: ComponentName) {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected

    private val _playState = MutableStateFlow(EMPTY_PLAYBACK_STATE)
    val playState = _playState

    private val _playMetadata = MutableStateFlow(NOTHING_PLAYING)
    val playMetadata = _playMetadata

    // 播放列表
    private val _playList = MutableStateFlow(emptyList<Song>())
    val playList = _playList

    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    private lateinit var mediaController:MediaControllerCompat

    private val mediaBrowserConnectionCallback = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            Log.d("TAG", "onConnected")
            _isConnected.value = true
            mediaController = MediaControllerCompat(context,mediaBrowser.sessionToken).apply {
                registerCallback(controllerCallback)
            }
            mediaBrowser.unsubscribe(mediaBrowser.root)
            mediaBrowser.subscribe(mediaBrowser.root,mBrowserSubscriptionCallback)
        }
        override fun onConnectionSuspended() { _isConnected.value = false }
        override fun onConnectionFailed() { _isConnected.value = false }
    }

    private var controllerCallback = object : MediaControllerCompat.Callback() {

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            _playMetadata.value = metadata ?: NOTHING_PLAYING
            Log.d(TAG, "onMetadataChanged: "+metadata?.getString(MediaMetadataCompat.METADATA_KEY_TITLE))
        }
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            _playState.value = state ?: EMPTY_PLAYBACK_STATE
        }

        override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
            super.onQueueChanged(queue)
            scope.launch {
                _playList.value = queue?.map {
                    Log.d(TAG, "onQueueChanged: " + it.queueId)
                    DataBaseUtils.querySongById(it.queueId)
                } ?: emptyList()
                Log.d(TAG, "onQueueChanged: " + _playList.value.toString())
                transportControls.play()
            }

        }
    }

    private var mBrowserSubscriptionCallback : MediaBrowserCompat.SubscriptionCallback =
        object : MediaBrowserCompat.SubscriptionCallback() {
            override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
                super.onChildrenLoaded(parentId, children)
                Log.d("TAG", "onChildrenLoaded: $children")
            }
        }

//    private val initBundle:Bundle get() {
//        return Bundle().apply {
//            putLong("lastPlaySongList",MMKV.defaultMMKV().decodeLong("lastPlaySongList"))
//            putLong("lastPlaySong",MMKV.defaultMMKV().decodeLong("lastPlaySong"))
//        }
//    }

    private val mediaBrowser : MediaBrowserCompat = MediaBrowserCompat(
        context,
        componentName,
        mediaBrowserConnectionCallback,
        null
    ).apply {
        connect()
    }


    companion object {
        @Suppress("PropertyName")
        val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
            .build()

        @Suppress("PropertyName")
        val NOTHING_PLAYING: MediaMetadataCompat = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
            .build()
        @Volatile
        private var instance: MediaServiceConnection? = null

        fun getInstance(context: Context, serviceComponent: ComponentName) =
            instance ?: synchronized(this) {
                instance ?: MediaServiceConnection(context, serviceComponent)
                    .also { instance = it }
            }
    }

    private val TAG: String = "MediaServiceConnection"
}
package io.github.mumu12641.lark.service

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadata.METADATA_KEY_ARTIST
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import io.github.mumu12641.lark.BaseApplication.Companion.applicationScope
import io.github.mumu12641.lark.BaseApplication.Companion.context
import io.github.mumu12641.lark.BaseApplication.Companion.kv
import io.github.mumu12641.lark.MainActivity
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.*
import io.github.mumu12641.lark.room.DataBaseUtils
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.DEFAULT_SEED_COLOR
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.SEED_COLOR
import io.github.mumu12641.lark.widget.LarkWidgetProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

class MediaServiceConnection(context: Context, componentName: ComponentName) {

    private val job = Job()
    private val scope = CoroutineScope(job + Dispatchers.IO + CoroutineExceptionHandler { _, e ->
        Log.e(
            TAG,
            e.message.toString(),

            )
    })

    private val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected

    private val _playState = MutableStateFlow(EMPTY_PLAYBACK_STATE)
    val playState = _playState

    private val _playMetadata = MutableStateFlow(NOTHING_PLAYING)

    private val _currentPlaySong = MutableStateFlow(INIT_SONG)
    val currentPlaySong = _currentPlaySong

    private val _playList = MutableStateFlow(emptyList<Song>())
    val playList = _playList

    private val _currentSongList = MutableStateFlow(INIT_SONG_LIST)
    val currentSongList = _currentSongList

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition = _currentPosition


    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    private lateinit var mediaController: MediaControllerCompat

    private var bitmapPrevious: Bitmap? = null
    private var bitmapNext: Bitmap? = null


    private val mediaBrowserConnectionCallback = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            Log.d(TAG, "onConnected: ")
            _isConnected.value = true
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                registerCallback(controllerCallback)
            }
            mediaBrowser.unsubscribe(mediaBrowser.root)
            mediaBrowser.subscribe(mediaBrowser.root, mBrowserSubscriptionCallback)
            scope.launch {
                delay(500)
                updateProgress()
            }

        }

        override fun onConnectionSuspended() {
            _isConnected.value = false
        }

        override fun onConnectionFailed() {
            _isConnected.value = false
        }
    }


    private var controllerCallback = object : MediaControllerCompat.Callback() {

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            _playMetadata.value = metadata ?: NOTHING_PLAYING

            applicationScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, _ ->
                transportControls.sendCustomAction(SET_EMPTY, Bundle())
            }) {
                if (_playMetadata.value.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
                        .toLong() == -1L
                ) {
                    _currentPlaySong.value = BUFFER_SONG
                } else {
                    _currentPlaySong.value = DataBaseUtils.querySongById(
                        _playMetadata.value.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
                            .toLong()
                    )
                }
            }

            updateCurrentAlbumColor(metadata)
            updateWidgetMetadata(metadata)
            updateWidgetPlayState(mediaController.playbackState)
            applicationScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, _ -> }) {
                val id =
                    metadata?.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)?.toLong()
                val song = DataBaseUtils.querySongById(id!!)
                DataBaseUtils.querySongById(id)
                song.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        DataBaseUtils.updateSong(it.copy(recentPlay = Date()))
                        DataBaseUtils.insertRef(PlaylistSongCrossRef(HistorySongListId, id))

                    }
                }
            }

        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            _playState.value = state ?: EMPTY_PLAYBACK_STATE
            updateWidgetPlayState(state)
            updateWidgetMetadata(_playMetadata.value)
        }

        override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
            super.onQueueChanged(queue)
            applicationScope.launch {
                _playList.value = queue?.map {
                    DataBaseUtils.querySongById(it.queueId)
                } ?: emptyList()
                _currentSongList.value = DataBaseUtils.querySongListById(
                    kv.decodeLong("lastPlaySongList")
                )
                transportControls.play()
            }
        }
    }

    private fun updateCurrentAlbumColor(metadata: MediaMetadataCompat?) {
        applicationScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, _ -> }) {
            val id = metadata?.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)?.toLong()
            val song = DataBaseUtils.querySongById(id!!)
            val bitmap: Bitmap = Glide
                .with(MainActivity.context)
                .asBitmap()
                .load(song.songAlbumFileUri)
                .error(R.drawable.music_note)
                .submit()
                .get()
            Palette.from(bitmap).generate {
                it?.getDominantColor(
                    kv.decodeInt(
                        SEED_COLOR, DEFAULT_SEED_COLOR
                    )
                )?.let { it1 ->
                    PreferenceUtil.changeCurrentAlbumColor(
                        it1
                    )
                }
            }
        }
    }


    private var mBrowserSubscriptionCallback: MediaBrowserCompat.SubscriptionCallback =
        object : MediaBrowserCompat.SubscriptionCallback() {
            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>
            ) {
                super.onChildrenLoaded(parentId, children)
                applicationScope.launch {
                    if (kv
                            .decodeLong("lastPlaySongList") == 0L || kv
                            .decodeLong("lastPlaySong") == 0L
                    ) {
                        _playList.value = emptyList<Song>().toMutableList()
                        _currentSongList.value = INIT_SONG_LIST
                    } else {
                        _currentSongList.value = DataBaseUtils.querySongListById(
                            kv.decodeLong("lastPlaySongList")
                        )
                        _playList.value = DataBaseUtils.querySongListWithSongsBySongListId(
                            kv.decodeLong("lastPlaySongList")
                        ).songs
                    }
                }
                if (children.size > 0) {
                    transportControls.play()
                }
            }
        }

    private val mediaBrowser: MediaBrowserCompat = MediaBrowserCompat(
        context,
        componentName,
        mediaBrowserConnectionCallback,
        null
    ).apply {
        connect()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun disConnected() {
        if (mediaBrowser.isConnected) {
            updateWidgetMetadata(NOTHING_PLAYING)
            updateWidgetPlayState(EMPTY_PLAYBACK_STATE)
            mediaController.unregisterCallback(controllerCallback)
            mediaBrowser.disconnect()
            job.cancel()
        }
    }

    private fun updateProgress() {
        while (job.isActive) {
            if (mediaController.playbackState.state == PlaybackStateCompat.STATE_PLAYING || mediaController.playbackState.state == PlaybackStateCompat.STATE_PAUSED) {
                _currentPosition.value = mediaController.playbackState.position

            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun updateWidgetMetadata(metadata: MediaMetadataCompat?) {
        RemoteViews(context.packageName, R.layout.lark_widget).apply {
            applicationScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, _ -> }) {
                withContext(Dispatchers.Main) {
                    this@apply.setTextViewText(
                        R.id.title,
                        metadata?.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
                    )
                    this@apply.setTextViewText(
                        R.id.artist,
                        metadata?.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
                    )
                    this@apply.setOnClickPendingIntent(
                        R.id.previous, PendingIntent.getBroadcast(
                            MainActivity.context,
                            0,
                            Intent(ACTION_PREVIOUS),
                            PendingIntent.FLAG_IMMUTABLE
                        )
                    )
                    this@apply.setOnClickPendingIntent(
                        R.id.next, PendingIntent.getBroadcast(
                            MainActivity.context,
                            0,
                            Intent(ACTION_NEXT),
                            PendingIntent.FLAG_IMMUTABLE
                        )
                    )
                }
                setImageBitmap(metadata)
                val manager = AppWidgetManager.getInstance(context)
                val component = ComponentName(context, LarkWidgetProvider::class.java)
                manager.updateAppWidget(component, this@apply)
            }
        }
    }

    private fun RemoteViews.setImageBitmap(metadata: MediaMetadataCompat?) {
        try {
            val bitmap: Bitmap = Glide
                .with(context)
                .asBitmap()
                .load(metadata?.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI))
                .override(480, 342)
                .centerCrop()
                .submit()
                .get()
            this.setImageViewBitmap(R.id.image, bitmap)
        } catch (e: Exception) {
            val bitmap: Bitmap = Glide
                .with(context)
                .asBitmap()
                .load(R.drawable.sample)
                .override(480, 342)
                .centerCrop()
                .submit()
                .get()
            this.setImageViewBitmap(R.id.image, bitmap)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun updateWidgetPlayState(state: PlaybackStateCompat?) {
        RemoteViews(context.packageName, R.layout.lark_widget).apply {
            applicationScope.launch(Dispatchers.IO) {
                val res: Int
                if (state?.state == PlaybackStateCompat.STATE_PLAYING) {
                    res = R.drawable.ic_baseline_pause_24
                    setOnClickPendingIntent(
                        R.id.play, PendingIntent.getBroadcast(
                            MainActivity.context,
                            0,
                            Intent(ACTION_PAUSE),
                            PendingIntent.FLAG_IMMUTABLE
                        )
                    )
                } else {
                    res = R.drawable.ic_baseline_play_arrow_24
                    setOnClickPendingIntent(
                        R.id.play, PendingIntent.getBroadcast(
                            MainActivity.context,
                            0,
                            Intent(ACTION_PLAY),
                            PendingIntent.FLAG_IMMUTABLE
                        )
                    )
                }
                val bitmap: Bitmap = Glide
                    .with(context)
                    .asBitmap()
                    .load(res)
                    .circleCrop()
                    .submit()
                    .get()
                if (bitmapPrevious == null) {
                    bitmapPrevious = Glide
                        .with(context)
                        .asBitmap()
                        .load(R.drawable.ic_baseline_skip_previous_24)
                        .centerCrop()
                        .submit()
                        .get()
                }
                if (bitmapNext == null) {
                    bitmapNext = Glide
                        .with(context)
                        .asBitmap()
                        .load(R.drawable.ic_baseline_skip_next_24)
                        .centerCrop()
                        .submit()
                        .get()
                }
                this@apply.setImageViewBitmap(R.id.play, bitmap)
                this@apply.setImageViewBitmap(R.id.previous, bitmapPrevious)
                this@apply.setImageViewBitmap(R.id.next, bitmapNext)
                setImageBitmap(mediaController.metadata)
                val manager = AppWidgetManager.getInstance(context)
                val component =
                    ComponentName(context, LarkWidgetProvider::class.java)
                manager.updateAppWidget(component, this@apply)
            }
        }
    }

    companion object {
        @Suppress("PropertyName")
        val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
            .build()

        @Suppress("PropertyName")
        val NOTHING_PLAYING: MediaMetadataCompat = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, "")
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "暂无歌曲播放")
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
            .putString(METADATA_KEY_ARTIST, "未知艺术家")
            .build()
        val regex = Regex("(\\[\\d{2}:\\d{2}.\\d{2,}\\])(.*?)")
        val regexTime = Regex("(\\[(\\d{2}:\\d{2}.\\d{2,})\\]).*?")

    }

    private val TAG: String = "MediaServiceConnection"
}
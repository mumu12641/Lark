package io.github.mumu12641.lark.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.media.MediaBrowserServiceCompat
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.REPEAT_MODE_ALL
import io.github.mumu12641.lark.BaseApplication.Companion.applicationScope
import io.github.mumu12641.lark.BaseApplication.Companion.kv
import io.github.mumu12641.lark.MainActivity
import io.github.mumu12641.lark.MainActivity.Companion.context
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.*
import io.github.mumu12641.lark.network.NetworkCreator.networkService
import io.github.mumu12641.lark.room.DataBaseUtils
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil
import io.github.mumu12641.lark.ui.theme.util.YoutubeDLUtil
import kotlinx.coroutines.*


class MediaPlaybackService : MediaBrowserServiceCompat() {

    companion object {
        const val MEDIA_ROOT_ID = "Lark"
        const val NOTIFICATION_ID = 111
    }

    private val TAG = "MediaPlaybackService"
    private lateinit var mReceiver: MediaActionReceiver

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var stateBuilder: PlaybackStateCompat.Builder
    private lateinit var mPlaybackState: PlaybackStateCompat
    private lateinit var mExoPlayer: ExoPlayer

    private lateinit var manager: NotificationManager
    private lateinit var channelId: String
    private lateinit var notification: Notification
    private lateinit var notificationBuilder: NotificationCompat.Builder

    private lateinit var toast: Toast

    private var currentPlayList = mutableListOf<Song>()
    private var currentSongList: SongList? = null
    private var currentPlaySong: Song? = null

    private var isBuffering = false
    private var isError = false

    private val scope =
        CoroutineScope(Job() + Dispatchers.IO + CoroutineExceptionHandler { _, _ -> })

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSessionCompat(baseContext, TAG).apply {
            stateBuilder = PlaybackStateCompat.Builder().setActions(
                PlaybackStateCompat.ACTION_PAUSE or
                        PlaybackStateCompat.ACTION_PLAY_PAUSE or
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                        PlaybackStateCompat.ACTION_STOP or
                        PlaybackStateCompat.ACTION_SEEK_TO or
                        PlaybackStateCompat.ACTION_PLAY or
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT
            )
            mPlaybackState = stateBuilder.build()
            setPlaybackState(mPlaybackState)
            setCallback(mSessionCallback)
            setSessionToken(sessionToken)
            isActive = true
        }

        mExoPlayer = ExoPlayer.Builder(this).build()
        mExoPlayer.addListener(mExoPlayerListener)
        mExoPlayer.repeatMode = REPEAT_MODE_ALL

        manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        channelId = "Lark"
        val mChannel: NotificationChannel?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel =
                NotificationChannel(channelId, "Lark", NotificationManager.IMPORTANCE_DEFAULT)
            mChannel.apply {
                enableVibration(true)
                vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                enableVibration(false)
                vibrationPattern = LongArray(1) { 0 }
                setSound(null, null)
                manager.createNotificationChannel(this)
            }
        }

        mReceiver = MediaActionReceiver()
        val filter = IntentFilter().apply {
            addAction(ACTION_PREVIOUS)
            addAction(ACTION_PAUSE)
            addAction(ACTION_NEXT)
            addAction(ACTION_PLAY)
        }
        registerReceiver(mReceiver, filter)

        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT)
    }

    override fun onDestroy() {
        super.onDestroy()
        mExoPlayer.release()
        scope.cancel()
        unregisterReceiver(mReceiver)
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        if (kv.decodeLong("lastPlaySongList") == 0L || kv
                .decodeLong("lastPlaySong") == 0L
        ) {
            currentPlayList = emptyList<Song>().toMutableList()
            currentPlaySong = null
            currentSongList = null
        } else {
            runBlocking {
                currentSongList = DataBaseUtils.querySongListById(
                    kv.decodeLong("lastPlaySongList")
                )
                currentPlayList = DataBaseUtils.querySongListWithSongsBySongListId(
                    kv.decodeLong("lastPlaySongList")
                ).songs.toMutableList()
                currentPlaySong =
                    DataBaseUtils.querySongById(kv.decodeLong("lastPlaySong"))
                if (!currentPlayList.contains(currentPlaySong)) {
                    currentPlaySong = currentPlayList[0]
                }
            }
        }
        return BrowserRoot(MEDIA_ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        val mediaItems = ArrayList<MediaBrowserCompat.MediaItem>()
        result.detach()
        for (i in currentPlayList) {
            mediaItems.add(
                MediaBrowserCompat.MediaItem(
                    createMetadataFromSong(i).description,
                    MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
                )
            )
            mExoPlayer.addMediaItem(MediaItem.fromUri(i.mediaFileUri))
        }
        mExoPlayer.prepare()
        currentPlaySong?.let {
            mExoPlayer.seekTo(currentPlayList.indexOf(it), 0L)
        }
        result.sendResult(mediaItems)
    }

    private val mExoPlayerListener: Player.Listener =
        object : Player.Listener {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                val index = mExoPlayer.currentMediaItemIndex
                if (currentPlayList[index].isBuffered > NOT_BUFFERED) {
                    if (currentPlayList[index].youtubeId != null) {
                        bufferYoutubeSteam(currentPlayList[index], index)
                    } else {
                        bufferSong(currentPlayList[index], index)
                    }
                }
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                val song = currentPlayList[mExoPlayer.currentMediaItemIndex]
                val index = mExoPlayer.currentMediaItemIndex
                when (song.isBuffered) {
                    NOT_BUFFERED -> {
                        if (currentPlayList[index].youtubeId != null) {
                            bufferYoutubeSteam(currentPlayList[index], index)
                        } else {
                            bufferSong(currentPlayList[index], index)
                        }
                    }
                    else -> {
                        currentPlaySong = song
                        if (!isBuffering) {
                            updateAllData()
                        }
                    }
                }
            }
        }

    private val mSessionCallback: MediaSessionCompat.Callback =
        object : MediaSessionCompat.Callback() {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onPlay() {
                super.onPlay()
                if (mPlaybackState.state == PlaybackStateCompat.STATE_PAUSED || mPlaybackState.state == PlaybackStateCompat.STATE_NONE
                    || mPlaybackState.state == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT || mPlaybackState.state == PlaybackStateCompat.STATE_PLAYING
                ) {
                    mExoPlayer.play()
                    if (currentPlayList.isNotEmpty()) {
                        updateAllData()
                    }
                }
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onPause() {
                super.onPause()
                if (mPlaybackState.state == PlaybackStateCompat.STATE_PLAYING && currentPlayList.isNotEmpty()) {
                    mExoPlayer.pause()
                    updatePlayBackState(PlaybackStateCompat.STATE_PAUSED)
                    createNotification(
                        PlaybackStateCompat.STATE_PAUSED,
                        currentPlayList[mExoPlayer.currentMediaItemIndex]
                    )
                }
            }

            override fun onSeekTo(pos: Long) {
                super.onSeekTo(pos)
                mExoPlayer.seekTo(pos)
                updatePlayBackState(mPlaybackState.state)
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onSkipToNext() {
                super.onSkipToNext()
                if (currentPlayList.isNotEmpty()) {
                    mExoPlayer.seekToNextMediaItem()
                    mExoPlayer.play()
                }
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                if (currentPlayList.isNotEmpty()) {
                    mExoPlayer.seekToPreviousMediaItem()
                    mExoPlayer.play()
                }
            }

            override fun onSetRepeatMode(repeatMode: Int) {
                super.onSetRepeatMode(repeatMode)
                mExoPlayer.repeatMode = repeatMode
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onCustomAction(action: String?, extras: Bundle?) {
                super.onCustomAction(action, extras)
                when (action) {
                    CHANGE_PLAY_LIST -> {
                        changePlayList(extras)
                    }
                    SET_EMPTY -> {
                        setEmptyPlayList()
                    }
                    SEEK_TO_SONG -> {
                        seekToSong(extras)
                    }
                    ADD_SONG_TO_LIST -> {
                        addSongToList(extras)
                    }
                }
            }
        }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun changePlayList(extras: Bundle?) {
        scope.launch {
            val songId = extras?.getLong("songId")
            kv.apply {
                extras?.getLong("songListId")?.let { encode("lastPlaySongList", it) }
                if (songId != CHANGE_PLAT_LIST_SHUFFLE) {
                    songId?.let { encode("lastPlaySong", it) }
                }
            }
            extras?.apply {
                isBuffering = true
                currentPlayList = DataBaseUtils.querySongListWithSongsBySongListId(
                    getLong("songListId")
                ).songs.toMutableList()
                currentPlaySong = if (songId != CHANGE_PLAT_LIST_SHUFFLE) {
                    DataBaseUtils.querySongById(getLong("songId"))
                } else {
                    currentPlayList.shuffle()
                    currentPlayList[0]
                }
                currentSongList =
                    DataBaseUtils.querySongListById(getLong("songListId"))
                withContext(Dispatchers.Main) {
                    updateQueue(currentPlayList, currentPlaySong) {
                        isBuffering = false
                    }
                }
            }
        }
    }

    private fun setEmptyPlayList() {
        toast.cancel()
        toast.setText("Something went wrong and the playlist has been emptied")
        toast.show()
        manager.cancel(NOTIFICATION_ID)
        isBuffering = true
        mExoPlayer.clearMediaItems()
        currentPlayList = emptyList<Song>().toMutableList()
        currentPlaySong = null
        currentSongList = null
        mediaSession.setQueue(emptyList())
        updateMetadata(createMetadataFromSong(BUFFER_SONG))
        isBuffering = false
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun seekToSong(extras: Bundle?) {
        scope.launch {
            val songId = extras?.getLong("songId")
            val song = songId?.let { DataBaseUtils.querySongById(it) }
            withContext(Dispatchers.Main) {
                var index: Int
                currentPlaySong = song
                index = currentPlayList.indexOf(currentPlaySong)
                if (currentPlayList.indexOf(currentPlaySong) == -1) {
                    index = 0
                }
                song?.let {
                    updateMetadata(createMetadataFromSong(it))
                    createNotification(
                        PlaybackStateCompat.STATE_PLAYING,
                        it
                    )
                }
                mExoPlayer.seekTo(index, 0L)
                mExoPlayer.play()
                updatePlayBackState(PlaybackStateCompat.STATE_PLAYING)
            }
        }
    }

    private fun addSongToList(extras: Bundle?) {
        scope.launch {
            val songId = extras?.getLong("songId")
            val song = songId?.let { DataBaseUtils.querySongById(it) }
            withContext(Dispatchers.Main) {
                if (!currentPlayList.contains(song)) {
                    currentPlayList.add(
                        mExoPlayer.currentMediaItemIndex + 1,
                        song!!
                    )
                    mediaSession.setQueue(currentPlayList.map {
                        MediaSessionCompat.QueueItem(
                            createMetadataFromSong(it).description,
                            it.songId
                        )
                    })
                    mExoPlayer.addMediaItem(
                        mExoPlayer.currentMediaItemIndex + 1,
                        MediaItem.fromUri(song.mediaFileUri)
                    )
                }
            }
        }
    }

    private fun createMetadataFromSong(song: Song): MediaMetadataCompat = with(song) {
        val builder = MediaMetadataCompat.Builder()
        this.lyrics?.let {
            builder.putString(MediaMetadataCompat.METADATA_KEY_COMPILATION, it)
        }
        builder
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, songId.toString())
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, songTitle)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, songSinger)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration.toLong())
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, songAlbumFileUri)
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, mediaFileUri)
            .putString(MediaMetadataCompat.METADATA_KEY_COMPILATION, neteaseId.toString())
            .build()
    }

    private fun updatePlayBackState(state: Int) {
        mPlaybackState = stateBuilder
            .setState(state, mExoPlayer.currentPosition, 1.0f)
            .build()
        mediaSession.setPlaybackState(mPlaybackState)
    }

    private fun updateMetadata(mediaMetadataCompat: MediaMetadataCompat) {
        mediaSession.setMetadata(mediaMetadataCompat)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun updateAllData() {
        updatePlayBackState(PlaybackStateCompat.STATE_PLAYING)
        updateMetadata(createMetadataFromSong(currentPlayList[mExoPlayer.currentMediaItemIndex]))
        createNotification(
            PlaybackStateCompat.STATE_PLAYING,
            currentPlayList[mExoPlayer.currentMediaItemIndex]
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun updateQueue(
        songList: List<Song>,
        song: Song? = null,
        setBuffer: (() -> Unit)? = null
    ) {
        mExoPlayer.clearMediaItems()
        songList.apply {
            mediaSession.setQueue(this.map {
                MediaSessionCompat.QueueItem(createMetadataFromSong(it).description, it.songId)
            })
            mExoPlayer.setMediaItems(this.map {
                MediaItem.fromUri(it.mediaFileUri)
            })
        }
        mExoPlayer.prepare()
        song?.let {
            mExoPlayer.seekTo(currentPlayList.indexOf(it), 0L)
        }
        setBuffer?.let {
            it()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun bufferYoutubeSteam(song: Song, index: Int) {
        var song1 = song
        applicationScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, _ ->
            Log.d(TAG, "bufferSong: error")
        }) {
            withContext(Dispatchers.Main) {
                isBuffering = true
                updateMetadata(createMetadataFromSong(BUFFER_SONG))
            }
            try {
                val streamUrl = YoutubeDLUtil.getStream(song1.youtubeId!!)
                song1 = song1.copy(
                    isBuffered = BUFFERED,
                    mediaFileUri = streamUrl
                )
                DataBaseUtils.updateSong(song1)

                withContext(Dispatchers.Main) {
                    currentPlayList[index] = song1
                    currentPlaySong = song1
                    updateQueue(currentPlayList, song1) {
                        isBuffering = false
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, context.getString(R.string.check_network),Toast.LENGTH_SHORT).show()
                    setEmptyPlayList()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun bufferSong(song: Song, index: Int) {
        var song1 = song
        applicationScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, _ ->
            Log.d(TAG, "bufferSong: error")
        }) {
            withContext(Dispatchers.Main) {
                isBuffering = true
                updateMetadata(createMetadataFromSong(BUFFER_SONG))
            }
            val detail = networkService.getSongDetail(song1.neteaseId.toString())
            val songQuality = networkService.getLevelMusic(
                song1.neteaseId, kv.decodeString(
                    PreferenceUtil.MUSIC_QUALITY, "standard"
                )!!
            )
            if (songQuality.data[0].url != null) {
                song1 = song1.copy(
                    isBuffered = BUFFERED,
                    mediaFileUri = songQuality.data[0].url!!,
                    duration = if (detail.privileges[0].fee == 0 || detail.privileges[0].fee == 8) detail.songs[0].dt else VIP_DURATION
                )
                DataBaseUtils.updateSong(song1)

                withContext(Dispatchers.Main) {
                    currentPlayList[index] = song1
                    currentPlaySong = song1
                    updateQueue(currentPlayList, song1) {
                        isBuffering = false
                    }
                }
            } else {
                val searchSong = networkService.getSongUrl(song1.neteaseId)
                if (searchSong.data[0].url != null) {
                    song1 = song1.copy(
                        isBuffered = BUFFERED,
                        mediaFileUri = searchSong.data[0].url!!,
                        duration = if (detail.privileges[0].fee == 0 || detail.privileges[0].fee == 8) detail.songs[0].dt else VIP_DURATION
                    )
                    DataBaseUtils.updateSong(song1)
                    withContext(Dispatchers.Main) {
                        currentPlayList[index] = song1
                        currentPlaySong = song1
                        updateQueue(currentPlayList, song1) {
                            isBuffering = false
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Log.d(TAG, "bufferSong: null $index + " + currentPlayList.size)
                        toast.setText("Error")
                        toast.show()
                        currentPlaySong = currentPlayList[index + 1]
                        updateQueue(currentPlayList, currentPlaySong) {
                            isBuffering = false
                        }
                    }
                }
            }
        }
    }

    inner class MediaActionReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_PAUSE -> {
                    mediaSession.controller?.transportControls?.pause()
                }
                ACTION_NEXT -> {
                    mediaSession.controller?.transportControls?.skipToNext()
                }
                ACTION_PREVIOUS -> {
                    mediaSession.controller?.transportControls?.skipToPrevious()
                }
                ACTION_PLAY -> {
                    mediaSession.controller?.transportControls?.play()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun createNotification(state: Int, song: Song) {
        val controller = mediaSession.controller
        val mediaMetadata = controller.metadata
        val description = mediaMetadata.description
        val clickPendingIntent = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        notificationBuilder = NotificationCompat.Builder(context, channelId).apply {

            setContentTitle(description.title)
            setContentText(description.subtitle)
            setSubText(description.description)
            setContentIntent(clickPendingIntent)
            setSmallIcon(R.drawable.ic_stat_notification_icon)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setSound(null)
            setVibrate(LongArray(1) { 0 })
            setSilent(true)

            addAction(
                NotificationCompat.Action(
                    R.drawable.ic_baseline_skip_previous_24,
                    "Next",
                    PendingIntent.getBroadcast(
                        context,
                        0,
                        Intent(ACTION_PREVIOUS),
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )
            )

            if (state == PlaybackStateCompat.STATE_PLAYING) {
                addAction(
                    NotificationCompat.Action(
                        R.drawable.ic_baseline_pause_24,
                        "Pause",
                        PendingIntent.getBroadcast(
                            context,
                            0,
                            Intent(ACTION_PAUSE),
                            PendingIntent.FLAG_IMMUTABLE
                        )
                    )

                )
            } else if (state == PlaybackStateCompat.STATE_PAUSED) {
                addAction(
                    NotificationCompat.Action(
                        R.drawable.ic_baseline_play_arrow_24,
                        "Play",
                        PendingIntent.getBroadcast(
                            context,
                            0,
                            Intent(ACTION_PLAY),
                            PendingIntent.FLAG_IMMUTABLE
                        )
                    )
                )
            }
            addAction(
                NotificationCompat.Action(
                    R.drawable.ic_baseline_skip_next_24,
                    "Next",
                    PendingIntent.getBroadcast(
                        context,
                        0,
                        Intent(ACTION_NEXT),
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )
            )
            setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )

        }
        scope.launch {
            try {
                val bitmap: Bitmap = Glide
                    .with(context)
                    .asBitmap()
                    .load(song.songAlbumFileUri)
                    .centerCrop()
                    .submit()
                    .get()
                notificationBuilder.setLargeIcon(bitmap)
                notificationBuilder.setProgress(0, 0, false)
                notification = notificationBuilder.build()
                startForeground(NOTIFICATION_ID, notification)
            } catch (e: Exception) {
                val bitmap: Bitmap = Glide
                    .with(context)
                    .asBitmap()
                    .load(R.mipmap.new_icon)
                    .submit()
                    .get()
                notificationBuilder.setLargeIcon(bitmap)
                notificationBuilder.setProgress(0, 0, false)
                notification = notificationBuilder.build()
                startForeground(NOTIFICATION_ID, notification)
            }
        }
    }
}
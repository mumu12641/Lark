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
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.media.MediaBrowserServiceCompat
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.tencent.mmkv.MMKV
import io.github.mumu12641.lark.MainActivity
import io.github.mumu12641.lark.MainActivity.Companion.context
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.*
import io.github.mumu12641.lark.room.DataBaseUtils
import kotlinx.coroutines.*


class MediaPlaybackService : MediaBrowserServiceCompat() {

    companion object {
        const val MEDIA_ROOT_ID = "Lark"
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

    private var currentPlayList = mutableListOf<Song>()
    private var currentSongList: SongList? = null
    private var currentPlaySong: Song? = null

    private val scope = CoroutineScope(Job() + Dispatchers.IO)


    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSessionCompat(baseContext, TAG).apply {
            setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                        or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )
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
        mExoPlayer.addListener(object : Player.Listener {
            @RequiresApi(Build.VERSION_CODES.M)

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                Log.d(TAG, "onMediaItemTransition: ")
                updatePlayBackState(PlaybackStateCompat.STATE_PLAYING)
                updateMetadata(createMetadataFromSong(currentPlayList[mExoPlayer.currentMediaItemIndex]))
                createNotification(
                    PlaybackStateCompat.STATE_PLAYING,
                    currentPlayList[mExoPlayer.currentMediaItemIndex]
                )
            }
        })

        manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        channelId = "Lark"
        val mChannel: NotificationChannel?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel =
                NotificationChannel(channelId, "Lark", NotificationManager.IMPORTANCE_DEFAULT)
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            mChannel.enableVibration(false)
            mChannel.vibrationPattern = LongArray(1){0}
            mChannel.setSound(null,null)
            manager.createNotificationChannel(mChannel)
        }

        mReceiver = MediaActionReceiver()
        val filter = IntentFilter().apply {
            addAction(ACTION_PREVIOUS)
            addAction(ACTION_PAUSE)
            addAction(ACTION_NEXT)
            addAction(ACTION_PLAY)
        }
        registerReceiver(mReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
        mExoPlayer.release()
        scope.cancel()
        unregisterReceiver(mReceiver)
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        if (MMKV.defaultMMKV().decodeLong("lastPlaySongList") == 0L || MMKV.defaultMMKV()
                .decodeLong("lastPlaySong") == 0L
        ) {
            currentPlayList = emptyList<Song>().toMutableList()
            currentPlaySong = null
            currentSongList = null
        } else {
            runBlocking {
                currentSongList = DataBaseUtils.querySongListById(
                    MMKV.defaultMMKV().decodeLong("lastPlaySongList")
                )
                currentPlayList = DataBaseUtils.querySongListWithSongsBySongListId(
                    MMKV.defaultMMKV().decodeLong("lastPlaySongList")
                ).songs.toMutableList()
                currentPlaySong =
                    DataBaseUtils.querySongById(MMKV.defaultMMKV().decodeLong("lastPlaySong"))
            }
        }
        return BrowserRoot(MEDIA_ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        runBlocking {
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
                mExoPlayer.seekTo(currentPlayList.indexOf(it),0L)
            }
            result.sendResult(mediaItems)
        }
    }

    private fun createMetadataFromSong(song: Song): MediaMetadataCompat = with(song) {
        MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, songId.toString())
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, songTitle)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, songSinger)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration.toLong())
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, songAlbumFileUri)
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, mediaFileUri)
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

    private fun updateQueue(songList: List<Song>) {
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
        mExoPlayer.seekTo(currentPlayList.indexOf(currentPlaySong),0L)
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
            setSmallIcon(R.drawable.icon)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setSound(null)
            setVibrate(LongArray(1){0})
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
        Thread {
            try {
                val bitmap: Bitmap = Glide
                    .with(context)
                    .asBitmap()
                    .load(song.songAlbumFileUri)
                    .submit()
                    .get()
                notificationBuilder.setLargeIcon(bitmap)
            } catch (e: Exception) {
                val bitmap: Bitmap = Glide
                    .with(context)
                    .asBitmap()
                    .load(R.drawable.ornithology)
                    .submit()
                    .get()
                notificationBuilder.setLargeIcon(bitmap)
            }
            notificationBuilder.setProgress(0, 0, false)
            notification = notificationBuilder.build()
            startForeground(1, notification)
        }.start()
    }


    private val mSessionCallback: MediaSessionCompat.Callback =
        object : MediaSessionCompat.Callback() {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onPlay() {
                super.onPlay()
                Log.d(TAG, "onPlay")
                if (mPlaybackState.state == PlaybackStateCompat.STATE_PAUSED || mPlaybackState.state == PlaybackStateCompat.STATE_NONE
                    || mPlaybackState.state == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT || mPlaybackState.state == PlaybackStateCompat.STATE_PLAYING
                ) {
                    mExoPlayer.play()
                    updatePlayBackState(PlaybackStateCompat.STATE_PLAYING)
                    updateMetadata(createMetadataFromSong(currentPlayList[mExoPlayer.currentMediaItemIndex]))
                    createNotification(
                        PlaybackStateCompat.STATE_PLAYING,
                        currentPlayList[mExoPlayer.currentMediaItemIndex]
                    )
                }
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onPause() {
                super.onPause()
                Log.d(TAG, "onPause")
                if (mPlaybackState.state == PlaybackStateCompat.STATE_PLAYING) {
                    mExoPlayer.pause()
                    updatePlayBackState(PlaybackStateCompat.STATE_PAUSED)
                    createNotification(
                        PlaybackStateCompat.STATE_PAUSED,
                        currentPlayList[mExoPlayer.currentMediaItemIndex]
                    )
//                    updateNotificationAction(PlaybackStateCompat.STATE_PAUSED)

                }
            }

            override fun onSeekTo(pos: Long) {
                super.onSeekTo(pos)
                mExoPlayer.seekTo(pos)
                updatePlayBackState(PlaybackStateCompat.STATE_PLAYING)
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onSkipToNext() {
                super.onSkipToNext()
                Log.d(TAG, "onSkipToNext")
                mExoPlayer.seekToNextMediaItem()
                updatePlayBackState(mPlaybackState.state)
                updateMetadata(createMetadataFromSong(currentPlayList[mExoPlayer.currentMediaItemIndex]))
                createNotification(
                    mPlaybackState.state,
                    currentPlayList[mExoPlayer.currentMediaItemIndex]
                )
//                updateNotificationAction(PlaybackStateCompat.STATE_PLAYING)

            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                Log.d(TAG, "onSkipToPrevious: ")
                mExoPlayer.seekToPreviousMediaItem()
                updatePlayBackState(mPlaybackState.state)
                updateMetadata(createMetadataFromSong(currentPlayList[mExoPlayer.currentMediaItemIndex]))
                createNotification(
                    mPlaybackState.state,
                    currentPlayList[mExoPlayer.currentMediaItemIndex]
                )
//                updateNotificationAction(PlaybackStateCompat.STATE_PLAYING)

            }

            override fun onCustomAction(action: String?, extras: Bundle?) {
                super.onCustomAction(action, extras)
                when (action) {
                    CHANGE_PLAY_LIST -> {
                        scope.launch {
                            val songId = extras?.get("songId") as Long
                            MMKV.defaultMMKV().apply {
                                encode("lastPlaySongList", extras.get("songListId") as Long)
                                if ( songId != CHANGE_PLAT_LIST_SHUFFLE) {
                                    encode("lastPlaySong", songId)
                                }
                            }
                            extras.apply {
                                currentPlayList = DataBaseUtils.querySongListWithSongsBySongListId(
                                    get("songListId") as Long
                                ).songs.toMutableList()
                                currentPlaySong = if (songId!= CHANGE_PLAT_LIST_SHUFFLE) {
                                    DataBaseUtils.querySongById(get("songId") as Long)
                                } else {
                                    currentPlayList.shuffle()
                                    currentPlayList[0]
                                }
                                currentSongList =
                                    DataBaseUtils.querySongListById(get("songListId") as Long)
                                withContext(Dispatchers.Main) {
                                    updateQueue(currentPlayList)
                                }
                            }
                        }
                    }
                }
            }


            override fun onSetRepeatMode(repeatMode: Int) {
                super.onSetRepeatMode(repeatMode)
            }

            override fun onSetShuffleMode(shuffleMode: Int) {
                super.onSetShuffleMode(shuffleMode)
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
}



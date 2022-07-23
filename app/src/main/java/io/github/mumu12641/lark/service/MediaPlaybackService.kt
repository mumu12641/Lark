package io.github.mumu12641.lark.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.core.app.NotificationCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import io.github.mumu12641.lark.MainActivity
import io.github.mumu12641.lark.MainActivity.Companion.context
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.LocalSongListId
import io.github.mumu12641.lark.entity.Song
import io.github.mumu12641.lark.room.DataBaseUtils
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.ArrayList

class MediaPlaybackService:MediaBrowserServiceCompat() {

    companion object{
        const val MEDIA_ROOT_ID = "Lark"
    }

    private  val TAG = "MediaPlaybackService"

    private lateinit var mediaSession:MediaSessionCompat
    private lateinit var stateBuilder:PlaybackStateCompat.Builder
    private lateinit var mPlaybackState:PlaybackStateCompat
    private lateinit var mExoPlayer:ExoPlayer

    private lateinit var manager: NotificationManager
    private lateinit var channelId: String
    private var list = mutableListOf<Song>()

    private lateinit var test:Song


    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSessionCompat(baseContext,TAG).apply {
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                        or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
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
        mExoPlayer.addListener(object : Player.Listener{
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if (playbackState == ExoPlayer.STATE_ENDED){
                    Log.d(TAG, "end")
                }
            }
        })

        manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        channelId = "Lark"
        val mChannel: NotificationChannel?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel =
                NotificationChannel(channelId, "name", NotificationManager.IMPORTANCE_DEFAULT)
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            manager.createNotificationChannel(mChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mExoPlayer.release()
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        // TODO rootHints get something
        return BrowserRoot(MEDIA_ROOT_ID,null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        runBlocking {
            val mediaItems = ArrayList<MediaBrowserCompat.MediaItem>()
            result.detach()
            for (i in DataBaseUtils.querySongListWithSongsBySongListId(LocalSongListId).songs){
                mediaItems.add(
                    MediaBrowserCompat.MediaItem(
                        createMetadataFromSong(i).description,
                        MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
                    )
                )
                mExoPlayer.addMediaItem(MediaItem.fromUri(i.mediaFileUri))

                list.add(i)
            }
            mExoPlayer.prepare()
            result.sendResult(mediaItems)
        }
    }

    private fun createMetadataFromSong(song: Song):MediaMetadataCompat = with(song) {
        MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, songId.toString())
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, songTitle)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, songSinger)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration.toLong())
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,songAlbumFileUri)
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI,mediaFileUri)
            .build()
    }

    private fun updatePlayBackState(state:Int){
        mPlaybackState = stateBuilder
            .setState(state,mExoPlayer.currentPosition,1.0f)
            .build()
        mediaSession.setPlaybackState(mPlaybackState)
    }

    private fun updateMetadata(mediaMetadataCompat: MediaMetadataCompat){
        mediaSession.setMetadata(mediaMetadataCompat)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun createNotification(state: Int, song: Song){
        val controller = mediaSession.controller
        val mediaMetadata = controller.metadata
        val description = mediaMetadata.description
        val clickPendingIntent = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        val builder = NotificationCompat.Builder(context,channelId).apply {

            setContentTitle(description.title)
            setContentText(description.subtitle)
            setSubText(description.description)
            setContentIntent(clickPendingIntent)
            setSmallIcon(R.drawable.ornithology)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)


            addAction(
                NotificationCompat.Action(
                    R.drawable.ic_baseline_skip_previous_24,
                    "Previous",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context,
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                    )
                )
            )

            if (state == PlaybackStateCompat.STATE_PLAYING) {
                addAction(
                    NotificationCompat.Action(
                        R.drawable.ic_baseline_pause_24,
                        "Pause",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            context,
                            PlaybackStateCompat.ACTION_PAUSE
                        )
                    )

                )
            } else if (state == PlaybackStateCompat.STATE_PAUSED){
                addAction(
                    NotificationCompat.Action(
                        R.drawable.ic_baseline_play_arrow_24,
                        "Play",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            context,
                            PlaybackStateCompat.ACTION_PLAY
                        )
                    )

                )
            }
            addAction(
                NotificationCompat.Action(
                    R.drawable.ic_baseline_skip_next_24,
                    "Next",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context,
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT
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
                builder.setLargeIcon(bitmap)
            } catch (e: Exception) {
                val bitmap: Bitmap = Glide
                    .with(context)
                    .asBitmap()
                    .load(R.drawable.ornithology)
                    .submit()
                    .get()
                builder.setLargeIcon(bitmap)
            }
            builder.setProgress(0, 0, false)
            startForeground(1, builder.build())
        }.start()
    }


    private val mSessionCallback : MediaSessionCompat.Callback =
        object:MediaSessionCompat.Callback(){
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onPlay() {
                super.onPlay()
                Log.d(TAG, "onPlay")
                if (mPlaybackState.state == PlaybackStateCompat.STATE_PAUSED || mPlaybackState.state == PlaybackStateCompat.STATE_NONE
                    || mPlaybackState.state == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT || mPlaybackState.state == PlaybackStateCompat.STATE_PLAYING
                ) {
                    mExoPlayer.play()
                    updatePlayBackState(PlaybackStateCompat.STATE_PLAYING)
                    updateMetadata(createMetadataFromSong(list[mExoPlayer.currentMediaItemIndex]))
                    createNotification(PlaybackStateCompat.STATE_PLAYING,list[mExoPlayer.currentMediaItemIndex])
                }
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onPause() {
                super.onPause()
                Log.d(TAG, "onPause")
                if (mPlaybackState.state == PlaybackStateCompat.STATE_PLAYING) {
                    mExoPlayer.pause()
                    updatePlayBackState(PlaybackStateCompat.STATE_PAUSED)
                    createNotification(PlaybackStateCompat.STATE_PAUSED,list[mExoPlayer.currentMediaItemIndex])
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
                updatePlayBackState(PlaybackStateCompat.STATE_PLAYING)
                updateMetadata(createMetadataFromSong(list[mExoPlayer.currentMediaItemIndex]))
                createNotification(PlaybackStateCompat.STATE_PLAYING,list[mExoPlayer.currentMediaItemIndex])
            }

            override fun onCustomAction(action: String?, extras: Bundle?) {
                super.onCustomAction(action, extras)
            }


            @RequiresApi(Build.VERSION_CODES.M)
            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                mExoPlayer.seekToPreviousMediaItem()
                updatePlayBackState(PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS)
                updateMetadata(createMetadataFromSong(list[mExoPlayer.currentMediaItemIndex]))
                createNotification(PlaybackStateCompat.STATE_PLAYING,list[mExoPlayer.currentMediaItemIndex])

            }
        }
}



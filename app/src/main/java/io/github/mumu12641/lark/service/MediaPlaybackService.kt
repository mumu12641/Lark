package io.github.mumu12641.lark.service

import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import io.github.mumu12641.lark.entity.LocalSongListId
import io.github.mumu12641.lark.entity.Song
import io.github.mumu12641.lark.room.DataBaseUtils
import kotlinx.coroutines.runBlocking

class MediaPlaybackService:MediaBrowserServiceCompat() {

    companion object{
        const val MEDIA_ROOT_ID = "Lark"
        const val EMPTY_MEDIA_ROOT_ID = "Empty"
    }

    private  val TAG = "MediaPlaybackService"

    private lateinit var mediaSession:MediaSessionCompat
    private lateinit var stateBuilder:PlaybackStateCompat.Builder
    private lateinit var mPlaybackState:PlaybackStateCompat
    private lateinit var mExoPlayer:ExoPlayer


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
//                    createNotification(
//                        PlaybackStateCompat.STATE_PLAYING,
//                        list?.get(mExoPlayer.currentMediaItemIndex)!!
//                    )
                }
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onPause() {
                super.onPause()
                Log.d(TAG, "onPause")
                if (mPlaybackState.state == PlaybackStateCompat.STATE_PLAYING) {
                    mExoPlayer.pause()
                    updatePlayBackState(PlaybackStateCompat.STATE_PAUSED)
//                    createNotification(
//                        PlaybackStateCompat.STATE_PAUSED,
//                        list?.get(mExoPlayer.currentMediaItemIndex)!!
//                    )
                }
            }

            override fun onSeekTo(pos: Long) {
                super.onSeekTo(pos)
            }

            override fun onSkipToNext() {
                super.onSkipToNext()
            }

            override fun onCustomAction(action: String?, extras: Bundle?) {
                super.onCustomAction(action, extras)
            }


            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
            }
        }
}



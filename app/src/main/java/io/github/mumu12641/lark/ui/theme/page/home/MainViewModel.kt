package io.github.mumu12641.lark.ui.theme.page.home

import android.content.ComponentName
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mumu12641.lark.BaseApplication.Companion.context
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.*
import io.github.mumu12641.lark.entity.network.Banner
import io.github.mumu12641.lark.network.NetworkCreator.networkService
import io.github.mumu12641.lark.room.DataBaseUtils
import io.github.mumu12641.lark.service.MediaPlaybackService
import io.github.mumu12641.lark.service.MediaServiceConnection
import io.github.mumu12641.lark.service.MediaServiceConnection.Companion.EMPTY_PLAYBACK_STATE
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val TAG = "MainViewModel"

    var mediaServiceConnection: MediaServiceConnection = MediaServiceConnection(
        context,
        ComponentName(context, MediaPlaybackService::class.java)
    )

    val currentPlayState by lazy { mediaServiceConnection.playState }

    private val _loadState = MutableStateFlow(Load.NONE)
    val loadState: StateFlow<Int> = _loadState

    private val _homeScreenUiState = MutableStateFlow(HomeScreenUiState())
    val homeScreenUiState = _homeScreenUiState

    private val _playState = MutableStateFlow(PlayState(mediaServiceConnection))
    val playState = _playState

    data class PlayState(
        val currentPlayMetadata: Flow<MediaMetadataCompat>,
        val currentPlayState: Flow<PlaybackStateCompat>,
        val currentSongList: Flow<SongList>,
        val currentPlaySongs: Flow<List<Song>>
    ) {
        constructor(mediaServiceConnection: MediaServiceConnection) : this(
            mediaServiceConnection.playMetadata,
            mediaServiceConnection.playState,
            mediaServiceConnection.currentSongList,
            mediaServiceConnection.playList
        )
    }

    data class HomeScreenUiState(
        val allSongList: Flow<List<SongList>> = DataBaseUtils.queryAllSongList().map {
            it.filter { songList ->
                songList.type in 1 until ARTIST_SONGLIST_TYPE
            }
        },
        val artistSongList: Flow<List<SongList>> = DataBaseUtils.queryAllSongList().map {
            val filterList = it.filter { songList ->
                songList.type == ARTIST_SONGLIST_TYPE
            }
            if (filterList.size <= 5) {
                return@map filterList
            } else {
                return@map filterList.sortedByDescending { songList ->
                    songList.songNumber
                }.subList(0, 5)
            }
        }
    )

    private val _bannerState = MutableStateFlow<List<Banner.BannerX>>(emptyList())
    val bannerState = _bannerState

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
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, e ->
            e.message?.let { Log.d(TAG, it) }
        }) {
            _bannerState.value = networkService.getBanner().banners.filter {
                it.targetType == 1
            }
        }
    }

    fun refreshArtist() {
        viewModelScope.launch(Dispatchers.IO) {
            val songLists = DataBaseUtils.querySongListsByType(ARTIST_SONGLIST_TYPE)
            for (i in songLists) {
                if (i.description == context.getString(R.string.no_description_text)) {
                    try {
                        val artistId =
                            networkService.getSearchArtistResponse(i.songListTitle).result.artists[0].artistId
                        artistId?.let {
                            val artistDetails =
                                networkService.getArtistDetail(artistId).data.artist
                            DataBaseUtils.updateSongList(
                                i.copy(
                                    imageFileUri = artistDetails.cover,
                                    description = artistDetails.briefDesc
                                )
                            )
                        }
                    } catch (e: Exception) {
                        Log.d(TAG, "refreshArtist: error" + e.message)
                    }
                }
            }
        }
    }

    fun getNeteaseSongList(id: Long) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, e ->
            _loadState.value = Load.ERROR
            Log.d(TAG, "getNeteaseSongList: " + e.message)
        }
        ) {
            _loadState.value = Load.LOADING
            val list = networkService.getNeteaseSongList(id)
            val tracks = networkService.getNeteaseSongListTracks(id)
            val songlist = SongList(
                0L,
                list.playlist.name,
                list.playlist.createTime.toString(),
                list.playlist.trackCount,
                description = list.playlist.description
                    ?: context.getString(R.string.no_description_text),
                list.playlist.coverImgUrl,
                CREATE_SONGLIST_TYPE
            )
            val listId: Long = DataBaseUtils.insertSongList(
                songlist
            )
            for (i in tracks.songs) {
                val song = Song(
                    0L,
                    i.name,
                    i.ar.joinToString(",") { it.name },
                    i.al.picUrl,
                    EMPTY_URI + i.al.picUrl,
                    i.dt,
                    neteaseId = i.id.toLong(),
                    isBuffered = NOT_BUFFERED
                )
                if (!DataBaseUtils.isNeteaseIdExist(i.id.toLong())) {
                    DataBaseUtils.insertSong(song)
                }
                val songId = DataBaseUtils.querySongIdByNeteaseId(i.id.toLong())
                if (!DataBaseUtils.isRefExist(listId, songId)) {
                    DataBaseUtils.insertRef(PlaylistSongCrossRef(listId, songId))
                }
            }

            _loadState.value = Load.SUCCESS
        }
    }


    fun playMedia(songListId: Long, songId: Long) {
        Log.d("TAG", "playMedia: $songListId + $songId")
        val bundle = Bundle()
        bundle.apply {
            putLong("songListId", songListId)
            putLong("songId", songId)
        }
        mediaServiceConnection.transportControls.sendCustomAction(CHANGE_PLAY_LIST, bundle)
    }

    fun addSongToCurrentList(songId: Long) {
        val bundle = Bundle()
        bundle.apply {
            putLong("songId", songId)
        }
        mediaServiceConnection.transportControls.sendCustomAction(ADD_SONG_TO_LIST, bundle)
    }

    fun seekToSong(songId: Long) {
        val bundle = Bundle()
        bundle.apply {
            putLong("songId", songId)
        }
        mediaServiceConnection.transportControls.sendCustomAction(SEEK_TO_SONG, bundle)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: ")
        mediaServiceConnection.disConnected()
    }
}
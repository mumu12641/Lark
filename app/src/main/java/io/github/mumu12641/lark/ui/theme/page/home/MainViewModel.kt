package io.github.mumu12641.lark.ui.theme.page.home

import android.content.ComponentName
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mumu12641.lark.BaseApplication.Companion.applicationScope
import io.github.mumu12641.lark.BaseApplication.Companion.context
import io.github.mumu12641.lark.BaseApplication.Companion.kv
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.*
import io.github.mumu12641.lark.entity.network.netease.Banner
import io.github.mumu12641.lark.entity.network.netease.UpdateInfo
import io.github.mumu12641.lark.network.Repository
import io.github.mumu12641.lark.room.DataBaseUtils
import io.github.mumu12641.lark.service.MediaPlaybackService
import io.github.mumu12641.lark.service.MediaServiceConnection
import io.github.mumu12641.lark.service.MediaServiceConnection.Companion.EMPTY_PLAYBACK_STATE
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.AUTO_UPDATE
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.REPEAT_MODE
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.REPEAT_ONE_NOT_REMIND
import io.github.mumu12641.lark.ui.theme.util.UpdateUtil.checkForUpdate
import io.github.mumu12641.lark.ui.theme.util.UpdateUtil.getUpdateInfo
import io.github.mumu12641.lark.ui.theme.util.YoutubeDLUtil
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val TAG = "MainViewModel"

    private var mediaServiceConnection: MediaServiceConnection = MediaServiceConnection(
        context,
        ComponentName(context, MediaPlaybackService::class.java)
    )

    private val _checkForUpdate = MutableStateFlow(CheckUpdateState())
    val checkForUpdate = _checkForUpdate

    private val currentPlayState by lazy { mediaServiceConnection.playState }

    private val _loadState = MutableStateFlow(ExpandLoadState())
    val loadState = _loadState

    val currentPosition by lazy { mediaServiceConnection.currentPosition }

    private val _homeScreenUiState = MutableStateFlow(HomeScreenUiState())
    val homeScreenUiState = _homeScreenUiState

    private val _playState = MutableStateFlow(PlayState(mediaServiceConnection))
    val playState = _playState

    private val _repeatState = MutableStateFlow(RepeatState())
    val repeatState = _repeatState

    private val _listRepeatState = MutableStateFlow(
        value = kv.decodeInt(
            REPEAT_MODE,
            PlaybackStateCompat.REPEAT_MODE_ALL
        ) == PlaybackStateCompat.REPEAT_MODE_ALL
    )
    val listRepeatState = _listRepeatState


    init {
        viewModelScope.launch(handleIOExceptionContext) {
            _bannerState.value = Repository.getBanner().banners.filter {
                it.targetType == 1
            }
            if (kv.encode(AUTO_UPDATE, true)) {
                _checkForUpdate.update {
                    val info = getUpdateInfo()
                    it.copy(info = info, showDialog = checkForUpdate(info))
                }
            }
        }
    }

    data class CheckUpdateState(
        val info: UpdateInfo = UpdateInfo(),
        val showDialog: Boolean = false,
    )

    data class ExpandLoadState(
        val num: Int = 0,
        val loadState: io.github.mumu12641.lark.network.LoadState = io.github.mumu12641.lark.network.LoadState.None()
//        val loadState: LoadResult<String> = LoadResult.None()
    )

    data class RepeatState(
        val repeatOne: Boolean = false,
        val notRemind: Boolean = kv.decodeBool(REPEAT_ONE_NOT_REMIND, false)
    )

    data class PlayState(
        val currentPlayState: Flow<PlaybackStateCompat>,
        val currentSongList: Flow<SongList>,
        val currentPlaySongs: Flow<List<Song>>,
        val currentPlaySong: Flow<Song>,
    ) {
        constructor(mediaServiceConnection: MediaServiceConnection) : this(
            mediaServiceConnection.playState,
            mediaServiceConnection.currentSongList,
            mediaServiceConnection.playList,
            mediaServiceConnection.currentPlaySong
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

    fun setUpdateDialog() {
        _checkForUpdate.update {
            it.copy(showDialog = false)
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

    fun onSetRepeatMode(repeatOne: Boolean) {
        _repeatState.update {
            it.copy(repeatOne = repeatOne)
        }
        mediaServiceConnection.transportControls.setRepeatMode(
            if (repeatOne) PlaybackStateCompat.REPEAT_MODE_ONE else kv.decodeInt(
                REPEAT_MODE,
                PlaybackStateCompat.REPEAT_MODE_ALL
            )
        )
    }

    fun onSetPlayListMode(repeatAll: Boolean) {
        _listRepeatState.value = repeatAll
        kv.apply {
            if (repeatAll) encode(REPEAT_MODE, PlaybackStateCompat.REPEAT_MODE_ALL)
            else encode(REPEAT_MODE, PlaybackStateCompat.REPEAT_MODE_NONE)

        }
        mediaServiceConnection.transportControls.setRepeatMode(if (repeatAll) PlaybackStateCompat.REPEAT_MODE_ALL else PlaybackStateCompat.REPEAT_MODE_NONE)
    }

    fun setRemindDialog(notRemind: Boolean) {
        _repeatState.update {
            it.copy(notRemind = notRemind)
        }
        kv.encode(REPEAT_ONE_NOT_REMIND, notRemind)
    }

    fun playMedia(songListId: Long, songId: Long) {
        onSetRepeatMode(false)
        val bundle = Bundle()
        bundle.apply {
            putLong("songListId", songListId)
            putLong("songId", songId)
        }
        Log.d(TAG, "playMedia: $songId")
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


    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: ")
        mediaServiceConnection.disConnected()
    }

    fun refreshArtist() {
        viewModelScope.launch(handleIOExceptionContext) {
            val songLists = DataBaseUtils.querySongListsByType(ARTIST_SONGLIST_TYPE)
            for (i in songLists) {
                if (i.description == context.getString(R.string.no_description_text)) {
                    try {
                        val artistId =
                            Repository.getSearchArtistResponse(i.songListTitle).result.artists[0].artistId
                        artistId?.let {
                            val artistDetails =
                                Repository.getArtistDetail(artistId).data.artist
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

    fun getYoutubePlayList(url: String) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, _ ->
            _loadState.update { state ->
                state.copy(loadState = io.github.mumu12641.lark.network.LoadState.Fail("0"))
            }
            applicationScope.launch(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    context.getString(R.string.check_network),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }) {
            _loadState.update {
                it.copy(
                    num = 0,
                    loadState = io.github.mumu12641.lark.network.LoadState.Loading("0")
                )
            }
            val playListInfo = YoutubeDLUtil.getPlayListInfo(url)
            Log.d(TAG, "getYoutubePlayList: " + playListInfo.playlist_count)
            if(playListInfo.playlist_count >0) {
                _loadState.update {
                    it.copy(num = playListInfo.playlist_count)
                }
            }
            val songList = SongList(
                0L,
                playListInfo.title,
                "",
                playListInfo.playlist_count,
                playListInfo.description,
                "",
                CREATE_SONGLIST_TYPE
            )
            val listId = DataBaseUtils.insertSongList(songList)
            for (i in playListInfo.entries) {
//                val thumbnail = getThumbnail(i.id)?:i.thumbnails.last().url
                val thumbnail = i.thumbnails.last().url
                val song = Song(
                    0L,
                    i.title,
                    i.uploader,
                    thumbnail,
                    "",
                    (i.duration * 1000).toInt(),
                    isBuffered = NOT_BUFFERED,
                    youtubeId = i.id
                )
                if (!DataBaseUtils.isYoutubeIdExist(i.id)) {
                    DataBaseUtils.insertSong(song)
                }
                val songId = DataBaseUtils.querySongIdByYoutubeId(i.id)
                DataBaseUtils.insertRef(PlaylistSongCrossRef(listId, songId))

                _loadState.update {
                    it.copy(
                        loadState = io.github.mumu12641.lark.network.LoadState.Loading(
                            (playListInfo.entries.indexOf(
                                i
                            ) + 1).toString()
                        )
                    )
                }

            }
            _loadState.update {
                it.copy(loadState = io.github.mumu12641.lark.network.LoadState.Success("0"))
            }
        }
    }

    fun getNeteaseSongList(id: Long) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, _ ->
            _loadState.update { state ->
                state.copy(loadState = io.github.mumu12641.lark.network.LoadState.Fail("0"))
            }
        }
        ) {
            _loadState.update {
                it.copy(loadState = io.github.mumu12641.lark.network.LoadState.Loading("0"))
            }
            val list = Repository.getNeteaseSongList(id)
            val tracks = Repository.getNeteaseSongListTracks(id)
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
            _loadState.update {
                it.copy(num = tracks.songs.size)
            }
            for (i in tracks.songs) {
                val song = Song(
                    0L,
                    i.name,
                    i.ar.joinToString(",") { it.name },
                    i.al.picUrl,
                    EMPTY_URI + i.al.picUrl,
                    i.dt,
                    neteaseId = i.id.toLong(),
                    isBuffered = NOT_BUFFERED,
                )
                if (!DataBaseUtils.isNeteaseIdExist(i.id.toLong())) {
                    DataBaseUtils.insertSong(song)
                }
                val songId = DataBaseUtils.querySongIdByNeteaseId(i.id.toLong())
                DataBaseUtils.insertRef(PlaylistSongCrossRef(listId, songId))

                _loadState.update {
                    it.copy(
                        loadState = io.github.mumu12641.lark.network.LoadState.Loading(
                            (tracks.songs.indexOf(
                                i
                            ) + 1).toString()
                        )
                    )
                }
            }

            _loadState.update {
                it.copy(loadState = io.github.mumu12641.lark.network.LoadState.Success("0"))
            }
        }
    }

}
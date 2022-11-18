package io.github.mumu12641.lark.ui.theme.page.details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mumu12641.lark.entity.*
import io.github.mumu12641.lark.network.LoadState
import io.github.mumu12641.lark.network.Repository
import io.github.mumu12641.lark.room.DataBaseUtils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SongListDetailsViewModel"
@HiltViewModel
class SongListDetailsViewModel @Inject constructor() : ViewModel() {

    private val _songListDetailUiState = MutableStateFlow(SongListDetailUiState())
    val songListDetailUiState = _songListDetailUiState

    private val _loadState = MutableStateFlow<LoadState>(LoadState.None())
    val loadState = _loadState

    fun initData(id: Long) {
        viewModelScope.launch(coroutineExceptionHandler) {
            _songListDetailUiState.update {
                it.copy(isLoading = true, currentSongListId = id)
            }
            _songListDetailUiState.update { songListDetailUiState ->
                songListDetailUiState.copy(
                    songs = DataBaseUtils.querySongListWithSongsBySongListIdFlow(id).map {
                        it.songs
                    },
                    songList = DataBaseUtils.querySongListFlowById(id)
                )
            }
            _songListDetailUiState.update {
                it.copy(isLoading = false, currentSongListId = id)
            }
        }
    }

    fun updateSongListDescription(description: String) {
        viewModelScope.launch(handleIOExceptionContext) {
            DataBaseUtils.updateSongList(
                DataBaseUtils.querySongListById(_songListDetailUiState.value.currentSongListId)
                    .copy(description = description)
            )
        }
    }

    fun changeSongListImage(uri: String) {
        viewModelScope.launch(handleIOExceptionContext) {
            DataBaseUtils.updateSongList(
                DataBaseUtils.querySongListById(_songListDetailUiState.value.currentSongListId)
                    .copy(imageFileUri = uri)
            )
        }
    }

    fun deletePlaylistSongCrossRef(songId: Long) {
        viewModelScope.launch(handleIOExceptionContext) {
            DataBaseUtils.deleteRef(_songListDetailUiState.value.currentSongListId, songId)
        }
    }

    fun refreshNeteaseSongList() {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, e ->
            _loadState.value = LoadState.Fail("")
            Log.d(TAG, "refreshNeteaseSongList: " + e.message)
        }) {
            val songListId = songListDetailUiState.value.currentSongListId
            val id = DataBaseUtils.queryNeteaseIdBySongListId(songListId)
            Log.d(TAG, "refreshNeteaseSongList: $songListId + $id")

            _loadState.value = LoadState.Loading("")
            val songList = Repository.getNeteaseSongList(id).playlist

            val tracks = Repository.getNeteaseSongListTracks(id)
            DataBaseUtils.updateSongList(
                DataBaseUtils.querySongListById(songListId).copy(
                    songListTitle = songList.name,
                    imageFileUri = songList.coverImgUrl,
                    description = songList.description.toString(),
                    songNumber = tracks.songs.size,
                )
            )
            val songIds =
                DataBaseUtils.querySongListWithSongsBySongListId(songListId).songs.map { it.neteaseId }
            for (i in tracks.songs) {
                if (!songIds.contains(i.id.toLong())) {
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
                    DataBaseUtils.insertRef(PlaylistSongCrossRef(songListId, songId))
                }
            }
            _loadState.value = LoadState.Success("")
        }
    }

    data class SongListDetailUiState(
        val currentSongListId: Long = 1L,
        val isLoading: Boolean = true,
        val songs: Flow<List<Song>> = emptyFlow(),
        val songList: Flow<SongList> = emptyFlow()
    )
}
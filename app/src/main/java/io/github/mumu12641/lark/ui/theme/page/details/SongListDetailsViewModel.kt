package io.github.mumu12641.lark.ui.theme.page.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mumu12641.lark.entity.Song
import io.github.mumu12641.lark.entity.SongList
import io.github.mumu12641.lark.room.DataBaseUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongListDetailsViewModel @Inject constructor() : ViewModel() {

    private val _songListDetailUiState = MutableStateFlow(SongListDetailUiState())
    val songListDetailUiState = _songListDetailUiState

    fun initData(id: Long) {
        viewModelScope.launch {
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
        viewModelScope.launch(Dispatchers.IO) {
            DataBaseUtils.updateSongList(
                DataBaseUtils.querySongListById(_songListDetailUiState.value.currentSongListId)
                    .copy(description = description)
            )
        }
    }

    fun changeSongListImage(uri: String) {
        viewModelScope.launch(Dispatchers.IO) {
            DataBaseUtils.updateSongList(
                DataBaseUtils.querySongListById(_songListDetailUiState.value.currentSongListId)
                    .copy(imageFileUri = uri)
            )
        }
    }

    data class SongListDetailUiState(
        val currentSongListId: Long = 1L,
        val isLoading: Boolean = true,
        val songs: Flow<List<Song>> = emptyFlow(),
        val songList: Flow<SongList> = emptyFlow()
    )
}
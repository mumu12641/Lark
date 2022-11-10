package io.github.mumu12641.lark.ui.theme.page.artist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mumu12641.lark.entity.ARTIST_SONGLIST_TYPE
import io.github.mumu12641.lark.entity.Song
import io.github.mumu12641.lark.entity.SongList
import io.github.mumu12641.lark.network.LoadState
import io.github.mumu12641.lark.network.Repository
import io.github.mumu12641.lark.room.DataBaseUtils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor() : ViewModel() {


    private val _artistUiState = MutableStateFlow(ArtistUiState())
    val artistUiState = _artistUiState

    private val _allArtistUiState = MutableStateFlow(AllArtistUiState())
    val allArtistUiState = _allArtistUiState

    fun initData(id: Long) {
        showLoading()
        viewModelScope.launch {
            _artistUiState.update {
                it.copy(
                    currentSongListId = 1L,
                    songList = null,
                    songs = emptyList()
                )
            }
            _artistUiState.update {
                it.copy(
                    currentSongListId = id,
                    songList = DataBaseUtils.querySongListById(id),
                    songs = DataBaseUtils.querySongListWithSongsBySongListId(id).songs
                )
            }
            hideLoading()
        }

    }


    private fun showLoading() {
        _artistUiState.update {
            it.copy(loadState = LoadState.Loading())
        }
    }

    private fun hideLoading() {
        _artistUiState.update {
            it.copy(loadState = LoadState.Success())
        }
    }

    private fun loadFail(s: String) {
        _artistUiState.update {
            it.copy(loadState = LoadState.Fail(s))
        }
    }

    fun updateArtistDetail(keywords: String) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, e ->
            loadFail(e.message ?: "Fail")
        }) {
            showLoading()
            val artistId =
                Repository.getSearchArtistResponse(keywords).result.artists[0].artistId
            artistId?.let { it ->
                val artistDetails =
                    Repository.getArtistDetail(it).data.artist
                DataBaseUtils.updateSongList(
                    DataBaseUtils.querySongListById(_artistUiState.value.currentSongListId).copy(
                        imageFileUri = artistDetails.cover,
                        description = artistDetails.briefDesc
                    )
                )
                _artistUiState.update { state ->
                    state.copy(songList = DataBaseUtils.querySongListById(state.currentSongListId))
                }
                hideLoading()
            }
            if (artistId == null) {
                loadFail("Fail Null")
            }
        }
    }

    data class AllArtistUiState(
        val artistSongList: Flow<List<SongList>> = DataBaseUtils.queryAllSongList().map {
            it.filter { songList ->
                songList.type == ARTIST_SONGLIST_TYPE
            }.sortedByDescending { list ->
                list.songNumber
            }
        },
    )

    data class ArtistUiState(
        var currentSongListId: Long = 1L,
        var songList: SongList? = null,
        var songs: List<Song> = emptyList(),
        val loadState: LoadState = LoadState.Loading()
    )

}
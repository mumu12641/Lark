package io.github.mumu12641.lark.ui.theme.page.artist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mumu12641.lark.entity.ARTIST_SONGLIST_TYPE
import io.github.mumu12641.lark.entity.LoadState
import io.github.mumu12641.lark.network.NetworkCreator
import io.github.mumu12641.lark.room.DataBaseUtils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor() : ViewModel() {

    private val TAG = "ArtistViewModel"

    val artistSongList = DataBaseUtils.queryAllSongList().map {
        it.filter { songList ->
            songList.type == ARTIST_SONGLIST_TYPE
        }.sortedByDescending { list ->
            list.songNumber
        }
    }

    var currentSongListId = 1L
    val songList
        get() = DataBaseUtils.querySongListFlowByIdType(
            currentSongListId,
            ARTIST_SONGLIST_TYPE
        )
    val songs
        get() =
            DataBaseUtils.querySongListWithSongsBySongListIdFlow(currentSongListId).map {
                it.songs
            }

    fun refreshId(id: Long) {
        currentSongListId = id
    }

    private val _loadState = MutableStateFlow<LoadState>(LoadState.None())
    val loadState = _loadState

    fun setStateToNone() {
        _loadState.value = LoadState.None()
    }

    fun updateArtistDetail(keywords: String) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, e ->
            _loadState.value = LoadState.Fail(e.message ?: "Load Fail")
            Log.d(TAG, "updateArtistDetail: " + e.message)
        }) {
            _loadState.value = LoadState.Loading()
            val artistId =
                NetworkCreator.networkService.getSearchArtistResponse(keywords).result.artists[0].artistId
            artistId?.let {
                val artistDetails =
                    NetworkCreator.networkService.getArtistDetail(it).data.artist
                DataBaseUtils.updateSongList(
                    DataBaseUtils.querySongListById(currentSongListId).copy(
                        imageFileUri = artistDetails.cover,
                        description = artistDetails.briefDesc
                    )
                )
                _loadState.value = LoadState.Success()
            }
            if (artistId == null) {
                _loadState.value = LoadState.Fail("artistId == null")
            }
        }
    }
}
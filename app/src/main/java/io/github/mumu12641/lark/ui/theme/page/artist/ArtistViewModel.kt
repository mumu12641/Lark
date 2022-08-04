package io.github.mumu12641.lark.ui.theme.page.artist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mumu12641.lark.entity.ARTIST_SONGLIST_TYPE
import io.github.mumu12641.lark.entity.LoadState
import io.github.mumu12641.lark.entity.Song
import io.github.mumu12641.lark.entity.SongList
import io.github.mumu12641.lark.network.NetworkCreator
import io.github.mumu12641.lark.room.DataBaseUtils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor() : ViewModel() {

    private  val TAG = "ArtistViewModel"

    val artistSongList = DataBaseUtils.queryAllSongList().map {
        it.filter { songList ->
            songList.type == ARTIST_SONGLIST_TYPE
        }
    }

    var currentSongListId = 1L
    var songList: Flow<SongList> = DataBaseUtils.querySongListFlowById(currentSongListId)
    var songs: Flow<List<Song>> =
        DataBaseUtils.querySongListWithSongsBySongListIdFlow(currentSongListId).map {
            it.songs
        }

    fun refreshId(id: Long) {
        currentSongListId = id
        songList = DataBaseUtils.querySongListFlowById(id)
        songs = DataBaseUtils.querySongListWithSongsBySongListIdFlow(id).map {
            it.songs
        }
    }

    private val _loadState = MutableStateFlow<LoadState>(LoadState.None())
    val loadState = _loadState

    fun updateArtistDetail(keywords: String) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, e ->
            _loadState.value = LoadState.Fail(e.message ?: "加载失败")
            Log.d(TAG, "updateArtistDetail: " + e.message)
        }) {
            _loadState.value = LoadState.Loading()
            Log.d(TAG, "updateArtistDetail: loading")
            val artistId =
                NetworkCreator.networkService.getSearchArtistResponse(keywords).result.artists[0].artistId
            val artistDetails =
                NetworkCreator.networkService.getArtistDetail(artistId!!).data.artist
            Log.d(TAG, "updateArtistDetail: success")
            _loadState.value = LoadState.Success()
            songList.collect {
                DataBaseUtils.updateSongList(
                    it.copy(
                        imageFileUri = artistDetails.cover,
                        description = artistDetails.briefDesc
                    )
                )

            }

        }
    }

}
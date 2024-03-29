package io.github.mumu12641.lark.ui.theme.page.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mumu12641.lark.entity.*
import io.github.mumu12641.lark.network.LoadState
import io.github.mumu12641.lark.network.NetworkCreator.networkService
import io.github.mumu12641.lark.room.DataBaseUtils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {

    private val _hotSearchWord = MutableStateFlow<List<HotData>>(emptyList())
    val hotSearchWord = _hotSearchWord
    private val _loadState = MutableStateFlow<LoadState>(LoadState.None())
    val loadState = _loadState
    private val _searchSongList = MutableStateFlow<List<Song>>(emptyList())
    val searchSongList = _searchSongList
    private val _currentShowSong = MutableStateFlow(INIT_SONG)
    val currentShowSong = _currentShowSong

    val allSongList: Flow<List<SongList>> = DataBaseUtils.queryAllSongList().map { list ->
        list.filter {
            it.type in 1 until ARTIST_SONGLIST_TYPE
        }
    }

    data class HotData(
        val hotSearchWord: String,
        val hotScore: Int
    )

    fun setCurrentShowSong(song: Song) {
        _currentShowSong.value = song
    }

    fun searchSongResponse(keywords: String) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, e ->
            _loadState.value = LoadState.Fail(e.message!!)
        }) {
            _loadState.value = LoadState.Loading()
            val response = networkService.getSearchSongResponse(keywords)
            _searchSongList.value = response.result.songs.map {
                Song(
                    0L,
                    it.name,
                    it.ar.joinToString(",") { ar -> ar.name },
                    it.al.picUrl,
                    EMPTY_URI + it.al.picUrl,
                    duration = if (it.privilege.fee == 1) 30000 else it.dt,
                    neteaseId = it.id.toLong(),
                    isBuffered = NOT_BUFFERED,
                    vip = it.privilege.fee == 1
                )
            }
            _loadState.value = LoadState.Success()
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val hot = networkService.getSearchHot()
            _hotSearchWord.value = hot.data.map {
                HotData(it.searchWord, it.score)
            }
        }
    }
}
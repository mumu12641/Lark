package io.github.mumu12641.lark.ui.theme.page.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mumu12641.lark.entity.Song
import io.github.mumu12641.lark.entity.SongList
import io.github.mumu12641.lark.room.DataBaseUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongListDetailsViewModel @Inject constructor() : ViewModel() {

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

    fun updateSongListDescription(description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            DataBaseUtils.updateSongList(
                DataBaseUtils.querySongListById(currentSongListId).copy(description = description)
            )
        }
    }

    fun changeSongListImage(uri: String) {
        viewModelScope.launch(Dispatchers.IO) {
            songList.collect {
                DataBaseUtils.updateSongList(it.copy(imageFileUri = uri))
            }
        }
    }

}
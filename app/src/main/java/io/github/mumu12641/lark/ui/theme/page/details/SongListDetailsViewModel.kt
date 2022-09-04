package io.github.mumu12641.lark.ui.theme.page.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mumu12641.lark.room.DataBaseUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongListDetailsViewModel @Inject constructor() : ViewModel() {

    private var currentSongListId = 1L
    val songList get() = DataBaseUtils.querySongListFlowById(currentSongListId)

    val songs
        get() = DataBaseUtils.querySongListWithSongsBySongListIdFlow(currentSongListId).map {
            it.songs
        }

    fun refreshId(id: Long) {
        currentSongListId = id
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
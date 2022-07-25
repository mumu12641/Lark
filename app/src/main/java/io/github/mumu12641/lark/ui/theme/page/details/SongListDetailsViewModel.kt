package io.github.mumu12641.lark.ui.theme.page.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.mumu12641.lark.entity.SongList
import io.github.mumu12641.lark.room.DataBaseUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SongListDetailsViewModel:ViewModel() {
    private val _songList = MutableStateFlow<SongList?>(null)
    val songList = _songList


    fun getSongList(songListId:Long) = run {
        runBlocking (Dispatchers.IO){
            _songList.value = DataBaseUtils.querySongListById(songListId)
        }
    }

    fun changeSongListImage(uri:String){
        viewModelScope.launch (Dispatchers.IO) {
            _songList.value?.let {
                _songList.value = it.copy(imageFileUri = uri)
                DataBaseUtils.updateSongList(it.copy(imageFileUri = uri))
            }
        }
    }
}
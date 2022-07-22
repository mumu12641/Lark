package io.github.mumu12641.lark.ui.theme.page.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.mumu12641.lark.room.DataBaseUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainViewModel:ViewModel() {

    val allSongList = DataBaseUtils.queryAllSongList().map { it ->
        it.filter {
            songList -> songList .type>0
        }
    }

    private  val TAG = "MainViewModel"

    fun addSongList(){
        viewModelScope.launch (Dispatchers.IO) {

        }
    }
}
package io.github.mumu12641.lark.ui.theme.page

import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.mumu12641.lark.entity.SongList
import io.github.mumu12641.lark.room.DataBaseUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel:ViewModel() {
    val allSongList = DataBaseUtils.queryAllSongList()

    fun addSongList(){
        viewModelScope.launch {
            DataBaseUtils.insertSongList(
                SongList(0L,"test","test",0,"test","test")
            )
        }
    }
}
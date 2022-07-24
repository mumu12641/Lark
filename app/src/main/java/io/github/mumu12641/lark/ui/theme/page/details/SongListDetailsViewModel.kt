package io.github.mumu12641.lark.ui.theme.page.details

import android.util.Log
import androidx.lifecycle.ViewModel
import io.github.mumu12641.lark.entity.Load
import io.github.mumu12641.lark.entity.SongList
import io.github.mumu12641.lark.room.DataBaseUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

class SongListDetailsViewModel:ViewModel() {
    private val _songList = MutableStateFlow<SongList?>(null)
    val songList = _songList

    private val _loadState = MutableStateFlow(Load.NONE)
    val loadState = _loadState

    fun getSongList(songListId:Long) = run {
        runBlocking (Dispatchers.IO){
            _loadState.value = Load.LOADING
            _songList.value = DataBaseUtils.querySongListById(songListId)
            _loadState.value = Load.SUCCESS
            Log.d("TAG", "getSongList: " + songList.value)
        }
    }
}
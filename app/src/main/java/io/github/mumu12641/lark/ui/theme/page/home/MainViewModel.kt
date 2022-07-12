package io.github.mumu12641.lark.ui.theme.page.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.mumu12641.lark.entity.SongList
import io.github.mumu12641.lark.room.DataBaseUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel:ViewModel() {
    val allSongList = DataBaseUtils.queryAllSongList()

    private  val TAG = "MainViewModel"

    fun addSongList(){
        viewModelScope.launch (Dispatchers.IO) {
            DataBaseUtils.insertSongList(
                SongList(0L,"test","test",0,"test","test")
            )
            Log.d(TAG, "addSongList: done")
        }
    }
}
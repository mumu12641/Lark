package io.github.mumu12641.lark.ui.theme.page.home

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
//            DataBaseUtils.insertSongList(
//                SongList(0L,"Local","2022/7/14",0,"Local Music","Local Image")
//            )
//            DataBaseUtils.insertSongList(
//                SongList(0L,"I Like ","2022/7/14",0,"I like","Like Image")
//            )
            DataBaseUtils.insertSongList(
                SongList(0L,"History","2022/7/14",0,"History Music","History Image")
            )
//            Log.d(TAG, "addSongList: done")
        }
    }
}
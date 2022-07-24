package io.github.mumu12641.lark.ui.theme.page.home

import android.content.ComponentName
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.mumu12641.lark.MainActivity.Companion.context
import io.github.mumu12641.lark.entity.CHANGE_PLAY_LIST
import io.github.mumu12641.lark.entity.SongList
import io.github.mumu12641.lark.room.DataBaseUtils
import io.github.mumu12641.lark.service.MediaPlaybackService
import io.github.mumu12641.lark.service.MediaServiceConnection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainViewModel:ViewModel() {

    val currentPlayMetadata by lazy { mediaServiceConnection.playMetadata }

    val currentPlayState by lazy { mediaServiceConnection.playState }

    private val mediaServiceConnection: MediaServiceConnection = MediaServiceConnection.getInstance(context,
        ComponentName(context,MediaPlaybackService::class.java)
    )

    val allSongList = DataBaseUtils.queryAllSongList().map {
        it.filter {
            songList -> songList .type > 0
        }
    }

    fun addSongList(songList: SongList){
        viewModelScope.launch (Dispatchers.IO) {
            DataBaseUtils.insertSongList(songList)
        }
    }

    fun playMedia(){
//        if (mediaServiceConnection.playState.value.state == PlaybackStateCompat.STATE_PLAYING){
//            mediaServiceConnection.transportControls.pause()
//        }else {
//            mediaServiceConnection.transportControls.play()
//        }
        val bundle = Bundle()
        bundle.apply {
            putLong("songListId",1L)
            putLong("songId",1L)
        }
        mediaServiceConnection.transportControls.sendCustomAction(CHANGE_PLAY_LIST,bundle )
    }
}
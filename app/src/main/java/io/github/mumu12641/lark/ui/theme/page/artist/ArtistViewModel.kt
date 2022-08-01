package io.github.mumu12641.lark.ui.theme.page.artist

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mumu12641.lark.entity.ARTIST_SONGLIST_TYPE
import io.github.mumu12641.lark.room.DataBaseUtils
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor() : ViewModel() {
    val artistSongList = DataBaseUtils.queryAllSongList().map {
        it.filter { songList ->
            songList.type == ARTIST_SONGLIST_TYPE
        }
    }
}
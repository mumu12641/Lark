package io.github.mumu12641.lark.ui.theme.page.artist

import android.provider.ContactsContract
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mumu12641.lark.entity.ARTIST_SONGLIST_TYPE
import io.github.mumu12641.lark.entity.Song
import io.github.mumu12641.lark.entity.SongList
import io.github.mumu12641.lark.network.NetworkCreator
import io.github.mumu12641.lark.room.DataBaseUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor() : ViewModel() {

    val artistSongList = DataBaseUtils.queryAllSongList().map {
        it.filter { songList ->
            songList.type == ARTIST_SONGLIST_TYPE
        }
    }

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

    fun updateArtistDetail(keywords:String){
        viewModelScope.launch(Dispatchers.IO) {
            val artistId = NetworkCreator.networkService.getSearchArtistResponse(keywords).result.artists[0].artistId
            val artistDetails = NetworkCreator.networkService.getArtistDetail(artistId!!).data.artist
//            _artistDetailState.value = ArtistState(artistDetails.cover,artistDetails.name,artistDetails.briefDesc)
            songList.collect{
                DataBaseUtils.updateSongList(it.copy(imageFileUri = artistDetails.cover, description = artistDetails.briefDesc))
            }
        }
    }

}
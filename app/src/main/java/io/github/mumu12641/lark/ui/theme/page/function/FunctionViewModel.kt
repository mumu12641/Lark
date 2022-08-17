package io.github.mumu12641.lark.ui.theme.page.function

import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mumu12641.lark.BaseApplication.Companion.context
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.*
import io.github.mumu12641.lark.room.DataBaseUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class FunctionViewModel @Inject constructor() : ViewModel() {

    val allSongList = DataBaseUtils.queryAllSongList().map { list ->
        list.filter {
            it.type in 1 until ARTIST_SONGLIST_TYPE
        }
    }

    val localMusicList = DataBaseUtils.querySongListWithSongsBySongListIdFlow(LocalSongListId).map {
        it.songs
    }

    val historySongList =
        DataBaseUtils.querySongListWithSongsBySongListIdFlow(HistorySongListId).map {
            it.songs.sortedByDescending {
                    song -> song.recentPlay
            }
        }

    private val _loadState = MutableStateFlow(Load.NONE)
    val loadLocal: StateFlow<Int> = _loadState

    private val _currentShowSong = MutableStateFlow(INIT_SONG)
    val currentShowSong: StateFlow<Song?> = _currentShowSong


    fun changeCurrentShowSong(song: Song) {
        _currentShowSong.value = song
    }


    @SuppressLint("Range", "Recycle")
    fun reFreshLocalMusicList() {
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.value = Load.LOADING
            Log.d(TAG, "reFreshLocalMusicList: $loadLocal")
            val allRef: List<PlaylistSongCrossRef> = DataBaseUtils.queryAllRef()
            val allMediaFileUri = DataBaseUtils.queryAllMediaFileUri()
            val cursor: Cursor? = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                MediaStore.Audio.AudioColumns.IS_MUSIC
            )
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    if (cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)) > 100 * 1000) {
                        var id = 0L
                        val song = Song(
                            id,
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                            getAlbumImageUri(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))).toString(),
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                        )
                        if (allMediaFileUri.isEmpty() || !allMediaFileUri.contains(song.mediaFileUri)) {
                            id = DataBaseUtils.insertSong(song)
                            DataBaseUtils.insertRef(PlaylistSongCrossRef(LocalSongListId, id))
                        } else {
                            if (!allRef.contains(
                                    PlaylistSongCrossRef(
                                        LocalSongListId,
                                        DataBaseUtils.querySongIdByMediaUri(song.mediaFileUri)
                                    )
                                )
                            ) {
                                DataBaseUtils.insertRef(
                                    PlaylistSongCrossRef(
                                        LocalSongListId,
                                        DataBaseUtils.querySongIdByMediaUri(song.mediaFileUri)
                                    )
                                )
                            }
                        }
                    }
                }
            }

            DataBaseUtils.updateSongList(
                DataBaseUtils.querySongListById(LocalSongListId).copy(
                    songNumber = DataBaseUtils.querySongListWithSongsBySongListId(
                        LocalSongListId
                    ).songs.size
                )
            )
            refreshArtist()
            delay(2000)
            _loadState.value = Load.SUCCESS
        }
    }

    companion object {
        private const val TAG = "FunctionViewModel"
    }

    private fun refreshArtist() {
        viewModelScope.launch(Dispatchers.IO) {
            val songs = DataBaseUtils.queryAllSong()
            for (i in songs) {
                if (!DataBaseUtils.isSongListExist(i.songSinger, ARTIST_SONGLIST_TYPE)) {
                    DataBaseUtils.insertSongList(
                        SongList(
                            0L, i.songSinger, "xxx", 0, context.getString(
                                R.string.no_description_text
                            ), "111", ARTIST_SONGLIST_TYPE
                        )
                    )
                }
                val songListId = DataBaseUtils.querySongListId(
                    i.songSinger,
                    ARTIST_SONGLIST_TYPE
                )
                if (!DataBaseUtils.isRefExist(songListId, i.songId)) {
                    DataBaseUtils.insertRef(
                        PlaylistSongCrossRef(
                            songListId, i.songId
                        )
                    )
                }
            }
        }
    }


}

fun getAlbumImageUri(id: Long): Uri {
    val sArtworkUri = Uri.parse("content://media/external/audio/albumart")
    return Uri.withAppendedPath(sArtworkUri, id.toString())
}


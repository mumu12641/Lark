package io.github.mumu12641.lark.ui.theme.page.function

import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.mumu12641.lark.BaseApplication.Companion.context
import io.github.mumu12641.lark.entity.Load
import io.github.mumu12641.lark.entity.LocalSongListId
import io.github.mumu12641.lark.entity.PlaylistSongCrossRef
import io.github.mumu12641.lark.entity.Song
import io.github.mumu12641.lark.room.DataBaseUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class FunctionViewModel:ViewModel() {

    val localMusicList = DataBaseUtils.querySongListWithSongsBySongListId(LocalSongListId).map {
        it.songs
    }

    private val _loadState = MutableStateFlow(Load.NONE)
    val loadLocal:StateFlow<Int> = _loadState

    @SuppressLint("Range", "Recycle")
    fun reFreshLocalMusicList(){
        viewModelScope.launch(Dispatchers.IO){
            _loadState.value = Load.LOADING
            Log.d(TAG, "reFreshLocalMusicList: $loadLocal")
            val allRef : List<PlaylistSongCrossRef> = DataBaseUtils.queryAllRef()
            val allMediaFileUri = DataBaseUtils.queryAllMediaFileUri()
            val cursor: Cursor? = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                MediaStore.Audio.AudioColumns.IS_MUSIC
            )
            if (cursor != null){
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
                        if (allMediaFileUri.isEmpty() || !allMediaFileUri.contains(song.mediaFileUri)){
                            id = DataBaseUtils.insertSong(song)
                            DataBaseUtils.insertRef(PlaylistSongCrossRef(LocalSongListId,id))
                        }else{
                            if (!allRef.contains(PlaylistSongCrossRef(LocalSongListId,DataBaseUtils.querySongIdByMediaUri(song.mediaFileUri)))){
                                DataBaseUtils.insertRef(PlaylistSongCrossRef(LocalSongListId,DataBaseUtils.querySongIdByMediaUri(song.mediaFileUri)))
                            }
                        }
                    }
                }
            }
            delay(2000)
            _loadState.value = Load.SUCCESS
        }
    }

    companion object {
        private const val TAG = "FunctionViewModel"
    }


}
fun getAlbumImageUri(id: Long): Uri {
    val sArtworkUri = Uri.parse("content://media/external/audio/albumart")
    return Uri.withAppendedPath(sArtworkUri, id.toString())
}

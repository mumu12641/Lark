package io.github.mumu12641.lark.ui.theme.page.function

import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.mumu12641.lark.BaseApplication.Companion.context
import io.github.mumu12641.lark.entity.LocalSongListId
import io.github.mumu12641.lark.entity.PlaylistSongCrossRef
import io.github.mumu12641.lark.entity.Song
import io.github.mumu12641.lark.room.DataBaseUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class FunctionViewModel:ViewModel() {

    private val TAG = "FunctionViewModel"

    val localMusicList = DataBaseUtils.querySongListWithSongsBySongListId(LocalSongListId).map {
        it.songs
    }


    class LocalMusicDataSource(
        private val fetchLocalMusicApi:FetchLocalMusicApi
    ){
        val localMusic: Flow<List<Song>> = flow {
            val data = fetchLocalMusicApi.fetchLocalMusic()
            emit(data)
        }
    }

    interface FetchLocalMusicApi{
        @SuppressLint("Range", "Recycle")
        suspend fun fetchLocalMusic():List<Song>{
            val localMusic = mutableListOf<Song>()
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
                        val song = Song(
                            0L,
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                            getAlbumImageUri(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))).toString(),
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                        )
                        localMusic.add(song)
                    }
                }
            }
            return localMusic.toList()
        }
    }

    @SuppressLint("Recycle", "Range")
    fun getLocalMusic(): Flow<List<Song>> {
        val localMusicDataSource = LocalMusicDataSource(object:FetchLocalMusicApi{})
        return localMusicDataSource.localMusic
    }

    @SuppressLint("Range", "Recycle")
    fun reFreshLocalMusicList(){
        viewModelScope.launch(Dispatchers.IO){

            Log.d(TAG, "reFreshLocalMusicList: start")
            
            val allRef : List<PlaylistSongCrossRef> = DataBaseUtils.queryAllRef()
            Log.d(TAG, "reFreshLocalMusicList: $allRef")
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
                            id = DataBaseUtils.querySongIdByMediaUri(song.mediaFileUri)
                            if (!allRef.contains(PlaylistSongCrossRef(LocalSongListId,id))){
                                DataBaseUtils.insertRef(PlaylistSongCrossRef(LocalSongListId,id))
                            }
                        }
                    }
                }
            }
            Log.d(TAG, "reFreshLocalMusicList: end")
        }
    }


}
fun getAlbumImageUri(id: Long): Uri {
    val sArtworkUri = Uri.parse("content://media/external/audio/albumart")
    return Uri.withAppendedPath(sArtworkUri, id.toString())
}

package io.github.mumu12641.lark.ui.theme.page.function

import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import io.github.mumu12641.lark.BaseApplication.Companion.context
import io.github.mumu12641.lark.entity.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class FunctionViewModel:ViewModel() {

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
//    fun collectLocalMusic(){
//        viewModelScope.launch {
//            val localMusicDataSource = LocalMusicDataSource(object:FetchLocalMusicApi{})
//            localMusicDataSource.localMusic.collect{
//
//            }
//        }
//    }

}
fun getAlbumImageUri(id: Long): Uri {
    val sArtworkUri = Uri.parse("content://media/external/audio/albumart")
    return Uri.withAppendedPath(sArtworkUri, id.toString())
}

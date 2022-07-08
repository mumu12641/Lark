package io.github.mumu12641.lark.room

import io.github.mumu12641.lark.entity.PlaylistSongCrossRef
import io.github.mumu12641.lark.entity.Song
import io.github.mumu12641.lark.entity.SongList
import kotlinx.coroutines.flow.Flow

class DataBaseUtils {
    companion object{
        private val DataBase : MusicDataBase = MusicDataBase.getInstance()
        private val musicDao : MusicDao = DataBase.musicDao

        fun queryAllSong(): Flow<List<Song>> {
            return musicDao.queryAllSong()
        }

        suspend fun insertSong(song: Song){
            musicDao.insertSong(song)
        }


        fun queryAllSongList():Flow<List<SongList>>{
            return musicDao.queryAllSongList()
        }

        suspend fun insertSongList(songList: SongList){
            musicDao.insertSongList(songList)
        }

        suspend fun insertRef(playlistSongCrossRef: PlaylistSongCrossRef){
            musicDao.insertRef(playlistSongCrossRef)
        }


    }

}
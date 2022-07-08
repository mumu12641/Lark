package io.github.mumu12641.lark.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.github.mumu12641.lark.entity.PlaylistSongCrossRef
import io.github.mumu12641.lark.entity.Song
import io.github.mumu12641.lark.entity.SongList
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {

    @Insert
    suspend fun insertSong(song: Song)

    @Query("SELECT * FROM Song")
    fun queryAllSong():Flow<List<Song>>

    @Insert
    suspend fun insertSongList(songList: SongList)

    @Query("SELECT * FROM SongList")
    fun queryAllSongList():Flow<List<SongList>>

    @Insert
    suspend fun insertRef(playlistSongCrossRef: PlaylistSongCrossRef)

    @Query("SELECT * FROM playlistsongcrossref")
    fun queryAllRef():Flow<List<PlaylistSongCrossRef>>


}
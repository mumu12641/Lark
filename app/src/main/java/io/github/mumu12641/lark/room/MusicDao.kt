package io.github.mumu12641.lark.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import io.github.mumu12641.lark.entity.PlaylistSongCrossRef
import io.github.mumu12641.lark.entity.Song
import io.github.mumu12641.lark.entity.SongList
import io.github.mumu12641.lark.entity.SongListWithSongs
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {

    @Insert
    suspend fun insertSong(song: Song):Long

    @Insert
    suspend fun insertSongList(songList: SongList)

    @Insert
    suspend fun insertRef(playlistSongCrossRef: PlaylistSongCrossRef)


    @Query("SELECT * FROM Song")
    fun queryAllSong():Flow<List<Song>>
    @Query("SELECT * FROM Song WHERE songId = :songId")
    suspend fun querySongById(songId:Long):Song
    @Query("SELECT songId FROM Song WHERE mediaFileUri = :mediaFileUri")
    suspend fun querySongIdByMediaUri(mediaFileUri:String):Long

    @Query("SELECT * FROM SongList")
    fun queryAllSongList():Flow<List<SongList>>
    @Query("SELECT * FROM SongList WHERE songListId = :songListId")
    suspend fun querySongListById(songListId: Long):SongList

    @Query("SELECT * FROM playlistsongcrossref")
    fun queryAllRef():List<PlaylistSongCrossRef>

    @Transaction
    @Query("SELECT * FROM SongList WHERE songListId = :songListId")
    fun querySongListWithSongsBySongListIdFlow(songListId:Long):Flow<SongListWithSongs>
    @Query("SELECT * FROM SongList WHERE songListId = :songListId")
    suspend fun querySongListWithSongsBySongListId(songListId:Long):SongListWithSongs

    @Query("SELECT mediaFileUri FROM Song")
    suspend fun queryAllMediaFileUri():List<String>


}
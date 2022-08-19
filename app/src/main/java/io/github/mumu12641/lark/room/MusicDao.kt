package io.github.mumu12641.lark.room

import androidx.room.*
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
    suspend fun queryAllSong():List<Song>
    @Query("SELECT * FROM Song WHERE songId = :songId")
    suspend fun querySongById(songId:Long):Song
    @Query("SELECT songId FROM Song WHERE mediaFileUri = :mediaFileUri")
    suspend fun querySongIdByMediaUri(mediaFileUri:String):Long
    @Query("SELECT songId FROM Song WHERE neteaseId = :neteaseId")
    suspend fun querySongIdByNeteaseId(neteaseId:Long):Long
    @Query("SELECT EXISTS(SELECT * FROM song WHERE neteaseId = :neteaseId)")
    suspend fun isNeteaseIdExist(neteaseId: Long) : Boolean

    @Query("SELECT * FROM SongList")
    fun queryAllSongList():Flow<List<SongList>>

    @Query("SELECT * FROM SongList WHERE songListId = :songListId")
    fun querySongListFlowById(songListId: Long):Flow<SongList>
    @Query("SELECT * FROM SongList WHERE songListId = :songListId")
    suspend fun querySongListById(songListId: Long):SongList
    @Query("SELECT EXISTS(SELECT * FROM songlist WHERE songListTitle = :title AND type = :type)")
    suspend fun isSongListExist(title:String,type:Int):Boolean
    @Query("SELECT songListId FROM songlist WHERE songListTitle = :title AND type = :type")
    suspend fun querySongListId(title:String,type:Int):Long
    @Query("SELECT * FROM songlist WHERE type = :type")
    suspend fun querySongListsByType(type: Int):List<SongList>


    @Query("SELECT * FROM playlistsongcrossref")
    fun queryAllRef():List<PlaylistSongCrossRef>
    @Query("SELECT EXISTS(SELECT * FROM playlistsongcrossref WHERE songListId = :songListId AND songId = :songId)")
    suspend fun isRefExist(songListId: Long,songId: Long) : Boolean

    @Transaction
    @Query("SELECT * FROM SongList WHERE songListId = :songListId")
    fun querySongListWithSongsBySongListIdFlow(songListId:Long):Flow<SongListWithSongs>
    @Query("SELECT * FROM SongList WHERE songListId = :songListId")
    suspend fun querySongListWithSongsBySongListId(songListId:Long):SongListWithSongs

    @Query("SELECT mediaFileUri FROM Song")
    suspend fun queryAllMediaFileUri():List<String>


    @Update
    suspend fun updateSongList(songList: SongList)
    @Update
    suspend fun updateSong(song: Song)
}
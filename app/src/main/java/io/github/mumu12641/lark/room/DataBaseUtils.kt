package io.github.mumu12641.lark.room

import io.github.mumu12641.lark.entity.*
import kotlinx.coroutines.flow.Flow

class DataBaseUtils {
    companion object {
        private val DataBase: MusicDataBase = MusicDataBase.getInstance()
        private val musicDao: MusicDao = DataBase.musicDao

        suspend fun queryAllSong(): List<Song> {
            return musicDao.queryAllSong()
        }

        suspend fun querySongById(songId: Long): Song {
            return musicDao.querySongById(songId)
        }


        suspend fun querySongIdByMediaUri(mediaFileUri: String): Long {
            return musicDao.querySongIdByMediaUri(mediaFileUri)
        }

        suspend fun querySongIdByNeteaseId(neteaseId:Long):Long{
            return musicDao.querySongIdByNeteaseId(neteaseId)
        }

        suspend fun isNeteaseIdExist(neteaseId: Long) : Boolean{
            return musicDao.isNeteaseIdExist(neteaseId)
        }

        suspend fun insertSong(song: Song): Long {
            return musicDao.insertSong(song)
        }


        fun queryAllSongList(): Flow<List<SongList>> {
            return musicDao.queryAllSongList()
        }

        fun querySongListFlowById(songListId: Long): Flow<SongList> {
            return musicDao.querySongListFlowById(songListId)
        }

        suspend fun querySongListById(songListId: Long): SongList {
            return musicDao.querySongListById(songListId)
        }

        suspend fun insertSongList(songList: SongList) {
            musicDao.insertSongList(songList)
        }
        suspend fun isSongListExist(title:String,type:Int):Boolean{
            return musicDao.isSongListExist(title ,type)
        }
        suspend fun querySongListId(title:String,type:Int):Long{
            return musicDao.querySongListId(title,type)
        }
        suspend fun querySongListsByType(type: Int):List<SongList>{
            return musicDao.querySongListsByType(type)
        }

        fun queryAllRef(): List<PlaylistSongCrossRef> {
            return musicDao.queryAllRef()
        }

        suspend fun isRefExist(songListId: Long, songId: Long): Boolean {
            return musicDao.isRefExist(songListId, songId)
        }

        suspend fun insertRef(playlistSongCrossRef: PlaylistSongCrossRef) {
            musicDao.insertRef(playlistSongCrossRef)
            if (playlistSongCrossRef.songListId == LikeSongListId) {
                updateSongList(
                    querySongListById(LikeSongListId).copy(
                        imageFileUri = querySongById(playlistSongCrossRef.songId).songAlbumFileUri
                    )
                )
            }
            updateSongList(
                querySongListById(playlistSongCrossRef.songListId).copy(
                    songNumber = querySongListWithSongsBySongListId(
                        playlistSongCrossRef.songListId
                    ).songs.size
                )
            )
        }

        fun querySongListWithSongsBySongListIdFlow(songListId: Long): Flow<SongListWithSongs> {
            return musicDao.querySongListWithSongsBySongListIdFlow(songListId)
        }

        suspend fun querySongListWithSongsBySongListId(songListId: Long): SongListWithSongs {
            return musicDao.querySongListWithSongsBySongListId(songListId)
        }

        suspend fun queryAllMediaFileUri(): List<String> {
            return musicDao.queryAllMediaFileUri()
        }


        suspend fun updateSongList(songList: SongList) {
            musicDao.updateSongList(songList)
        }

        suspend fun updateSong(song: Song){
            musicDao.updateSong(song)
        }

    }

}
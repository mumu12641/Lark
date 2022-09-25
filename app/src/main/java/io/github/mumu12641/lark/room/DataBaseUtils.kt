package io.github.mumu12641.lark.room

import io.github.mumu12641.lark.BaseApplication
import io.github.mumu12641.lark.BaseApplication.Companion.applicationScope
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.*
import io.github.mumu12641.lark.network.NetworkCreator
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

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

        suspend fun querySongFlowById(songId: Long): Flow<Song>{
            return musicDao.querySongFlowById(songId)
        }


        suspend fun querySongIdByMediaUri(mediaFileUri: String): Long {
            return musicDao.querySongIdByMediaUri(mediaFileUri)
        }

        suspend fun querySongIdByNeteaseId(neteaseId: Long): Long {
            return musicDao.querySongIdByNeteaseId(neteaseId)
        }

        suspend fun isNeteaseIdExist(neteaseId: Long): Boolean {
            return musicDao.isNeteaseIdExist(neteaseId)
        }

        suspend fun insertSong(song: Song): Long {
            if (song.neteaseId != 0L && isNeteaseIdExist(song.neteaseId)) {
                return querySongIdByNeteaseId(song.neteaseId)
            }
            val id = musicDao.insertSong(song)
            val singerList = song.songSinger.split(",")
            for (i in singerList) {
                if (!isSongListExist(i, ARTIST_SONGLIST_TYPE)) {
                    insertSongList(
                        SongList(
                            0L, i, "", 0, BaseApplication.context.getString(
                                R.string.no_description_text
                            ), "", ARTIST_SONGLIST_TYPE
                        )
                    )
                }
                val songListId = querySongListId(
                    i,
                    ARTIST_SONGLIST_TYPE
                )
                if (!isRefExist(songListId, id)) {
                    insertRef(
                        PlaylistSongCrossRef(
                            songListId, id
                        )
                    )
                }
            }

            return id
        }


        fun queryAllSongList(): Flow<List<SongList>> {
            return musicDao.queryAllSongList()
        }

        fun querySongListFlowById(songListId: Long): Flow<SongList> {
            return musicDao.querySongListFlowById(songListId)
        }

        fun querySongListFlowByIdType(songListId: Long, type: Int): Flow<SongList> {
            return musicDao.querySongListFlowByIdType(songListId, type)
        }

        suspend fun querySongListById(songListId: Long): SongList {
            return musicDao.querySongListById(songListId)
        }

        suspend fun insertSongList(songList: SongList): Long {
            val id = musicDao.insertSongList(songList)
            if (songList.type == ARTIST_SONGLIST_TYPE) {
                applicationScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, _ -> }) {
                    NetworkCreator.networkService.getSearchArtistResponse(songList.songListTitle).result.artists[0].artistId?.let {
                        val artistDetails =
                            NetworkCreator.networkService.getArtistDetail(it).data.artist
                        updateSongList(
                            querySongListById(id).copy(
                                imageFileUri = artistDetails.cover,
                                description = artistDetails.briefDesc
                            )
                        )
                    }
                }
            }
            return id
        }

        suspend fun isSongListExist(title: String, type: Int): Boolean {
            return musicDao.isSongListExist(title, type)
        }

        suspend fun querySongListId(title: String, type: Int): Long {
            return musicDao.querySongListId(title, type)
        }

        suspend fun querySongListsByType(type: Int): List<SongList> {
            return musicDao.querySongListsByType(type)
        }

        suspend fun queryAllRef(): List<PlaylistSongCrossRef> {
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

        suspend fun updateSong(song: Song) {
            musicDao.updateSong(song)
        }

    }

}
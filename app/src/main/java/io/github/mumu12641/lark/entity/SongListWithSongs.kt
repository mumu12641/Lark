package io.github.mumu12641.lark.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class SongListWithSongs(
    @Embedded
    val songList: SongList,

    @Relation(
        parentColumn = "songListId", entityColumn = "songId", associateBy = Junction(
            PlaylistSongCrossRef::class
        )
    )
    val songs: List<Song>
)
package io.github.mumu12641.lark.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class SongWithPlaylists (
    @Embedded
    val song: Song,

    @Relation(
        parentColumn = "songId", entityColumn = "songListId", associateBy = Junction(
            PlaylistSongCrossRef::class
        )
    )
    val songLists: List<SongList>
)
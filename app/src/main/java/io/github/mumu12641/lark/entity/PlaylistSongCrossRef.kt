package io.github.mumu12641.lark.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["songListId", "songId"])
data class PlaylistSongCrossRef(
    @ColumnInfo(index = true) val songListId: Long,
    @ColumnInfo(index = true) val songId: Long
)
package io.github.mumu12641.lark.entity

import androidx.room.Entity

@Entity(primaryKeys = ["songListId", "songId"])
data class PlaylistSongCrossRef(val songListId: Long, val songId: Long)
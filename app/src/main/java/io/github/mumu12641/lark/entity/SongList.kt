package io.github.mumu12641.lark.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SongList(
    @PrimaryKey(autoGenerate = true) val songListId: Long,
    var songListTitle: String,
    var createDate: String,
    var songNumber: Int,
    var description: String,
    var imageFileUri: String
)
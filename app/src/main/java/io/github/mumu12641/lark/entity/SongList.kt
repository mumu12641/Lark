package io.github.mumu12641.lark.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class SongList(
    @PrimaryKey(autoGenerate = true) val songListId: Long,
    var songListTitle: String,
    var createDate: String,
    var songNumber: Int,
    var description: String,
    var imageFileUri: String,
    var type: Int,
    var recentPlay: Date? = null,
    @ColumnInfo(name ="neteaseId", defaultValue = "0")
    var neteaseId: Long = 0L,
    var youtubeId: String? = null
)
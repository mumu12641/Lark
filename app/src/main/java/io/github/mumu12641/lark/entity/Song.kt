package io.github.mumu12641.lark.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.*

@Entity
data class Song(
    @PrimaryKey(autoGenerate = true) var songId:Long,
    var songTitle: String,
    var songSinger: String,
    var songAlbumFileUri: String,
    var mediaFileUri: String,
    var duration:Int,
    var recentPlay: Date? = null,
    var neteaseId:Long = 0L,
    var isBuffered:Int = NOT_NEED_BUFFER,
)

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
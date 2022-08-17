package io.github.mumu12641.lark.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.util.*

@Entity
data class Song(
    @PrimaryKey(autoGenerate = true) var songId:Long,
    var songTitle: String,
    var songSinger: String,
    var songAlbumFileUri: String,
    var mediaFileUri: String,
    var duration:Int,
    var recentPlay: Date? = null
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
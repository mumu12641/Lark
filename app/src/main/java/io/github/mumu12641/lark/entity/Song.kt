package io.github.mumu12641.lark.entity

import android.support.v4.media.MediaMetadataCompat
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.*

@Entity
data class Song(
    @PrimaryKey(autoGenerate = true) var songId: Long,
    var songTitle: String,
    var songSinger: String,
    var songAlbumFileUri: String,
    var mediaFileUri: String,
    var duration: Int,
    var recentPlay: Date? = null,
    var neteaseId: Long = 0L,
    var isBuffered: Int = NOT_NEED_BUFFER,
    var vip: Boolean = false,
    var lyrics: String? = null
) {
    override fun equals(other: Any?): Boolean {
        return (other as Song).songId == this.songId && other.neteaseId == this.neteaseId
    }

    override fun hashCode(): Int {
        var result = songId.hashCode()
        result = 31 * result + songTitle.hashCode()
        result = 31 * result + songSinger.hashCode()
        result = 31 * result + songAlbumFileUri.hashCode()
        result = 31 * result + mediaFileUri.hashCode()
        result = 31 * result + duration
        result = 31 * result + (recentPlay?.hashCode() ?: 0)
        result = 31 * result + neteaseId.hashCode()
        result = 31 * result + isBuffered
        return result
    }
}

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

fun Song.toMetaData(): MediaMetadataCompat{
    val builder = MediaMetadataCompat.Builder()
    this.lyrics?.let {
        builder.putString(MediaMetadataCompat.METADATA_KEY_COMPILATION, it)
    }
    return builder
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, songId.toString())
        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, songTitle)
        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, songSinger)
        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration.toLong())
        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, songAlbumFileUri)
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, mediaFileUri)
        .putString(MediaMetadataCompat.METADATA_KEY_COMPILATION, neteaseId.toString())
        .build()

}
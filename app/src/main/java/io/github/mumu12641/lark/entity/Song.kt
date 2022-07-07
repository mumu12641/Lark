package io.github.mumu12641.lark.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Song(
    @PrimaryKey(autoGenerate = true) var songId:Long,
    var songTitle: String,
    var songSinger: String,
    var songAlbumFileUri: String,
    var mediaFileUri: String,
    var duration:Int,
)
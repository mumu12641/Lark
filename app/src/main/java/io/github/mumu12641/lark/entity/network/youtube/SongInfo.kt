package io.github.mumu12641.lark.entity.network.youtube

import kotlinx.serialization.Serializable

@Serializable
data class SongInfo(
    val id:String,
    val title:String,
    val duration: Float,
    val thumbnails:List<Thumbnail>,
    val uploader:String
)

@Serializable
data class Thumbnail(
    val url:String,
)

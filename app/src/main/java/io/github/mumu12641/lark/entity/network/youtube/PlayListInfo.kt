package io.github.mumu12641.lark.entity.network.youtube

@kotlinx.serialization.Serializable
data class PlayListInfo(
//    val _type:String,
    val id: String,
    val title: String,
    val playlist_count: Int,
    val description: String,
    val entries: List<SongInfo>
)
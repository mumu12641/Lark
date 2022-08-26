package io.github.mumu12641.lark.entity.network

data class NeteaseSongList(
    val code: Int,
    val playlist: Playlist,
){
    data class Playlist(
        val coverImgUrl: String,
        val createTime: Long,
        val creator: Creator,
        val description: Any,
        val id: Int,
        val name: String,
        val trackCount: Int,
        val trackIds: List<TrackId>,
    )
    data class Creator(
        val nickname: String,
    )

    data class TrackId(
        val id: Int,
    )
}


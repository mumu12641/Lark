package io.github.mumu12641.lark.entity.network

data class NeteaseSongList(
    val code: Int,
    val playlist: Playlist,
) {
    data class Playlist(
        val coverImgUrl: String,
        val createTime: Long,
        val creator: Creator,
        val description: Any?,
        val id: Long,
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

data class Tracks(
    val code: Int,

    val songs: List<Song>
) {

    data class Song(
        val al: Al,
        val ar: List<Ar>,
        val dt: Int,
        val name: String,
        val id:Int,
    )

    data class Al(
        val id: Int,
        val name: String,
        val picUrl: String
    )

    data class Ar(
        val id: Int,
        val name: String
    )
}

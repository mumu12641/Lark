package io.github.mumu12641.lark.entity.network
data class SongDetail(
    val code: Int,
    val songs: List<Song>
){
    data class Song(
        val dt: Int
    )
}

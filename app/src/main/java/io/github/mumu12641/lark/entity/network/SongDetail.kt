package io.github.mumu12641.lark.entity.network

data class SongDetail(
    val code: Int,
    val songs: List<Song>,
    val privileges:List<Privilege>
) {
    data class Song(
        val dt: Int
    )
    data class Privilege(
        val fee: Int
    )
}

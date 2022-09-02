package io.github.mumu12641.lark.entity.network

data class SearchSongResponse(
    val code: Int,
    val result: Result
) {
    data class Result(
        val songCount: Int,
        val songs: List<Song>
    )

    data class Song(
        val id: Int,
        val al: Al,
        val ar: List<Ar>,
        val dt: Int,
        val mv: Int,
        val name: String,
        val privilege: Privilege
    )

    data class Al(
        val id: Int,
        val name: String,
        val picUrl: String,
    )

    data class Ar(
        val id: Int,
        val name: String,
    )

    data class Privilege(
        val fee:Int
    )
}
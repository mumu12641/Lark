package io.github.mumu12641.lark.entity.network

data class Lyrics(
    val code: Int,
    val klyric: Klyric,
    val lrc: Lrc,
    val qfy: Boolean,
    val romalrc: Romalrc,
    val sfy: Boolean,
    val sgc: Boolean,
    val tlyric: Tlyric
) {

    data class Klyric(
        val lyric: String,
        val version: Int
    )

    data class Lrc(
        val lyric: String,
        val version: Int
    )

    data class Romalrc(
        val lyric: String,
        val version: Int
    )

    data class Tlyric(
        val lyric: String,
        val version: Int
    )
}
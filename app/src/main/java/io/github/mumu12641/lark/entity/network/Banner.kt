package io.github.mumu12641.lark.entity.network

data class Banner(
    val banners: List<BannerX>,
    val code: Int
) {
    data class BannerX(
        val song: Song,
        val targetType: Int,
        val pic: String

    )

    data class Song(
        val al: Al,
        val ar: List<Ar>,
        val id: Int,
        val name: String,
    )

    data class Al(
        val picUrl: String,
    )

    data class Ar(
        val name: String,
    )
}

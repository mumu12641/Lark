package io.github.mumu12641.lark.entity.network.netease

data class SongUrl(
    val code: Int,
    val `data`: List<Data>
) {
    data class Data(
        val url: String?,
    )
}


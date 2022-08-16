package io.github.mumu12641.lark.entity.network

data class SearchHot(
    val code: Int,
    val `data`: List<Data>,
    val message: String
)

data class Data(
    val alg: String,
    val content: String,
    val iconType: Int,
    val iconUrl: String,
    val score: Int,
    val searchWord: String,
    val source: Int,
    val url: String
)
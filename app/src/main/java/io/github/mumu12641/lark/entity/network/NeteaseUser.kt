package io.github.mumu12641.lark.entity.network

data class NeteaseUser(
    val account: Account,
    val code: Int,
    val cookie: String,
) {
    data class Account(
        val id: Int,
    )
}

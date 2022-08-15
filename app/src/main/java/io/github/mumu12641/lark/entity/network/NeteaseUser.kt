package io.github.mumu12641.lark.entity.network

data class NeteaseUser(
    val account: Account,
    val bindings: List<Binding>,
    val code: Int,
    val cookie: String,
    val loginType: Int,
    val token: String
)

data class Account(
    val anonimousUser: Boolean,
    val ban: Int,
    val baoyueVersion: Int,
    val createTime: Long,
    val donateVersion: Int,
    val id: Int,
    val salt: String,
    val status: Int,
    val tokenVersion: Int,
    val type: Int,
    val uninitialized: Boolean,
    val userName: String,
    val vipType: Int,
    val viptypeVersion: Long,
    val whitelistAuthority: Int
)

data class Binding(
    val bindingTime: Long,
    val expired: Boolean,
    val expiresIn: Int,
    val id: Long,
    val refreshTime: Int,
    val tokenJsonStr: String,
    val type: Int,
    val url: String,
    val userId: Int
)
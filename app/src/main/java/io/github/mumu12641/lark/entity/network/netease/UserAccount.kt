package io.github.mumu12641.lark.entity.network.netease

data class UserAccount(
    val `data`: Data
) {
    data class Data(
        val account: Any,
        val code: Int,
        val profile: Any
    )
}


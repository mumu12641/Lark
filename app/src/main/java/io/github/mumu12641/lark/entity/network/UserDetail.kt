package io.github.mumu12641.lark.entity.network

data class UserDetail(
    val profile: Profile,
) {
    data class Profile(
        val avatarUrl: String,
        val backgroundUrl: String,
        val nickname: String,
    )
}



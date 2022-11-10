package io.github.mumu12641.lark.entity.network.netease

import kotlinx.serialization.Serializable

@Serializable
data class UpdateInfo(
    val body: String = "",
    val name: String = "",
    val tag_name: String = "",
)



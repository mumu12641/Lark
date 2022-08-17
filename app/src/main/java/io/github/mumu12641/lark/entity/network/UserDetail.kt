package io.github.mumu12641.lark.entity.network

data class UserDetail(
    val adValid: Boolean,
    val bindings: List<Any>,
    val code: Int,
    val createDays: Int,
    val createTime: Long,
    val level: Int,
    val listenSongs: Int,
    val mobileSign: Boolean,
    val pcSign: Boolean,
    val peopleCanSeeMyPlayRecord: Boolean,
    val profile: Profile,
    val profileVillageInfo: ProfileVillageInfo,
    val userPoint: UserPoint
)

data class Profile(
    val accountStatus: Int,
    val allSubscribedCount: Int,
    val artistIdentity: List<Any>,
    val authStatus: Int,
    val authority: Int,
    val avatarDetail: Any,
    val avatarImgId: Long,
    val avatarImgIdStr: String,
    val avatarImgId_str: String,
    val avatarUrl: String,
    val backgroundImgId: Long,
    val backgroundImgIdStr: String,
    val backgroundUrl: String,
    val birthday: Long,
    val blacklist: Boolean,
    val cCount: Int,
    val city: Int,
    val createTime: Long,
    val defaultAvatar: Boolean,
    val description: String,
    val detailDescription: String,
    val djStatus: Int,
    val eventCount: Int,
    val expertTags: Any,
    val experts: Experts,
    val followMe: Boolean,
    val followTime: Any,
    val followed: Boolean,
    val followeds: Int,
    val follows: Int,
    val gender: Int,
    val inBlacklist: Boolean,
    val mutual: Boolean,
    val newFollows: Int,
    val nickname: String,
    val playlistBeSubscribedCount: Int,
    val playlistCount: Int,
    val privacyItemUnlimit: PrivacyItemUnlimit,
    val province: Int,
    val remarkName: Any,
    val sCount: Int,
    val sDJPCount: Int,
    val signature: String,
    val userId: Int,
    val userType: Int,
    val vipType: Int
)

data class ProfileVillageInfo(
    val imageUrl: String,
    val targetUrl: String,
    val title: String
)

data class UserPoint(
    val balance: Int,
    val blockBalance: Int,
    val status: Int,
    val updateTime: Long,
    val userId: Int,
    val version: Int
)

class Experts

data class PrivacyItemUnlimit(
    val age: Boolean,
    val area: Boolean,
    val college: Boolean,
    val villageAge: Boolean
)



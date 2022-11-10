package io.github.mumu12641.lark.entity.network.netease

data class RecommendSongs(
    val code: Int,
    val `data`: Data
) {

    data class Data(
        val dailySongs: List<DailySong>,
        val orderSongs: List<Any>,
        val recommendReasons: List<RecommendReason>
    )

    data class DailySong(
        val a: Any,
        val al: Al,
        val alg: String,
        val alia: List<String>,
        val ar: List<Ar>,
        val cd: String,
        val cf: String,
        val copyright: Int,
        val cp: Int,
        val crbt: Any,
        val djId: Int,
        val dt: Int,
        val entertainmentTags: Any,
        val fee: Int,
        val ftype: Int,
        val h: H,
        val hr: Hr,
        val id: Int,
        val l: L,
        val m: M,
        val mark: Long,
        val mst: Int,
        val mv: Int,
        val name: String,
        val no: Int,
        val noCopyrightRcmd: Any,
        val originCoverType: Int,
        val originSongSimpleData: OriginSongSimpleData,
        val pop: Int,
        val privilege: Privilege,
        val pst: Int,
        val publishTime: Long,
        val reason: String,
        val resourceState: Boolean,
        val rt: String,
        val rtUrl: Any,
        val rtUrls: List<Any>,
        val rtype: Int,
        val rurl: Any,
        val s_ctrp: String,
        val s_id: Int,
        val single: Int,
        val songJumpInfo: Any,
        val sq: Sq,
        val st: Int,
        val t: Int,
        val tagPicList: Any,
        val tns: List<String>,
        val v: Int,
        val version: Int
    )

    data class RecommendReason(
        val reason: String,
        val reasonId: Any,
        val songId: Int,
        val targetUrl: Any
    )

    data class Al(
        val id: Int,
        val name: String,
        val pic: Long,
        val picUrl: String,
        val pic_str: String,
        val tns: List<Any>
    )

    data class Ar(
        val alias: List<Any>,
        val id: Int,
        val name: String,
        val tns: List<Any>
    )

    data class H(
        val br: Int,
        val fid: Int,
        val size: Int,
        val sr: Int,
        val vd: Int
    )

    data class Hr(
        val br: Int,
        val fid: Int,
        val size: Int,
        val sr: Int,
        val vd: Int
    )

    data class L(
        val br: Int,
        val fid: Int,
        val size: Int,
        val sr: Int,
        val vd: Int
    )

    data class M(
        val br: Int,
        val fid: Int,
        val size: Int,
        val sr: Int,
        val vd: Int
    )

    data class OriginSongSimpleData(
        val albumMeta: AlbumMeta,
        val artists: List<Artist>,
        val name: String,
        val songId: Int
    )

    data class Privilege(
        val chargeInfoList: List<ChargeInfo>,
        val cp: Int,
        val cs: Boolean,
        val dl: Int,
        val dlLevel: String,
        val downloadMaxBrLevel: String,
        val downloadMaxbr: Int,
        val fee: Int,
        val fl: Int,
        val flLevel: String,
        val flag: Int,
        val freeTrialPrivilege: FreeTrialPrivilege,
        val id: Int,
        val maxBrLevel: String,
        val maxbr: Int,
        val payed: Int,
        val pl: Int,
        val plLevel: String,
        val playMaxBrLevel: String,
        val playMaxbr: Int,
        val preSell: Boolean,
        val rscl: Any,
        val sp: Int,
        val st: Int,
        val subp: Int,
        val toast: Boolean
    )

    data class Sq(
        val br: Int,
        val fid: Int,
        val size: Int,
        val sr: Int,
        val vd: Int
    )

    data class AlbumMeta(
        val id: Int,
        val name: String
    )

    data class Artist(
        val id: Int,
        val name: String
    )

    data class ChargeInfo(
        val chargeMessage: Any,
        val chargeType: Int,
        val chargeUrl: Any,
        val rate: Int
    )

    data class FreeTrialPrivilege(
        val listenType: Any,
        val resConsumable: Boolean,
        val userConsumable: Boolean
    )
}
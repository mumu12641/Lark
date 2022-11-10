package io.github.mumu12641.lark.entity.network.netease

data class CloudDiskDetail(
    val code: Int,
    val `data`: List<Data>
) {
    data class Data(
        val addTime: Long,
        val album: String,
        val artist: String,
        val bitrate: Int,
        val cover: Int,
        val coverId: String,
        val fileName: String,
        val fileSize: Int,
        val lyricId: String,
        val simpleSong: SimpleSong,
        val songId: Int,
        val songName: String,
        val version: Int
    )

    data class SimpleSong(
        val a: Any,
        val al: Al,
        val alia: List<Any>,
        val ar: List<Ar>,
        val cd: String,
        val cf: String,
        val copyright: Int,
        val cp: Int,
        val crbt: Any,
        val djId: Int,
        val dt: Int,
        val fee: Int,
        val ftype: Int,
        val h: H,
        val id: Int,
        val l: L,
        val m: M,
        val mark: Int,
        val mst: Int,
        val mv: Int,
        val name: String,
        val no: Int,
        val noCopyrightRcmd: Any,
        val originCoverType: Int,
        val originSongSimpleData: Any,
        val pop: Int,
        val privilege: Privilege,
        val pst: Int,
        val publishTime: Long,
        val rt: String,
        val rtUrl: Any,
        val rtUrls: List<Any>,
        val rtype: Int,
        val rurl: Any,
        val s_id: Int,
        val single: Int,
        val st: Int,
        val t: Int,
        val v: Int
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
        val vd: Int
    )

    data class L(
        val br: Int,
        val fid: Int,
        val size: Int,
        val vd: Int
    )

    data class M(
        val br: Int,
        val fid: Int,
        val size: Int,
        val vd: Int
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
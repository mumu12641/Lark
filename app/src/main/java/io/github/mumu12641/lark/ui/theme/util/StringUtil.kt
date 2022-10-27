package io.github.mumu12641.lark.ui.theme.util

import android.content.ClipboardManager
import android.content.Context
import io.github.mumu12641.lark.BaseApplication.Companion.context

object StringUtil {

    private fun getPaste(): String {
        val manager: ClipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val data = manager.primaryClip
        if (data == null || data.itemCount <= 0) {
            return ""
        }
        return data.getItemAt(0).text.toString()
    }

    fun getHttpUrl(): String? {
        val string = getPaste()
        val r = Regex("[a-zA-z]+://[^\\s]*")
        r.find(string)?.let {
            return it.value
        }
        return null
    }

    fun getNeteaseSongListId(s: String): String? {
        val r = Regex("[a-zA-z]+://[^\\s]*")
        r.find(s)?.let {
            return it.value.substringAfter("playlist?id=").substringBefore("&userid")
        }
        return null
    }

    fun matchYoutubeLink(s:String):String?{
        val r = Regex("[a-zA-z]+://[^\\s]*")
//        https://music.youtube.com/playlist?list=PLq6Tn5ulC8-vG-L0f0v-pvnhaM9ajYLOx&feature=share
        r.find(s)?.let {
            return it.value.substringAfter("playlist?list=").substringBefore("&feature=share")
        }
        return null
    }
}

package io.github.mumu12641.lark.ui.theme.util

import android.content.ClipboardManager
import android.content.Context
import io.github.mumu12641.lark.BaseApplication.Companion.context

object StringUtil {

    val httpRegex = Regex("[a-zA-z]+://[^\\s]*")

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
        httpRegex.find(string)?.let {
            return it.value
        }
        return null
    }

    fun getNeteaseSongListId(s: String): String? {
        httpRegex.find(s)?.let {
            return it.value.substringAfter("playlist?id=").substringBefore("&userid")
        }
        return null
    }

    fun matchYoutubeLink(s:String):String?{
        httpRegex.find(s)?.let {
            return it.value.substringAfter("playlist?list=").substringBefore("&feature=share")
        }
        return null
    }
}

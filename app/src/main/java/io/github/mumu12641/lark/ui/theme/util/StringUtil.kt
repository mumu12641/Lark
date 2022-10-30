package io.github.mumu12641.lark.ui.theme.util

import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import io.github.mumu12641.lark.BaseApplication.Companion.applicationScope
import io.github.mumu12641.lark.BaseApplication.Companion.context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    fun matchYoutubeLink(s: String): String? {
        httpRegex.find(s)?.let {
            return it.value.substringAfter("playlist?list=").substringBefore("&feature=share")
        }
        return null
    }
}

fun String.toast() {
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
}

fun String.suspendToast() {
    applicationScope.launch(Dispatchers.Main) {
        this@suspendToast.toast()
    }
}


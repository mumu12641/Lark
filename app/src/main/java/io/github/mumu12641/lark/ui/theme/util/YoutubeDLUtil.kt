package io.github.mumu12641.lark.ui.theme.util

import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import com.yausername.youtubedl_android.YoutubeDLResponse
import io.github.mumu12641.lark.entity.network.PlayListInfo
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

object YoutubeDLUtil {


    private const val TAG = "YoutubeDLUtil"
    private val jsonFormat = Json { ignoreUnknownKeys = true }


    suspend fun getPlayListInfo(url: String): PlayListInfo {
        val request = YoutubeDLRequest(url)
        with(request) {
            addOption("--flat-playlist")
            addOption("-J")
            addOption("-R", "1")
            addOption("--socket-timeout", "5")
        }
        val resp: YoutubeDLResponse = YoutubeDL.getInstance().execute(request, null)
        return jsonFormat.decodeFromString(resp.out)
    }

    suspend fun getStream(id: String): String {
        val request = YoutubeDLRequest("https://youtu.be/$id")
        request.addOption("-f", "b")
        val streamInfo = YoutubeDL.getInstance().getInfo(request)
        return streamInfo.url
    }

}
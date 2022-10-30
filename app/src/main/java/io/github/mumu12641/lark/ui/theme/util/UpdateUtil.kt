package io.github.mumu12641.lark.ui.theme.util

import android.util.Log
import android.widget.Toast
import com.yausername.youtubedl_android.YoutubeDL
import io.github.mumu12641.lark.BaseApplication
import io.github.mumu12641.lark.BaseApplication.Companion.context
import io.github.mumu12641.lark.BaseApplication.Companion.version
import io.github.mumu12641.lark.BaseApplication.Companion.ytDlpVersion
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.network.UpdateInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.*
import java.io.IOException
import java.util.regex.Pattern
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


object UpdateUtil {

    private val request = Request.Builder()
        .url("https://api.github.com/repos/mumu12641/Lark/releases/latest")
        .build()
    private val client = OkHttpClient()
    private val jsonFormat = Json { ignoreUnknownKeys = true }

    suspend fun getUpdateInfo(): UpdateInfo {
        return suspendCoroutine { continuation ->
            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val responseData = response.body.string()
                    val latestRelease = jsonFormat.decodeFromString<UpdateInfo>(responseData)
                    response.body.close()
                    continuation.resume(latestRelease)
                }

                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }
            })
        }
    }

    fun checkForUpdate(info: UpdateInfo): Boolean {
        return Version(info.name) > Version(version)
    }

    suspend fun updateYtDlp(): String {
        withContext(Dispatchers.IO) {
            try {
                YoutubeDL.getInstance().updateYoutubeDL(context)
                context.getString(R.string.yt_dlp_up_to_date).suspendToast()
            } catch (e: Exception) {
                Log.d("TAG", "updateYtDlp: " + e.message)
                context.getString(R.string.yt_dlp_update_fail).suspendToast()
            }
        }
        YoutubeDL.getInstance().version(context)?.let {
            ytDlpVersion = it
        }
        return ytDlpVersion
    }

    const val RELEASE_URL = "https://github.com/mumu12641/Lark/releases/"
}

class Version(
    versionName: String = "v0.0.0",
) {
    var major: Int = 0
        private set
    var minor: Int = 0
        private set
    var patch: Int = 0
        private set
    var build: Int = 0
        private set

    private fun toNumber(): Long {
        return major * MAJOR + minor * MINOR + patch * PATCH + build * BUILD
    }

    companion object {
        private val pattern = Pattern.compile("""v?(\d+)\.(\d+)\.(\d+)(-.*?(\d+))*""")

        private const val BUILD = 1L
        private const val PATCH = 100L
        private const val MINOR = 10_000L
        private const val MAJOR = 1_000_000L
    }

    init {
        val matcher = pattern.matcher(versionName)
        if (matcher.find()) {
            major = matcher.group(1)?.toInt() ?: 0
            minor = matcher.group(2)?.toInt() ?: 0
            patch = matcher.group(3)?.toInt() ?: 0
            build = matcher.group(5)?.toInt() ?: 99
        }
    }

    operator fun compareTo(other: Version) = toNumber().compareTo(other.toNumber())
}
package io.github.mumu12641.lark.network

import io.github.mumu12641.lark.BaseApplication
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val TAG = "Repository"

object Repository {
    private val service = NetworkCreator.networkService

    suspend fun getSearchArtistResponse(keywords: String) =
        service.getSearchArtistResponse(keywords).await()

    suspend fun getArtistDetail(id: Long) =
        service.getArtistDetail(id).await()

    suspend fun cellphoneLogin(
        phoneNumber: String,
        Password: String
    ) = service.cellphoneLogin(phoneNumber, Password)
        .await()

    suspend fun getUserDetail(uid: Long) =
        service.getUserDetail(uid).await()

    suspend fun logout() =
        service.logout().await()

    suspend fun getSongUrl(id: Long) =
        service.getSongUrl(id).await()

    suspend fun getSongDetail(ids: String) =
        service.getSongDetail(ids).await()

    suspend fun getNeteaseSongList(id: Long) =
        service.getNeteaseSongList(id).await()

    suspend fun getNeteaseSongListTracks(id: Long) =
        service.getNeteaseSongListTracks(id).await()

    suspend fun getLyric(id: Long) =
        service.getLyric(id).await()

    suspend fun getBanner() =
        service.getBanner().await()

    suspend fun getLevelMusic(
        id: Long,
        level: String = BaseApplication.kv.decodeString(
            PreferenceUtil.MUSIC_QUALITY,
            "standard"
        )!!
    ) = service.getLevelMusic(id, level).await()

    suspend fun anonymousLogin() =
        service.anonymousLogin().await()


    private suspend fun <T> Call<T>.await() = suspendCoroutine<T> { continuation ->
        enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val body = response.body()
                val statusCode = response.code()
                if (body != null && statusCode == HttpURLConnection.HTTP_OK) {
                    continuation.resume(body)
                } else {
                    continuation.resumeWithException(RuntimeException("Network request failed, the HTTP status code is $statusCode"))
                }
            }
            override fun onFailure(call: Call<T>, t: Throwable) {
                continuation.resumeWithException(t)
                t.printStackTrace()
            }
        })
    }
}
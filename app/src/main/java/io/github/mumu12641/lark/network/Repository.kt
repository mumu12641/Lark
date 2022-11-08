package io.github.mumu12641.lark.network

import android.util.Log
import io.github.mumu12641.lark.entity.network.Banner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    //    suspend fun getBanner(): ApiResult<Banner> {
//        return safeApiCall(Dispatchers.IO) { service.getBanner() }
//    }

    suspend fun getBanner() =
        service.getBanner().await()

    suspend fun getSafeCallBanner(): LoadResult<Banner> = safeApiCall {
        service.getBanner().await()
    }

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

    private suspend fun <T> safeApiCall(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        apiCall: suspend () -> T
    ): LoadResult<T> {
        return withContext(dispatcher) {
            try {
                LoadResult.Success<T>(_data = apiCall.invoke())
            } catch (e: Exception) {
                LoadResult.Error<T>(_data = null)
            }
        }
    }

    inline fun <T> returnFunction(result: LoadResult<T>){
        if (result.status == LoadStatus.ERROR){
            return
        }

    }
}
package io.github.mumu12641.lark.network

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

enum class ApiStatus {
    SUCCESS,
    ERROR,
    LOADING
}  // for your case might be simplify to use only sealed class

sealed class ApiResult<out T>(val status: ApiStatus, val data: T?, val message: String?) {

    data class Success<out R>(val _data: R?) : ApiResult<R>(
        status = ApiStatus.SUCCESS,
        data = _data,
        message = null
    )

    data class Error(val exception: String) : ApiResult<Nothing>(
        status = ApiStatus.ERROR,
        data = null,
        message = exception
    )

    data class Loading<out R>(val _data: R?, val isLoading: Boolean) : ApiResult<R>(
        status = ApiStatus.LOADING,
        data = _data,
        message = null
    )
}

suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    apiCall: suspend () -> T
): ApiResult<T> {
    return withContext(dispatcher) {
        try {
            ApiResult.Success(apiCall.invoke())
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResult.Error(e.message.toString())
        }
    }
}

//inline fun <T> Call<T>.request(crossinline onResult: (response: ApiResult<T>) -> Unit) {
//    enqueue(object : retrofit2.Callback<T> {
//        override fun onResponse(call: Call<T>, response: Response<T>) {
//            if (response.isSuccessful) {
//                // success
//                onResult(ApiResult.Success(response))
//            } else {
//                //failure
//                onResult(ApiResult.Error(response))
//            }
//        }
//
//        override fun onFailure(call: Call<T>, throwable: Throwable) {
//            onResult(ApiResult.Error(throwable.message))
//        }
//    })
//}
//suspend fun <T: Any> handleRequest(requestFunc: suspend () -> T): kotlin.Result<T> {
//    return try {
//        Result.success(requestFunc.invoke())
//    } catch (he: HttpException) {
//        Result.failure(he)
//    }
//}
package io.github.mumu12641.lark.network

enum class LoadStatus {
    SUCCESS,
    ERROR,
    LOADING,
    None
}  // for your case might be simplify to use only sealed class

sealed class LoadResult<out T>(val status: LoadStatus, val data: T?) {

    data class Success<out R>(val _data: R?) : LoadResult<R>(
        status = LoadStatus.SUCCESS,
        data = _data,
    )

    data class Error<out R>(val _data: R?) : LoadResult<R>(
        status = LoadStatus.ERROR,
        data = _data,
    )

    data class Loading<out R>(val _data: R?) : LoadResult<R>(
        status = LoadStatus.LOADING,
        data = _data,
    )

    data class None<out R>(val _data: R? = null) : LoadResult<R>(
        status = LoadStatus.None,
        data = _data,
    )
}
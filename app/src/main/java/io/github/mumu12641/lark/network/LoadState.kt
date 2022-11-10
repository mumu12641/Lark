package io.github.mumu12641.lark.network

sealed class LoadState(val msg: String) {
    class Loading(msg: String = "") : LoadState(msg)
    class Success(msg: String = "") : LoadState(msg)
    class Fail(msg: String) : LoadState(msg)
    class None(msg: String = "") : LoadState(msg)
}
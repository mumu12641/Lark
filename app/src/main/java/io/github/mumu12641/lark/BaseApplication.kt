package io.github.mumu12641.lark


import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.tencent.mmkv.MMKV


class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        MMKV.initialize(this)
        MMKV.defaultMMKV().encode("First",true)
        MMKV.defaultMMKV().encode("UserName", context.getString(R.string.user))
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

    }
}
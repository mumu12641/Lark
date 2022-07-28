package io.github.mumu12641.lark


import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        MMKV.initialize(this)
        if (MMKV.defaultMMKV().decodeInt("first") == 0) {
            MMKV.defaultMMKV().encode("first", 1)
            MMKV.defaultMMKV().encode("userName", context.getString(R.string.user))
        }
    }


    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
}
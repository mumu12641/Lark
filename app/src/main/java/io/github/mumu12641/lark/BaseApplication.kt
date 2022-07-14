package io.github.mumu12641.lark


import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.tencent.mmkv.MMKV
import io.github.mumu12641.lark.room.MusicDao
import io.github.mumu12641.lark.room.MusicDataBase


class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        MMKV.initialize(this)
        MMKV.defaultMMKV().encode("First",true)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

    }
}
package io.github.mumu12641.lark


import android.annotation.SuppressLint
import android.app.Application
import android.content.Context


class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
}
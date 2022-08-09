package io.github.mumu12641.lark


import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
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
        deviceScreen = getAndroidScreenProperty()
    }


    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        lateinit var deviceScreen:List<Int>

    }

    private fun getAndroidScreenProperty():List<Int> {
        val dm = DisplayMetrics()
        val width = dm.widthPixels
        val height = dm.heightPixels
        val density = dm.density
        val screenWidth = (width / density).toInt()
        val screenHeight = (height / density).toInt()
        return listOf(screenWidth,screenHeight)
    }

}
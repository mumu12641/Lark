package io.github.mumu12641.lark


import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


@HiltAndroidApp
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        applicationScope  = CoroutineScope(SupervisorJob())
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
        lateinit var applicationScope: CoroutineScope

    }

    fun getAndroidScreenProperty():List<Int> {
        val wm = this.getSystemService(WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        wm.defaultDisplay.getMetrics(dm)
        val width = dm.widthPixels // 屏幕宽度（像素）
        val height = dm.heightPixels // 屏幕高度（像素）
        val density = dm.density // 屏幕密度（0.75 / 1.0 / 1.5）
        val densityDpi = dm.densityDpi // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        val screenWidth = (width / density).toInt() // 屏幕宽度(dp)
        val screenHeight = (height / density).toInt() // 屏幕高度(dp)
        return listOf(screenWidth,screenHeight)
    }

}
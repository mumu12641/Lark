package io.github.mumu12641.lark


import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


@HiltAndroidApp
class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        applicationScope = CoroutineScope(SupervisorJob())
        MMKV.initialize(this)
        kv = MMKV.defaultMMKV()
        if (kv.decodeInt("first") == 0) {
            kv.encode("first", 1)
            kv.encode("userName", context.getString(R.string.user))
        }
        version = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(
                packageName,
                PackageManager.PackageInfoFlags.of(0)
            ).versionName
        } else {
            packageManager.getPackageInfo(packageName, 0).versionName
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        lateinit var applicationScope: CoroutineScope
        lateinit var version: String
        lateinit var kv: MMKV
    }
}
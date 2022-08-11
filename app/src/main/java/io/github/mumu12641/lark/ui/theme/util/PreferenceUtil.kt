package io.github.mumu12641.lark.ui.theme.util

import android.util.Log
import com.tencent.mmkv.MMKV
import io.github.mumu12641.lark.BaseApplication.Companion.applicationScope
import io.github.mumu12641.lark.BaseApplication.Companion.context
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.FOLLOW_SYSTEM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


object PreferenceUtil {
    private val kv = MMKV.defaultMMKV()

    const val DEFAULT_SEED_COLOR = 0xFF44655A.toInt()

    val DARK_MODE_FOLLOW_SYSTEM = context.getString(R.string.follow_system_text)
    val DARK_MODE_OPEN = context.getString(R.string.on_text)
    val DARK_MODE_CLOSE = context.getString(R.string.off_text)
    const val FOLLOW_SYSTEM = 0
    const val ON = 1
    const val OFF = 2

    private const val DARK_MODE = "dark mode value"
    private const val SEED_COLOR = "seed color value"

    private val _disaplayPreferenceFlow = MutableStateFlow(
        DisplayPreference(
            kv.decodeInt(DARK_MODE, FOLLOW_SYSTEM),
            kv.decodeInt(SEED_COLOR, DEFAULT_SEED_COLOR)
        )
    )
    val displayPreferenceFlow = _disaplayPreferenceFlow

    data class DisplayPreference(
        val darkModePreference: Int = FOLLOW_SYSTEM,
        val seedColor: Int = DEFAULT_SEED_COLOR
    )

//    data class DarkModePreference(var mode: Int = FOLLOW_SYSTEM)

    fun switchDarkMode(mode: Int) {
        applicationScope.launch(Dispatchers.IO) {
            Log.d(TAG, "switchDarkMode")
            _disaplayPreferenceFlow.update {
                it.copy(darkModePreference = mode)
            }
            kv.encode(DARK_MODE,mode)
        }
    }

    fun changeSeedColor(color:Int){
        applicationScope.launch(Dispatchers.IO) {
            _disaplayPreferenceFlow.update {
                it.copy(seedColor = color)
            }
            kv.encode(SEED_COLOR,color)
        }
    }
    private const val TAG = "PreferenceUtil"
}
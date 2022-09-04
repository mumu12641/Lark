package io.github.mumu12641.lark.ui.theme.util

import android.os.Build
import android.util.Log
import com.tencent.mmkv.MMKV
import io.github.mumu12641.lark.BaseApplication.Companion.applicationScope
import io.github.mumu12641.lark.BaseApplication.Companion.context
import io.github.mumu12641.lark.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


object PreferenceUtil {
    private val kv = MMKV.defaultMMKV()

    const val DEFAULT_SEED_COLOR = 0xFF00221A.toInt()
    const val EMPTY_SEED_COLOR = 0

    val DARK_MODE_FOLLOW_SYSTEM = context.getString(R.string.follow_system_text)
    val DARK_MODE_OPEN = context.getString(R.string.on_text)
    val DARK_MODE_CLOSE = context.getString(R.string.off_text)
    const val FOLLOW_SYSTEM = 0
    const val ON = 1
    const val OFF = 2

    private const val DARK_MODE = "dark mode value"
    const val SEED_COLOR = "seed color value"
    private const val DYNAMIC_COLOR = "dynamic color preference"
    private const val FOLLOW_ALBUM_COLOR_SWITCH = "FOLLOW_ALBUM_COLOR_SWITCH"

    private val _disaplayPreferenceFlow = MutableStateFlow(
        DisplayPreference(
            kv.decodeInt(DARK_MODE, FOLLOW_SYSTEM),
            kv.decodeInt(SEED_COLOR, DEFAULT_SEED_COLOR),
            DynamicPreference(dynamicColorSwitch = kv.decodeInt(DYNAMIC_COLOR, OFF)),
            followAlbumSwitch = kv.decodeInt(FOLLOW_ALBUM_COLOR_SWITCH, ON)
        )
    )
    val displayPreferenceFlow = _disaplayPreferenceFlow

    data class DisplayPreference(
        val darkModePreference: Int = FOLLOW_SYSTEM,
        val seedColor: Int = DEFAULT_SEED_COLOR,
        val dynamicPreference: DynamicPreference = DynamicPreference(),
        val currentAlbumColor: Int = DEFAULT_SEED_COLOR,
        val followAlbumSwitch: Int = ON
    )

    data class DynamicPreference(
        val enable: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
        val dynamicColorSwitch: Int = OFF
    )

    fun switchDarkMode(mode: Int) {
        applicationScope.launch(Dispatchers.IO) {
            Log.d(TAG, "switchDarkMode")
            _disaplayPreferenceFlow.update {
                it.copy(darkModePreference = mode)
            }
            kv.encode(DARK_MODE, mode)
        }
    }

    fun changeCurrentAlbumColor(color: Int) {
        applicationScope.launch(Dispatchers.IO) {
            _disaplayPreferenceFlow.update {
                it.copy(currentAlbumColor = color)
            }
        }
    }

    fun changeSeedColor(color: Int) {
        applicationScope.launch(Dispatchers.IO) {
            _disaplayPreferenceFlow.update {
                it.copy(seedColor = color)
            }
            kv.encode(SEED_COLOR, color)
        }
    }

    fun switchDynamicColor(mode: Int) {
        applicationScope.launch(Dispatchers.IO) {
            _disaplayPreferenceFlow.update {
                it.copy(dynamicPreference = DynamicPreference(dynamicColorSwitch = mode))
            }
            kv.encode(DYNAMIC_COLOR, mode)
        }
    }

    fun switchFollowAlbum(mode: Int) {
        applicationScope.launch(Dispatchers.IO) {
            _disaplayPreferenceFlow.update {
                it.copy(followAlbumSwitch = mode)
            }
            kv.encode(FOLLOW_ALBUM_COLOR_SWITCH, mode)
        }
    }

    private const val TAG = "PreferenceUtil"
}
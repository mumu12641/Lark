package io.github.mumu12641.lark.ui.theme.util

import android.os.Build
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.core.os.LocaleListCompat
import com.tencent.mmkv.MMKV
import io.github.mumu12641.lark.BaseApplication.Companion.applicationScope
import io.github.mumu12641.lark.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


object PreferenceUtil {
    private val kv = MMKV.defaultMMKV()

    const val DEFAULT_SEED_COLOR = 0xFF00221A.toInt()
    const val EMPTY_SEED_COLOR = 0

    const val FOLLOW_SYSTEM = 0

    const val ON = 1
    const val OFF = 2

    private const val SYSTEM_DEFAULT = 0
    private const val SIMPLIFIED_CHINESE = 1
    private const val ENGLISH = 2

    const val SEED_COLOR = "seed color value"
    const val REPEAT_MODE = "repeat mode"

    private const val DARK_MODE = "dark mode value"
    private const val DYNAMIC_COLOR = "dynamic color preference"
    private const val FOLLOW_ALBUM_COLOR_SWITCH = "FOLLOW_ALBUM_COLOR_SWITCH"
    private const val LANGUAGE = "language"


    private val languageMap: Map<Int, String> = mapOf(
        Pair(SIMPLIFIED_CHINESE, "zh-CN"),
        Pair(ENGLISH, "en-US"),
    )

    fun setLanguage(number: Int) {
        kv.encode(LANGUAGE, number)
    }

    fun getLanguageConfiguration(languageNumber: Int = kv.decodeInt(LANGUAGE)): String {
        return if (languageMap.containsKey(languageNumber)) languageMap[languageNumber].toString() else ""
    }

    private fun getLanguageNumberByCode(languageCode: String): Int {
        languageMap.entries.forEach {
            if (it.value == languageCode) return it.key
        }
        return SYSTEM_DEFAULT
    }

    fun getLanguageNumber(): Int {
        return if (Build.VERSION.SDK_INT >= 33)
            getLanguageNumberByCode(
                LocaleListCompat.getAdjustedDefault()[0]?.toLanguageTag().toString()
            )
        else kv.getInt(LANGUAGE, 0)
    }

    @Composable
    fun getLanguageDesc(languageNumber: Int = getLanguageNumber()): String {
        return when (languageNumber) {
            SIMPLIFIED_CHINESE -> stringResource(R.string.la_zh_CN)
            ENGLISH -> stringResource(R.string.la_en_US)
            else -> stringResource(id = R.string.follow_system_text)
        }
    }


    private val _disaplayPreferenceFlow = MutableStateFlow(
        DisplayPreference(
            kv.decodeInt(DARK_MODE, FOLLOW_SYSTEM),
            kv.decodeInt(SEED_COLOR, DEFAULT_SEED_COLOR),
            DynamicColorPreference(dynamicColorSwitch = kv.decodeInt(DYNAMIC_COLOR, OFF)),
            followAlbumSwitch = kv.decodeInt(FOLLOW_ALBUM_COLOR_SWITCH, OFF)
        )
    )
    val displayPreferenceFlow = _disaplayPreferenceFlow

    data class DisplayPreference(
        val darkModePreference: Int = FOLLOW_SYSTEM,
        val seedColor: Int = DEFAULT_SEED_COLOR,
        val dynamicPreference: DynamicColorPreference = DynamicColorPreference(),
        val currentAlbumColor: Int = DEFAULT_SEED_COLOR,
        val followAlbumSwitch: Int = ON
    )

    data class DynamicColorPreference(
        val enable: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
        val dynamicColorSwitch: Int = OFF
    )

    fun getDarkMode(): Int {
        return kv.decodeInt(DARK_MODE)
    }

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
                it.copy(dynamicPreference = DynamicColorPreference(dynamicColorSwitch = mode))
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
package io.github.mumu12641.lark.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import io.github.mumu12641.lark.ui.theme.color.scheme.ColorScheme
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.FOLLOW_SYSTEM
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.OFF
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.ON


@Composable
fun LarkTheme(
    darkTheme: Int,
    dynamicColorEnable: Boolean,
    dynamicColor: Int,
    seedColor: Int,
    content: @Composable () -> Unit
) {
    val colorScheme: androidx.compose.material3.ColorScheme
    when {
        dynamicColor == ON && dynamicColorEnable -> {
            val context = LocalContext.current
            colorScheme =
                if (darkTheme == ON || (darkTheme == FOLLOW_SYSTEM && isSystemInDarkTheme())) {
                    dynamicDarkColorScheme(context)
                } else {
                    dynamicLightColorScheme(context)

                }
        }
        darkTheme == ON || (darkTheme == FOLLOW_SYSTEM && isSystemInDarkTheme()) -> {
            colorScheme = ColorScheme.getDarkColorScheme(seedColor)
        }
        else -> {
            colorScheme = ColorScheme.getLightColorScheme(seedColor)
        }
    }
    ControlSystemUi(darkTheme)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
private fun ControlSystemUi(darkTheme: Int) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        val systemUiController = rememberSystemUiController()
        val useDarkIcons =
            !(darkTheme == ON || (darkTheme == FOLLOW_SYSTEM && isSystemInDarkTheme()))
        SideEffect {
            systemUiController.setStatusBarColor(Color.Transparent, darkIcons = useDarkIcons)
            systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = useDarkIcons)
            systemUiController.setNavigationBarColor(Color.Transparent, darkIcons = useDarkIcons)
        }
    }
}

@Composable
fun PlayPageTheme(
    darkTheme: Int,
    followAlbumSwitch: Int,
    dynamicColorEnable: Boolean,
    dynamicColor: Int,
    seedColor: Int,
    currentAlbumColor: Int,
    content: @Composable () -> Unit
) {
    when (followAlbumSwitch) {
        ON -> {
            val colorScheme = when {
                darkTheme == ON || (darkTheme == FOLLOW_SYSTEM && isSystemInDarkTheme()) -> {
                    ColorScheme.getDarkColorScheme(currentAlbumColor)
                }
                else -> {
                    ColorScheme.getLightColorScheme(currentAlbumColor)
                }
            }
            MaterialTheme(
                colorScheme = colorScheme,
                typography = Typography,
                content = content
            )
        }
        OFF -> {
            LarkTheme(
                darkTheme = darkTheme,
                dynamicColorEnable = dynamicColorEnable,
                dynamicColor = dynamicColor,
                seedColor = seedColor,
                content = content
            )
        }
    }
    ControlSystemUi(darkTheme = darkTheme)

}
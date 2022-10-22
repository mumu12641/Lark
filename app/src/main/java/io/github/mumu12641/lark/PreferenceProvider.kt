package io.github.mumu12641.lark

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.DEFAULT_SEED_COLOR
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.FOLLOW_SYSTEM
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.ON
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.STANDARD

val LocalDarkTheme = compositionLocalOf { FOLLOW_SYSTEM }
val LocalSeedColor = compositionLocalOf { DEFAULT_SEED_COLOR }

@SuppressLint("CompositionLocalNaming")
val CurrentAlbumColor = compositionLocalOf { DEFAULT_SEED_COLOR }

@SuppressLint("CompositionLocalNaming")
val FollowAlbumSwitch = compositionLocalOf { ON }

@SuppressLint("CompositionLocalNaming")
val DynamicColorSwitch = compositionLocalOf { PreferenceUtil.DynamicColorPreference() }
val displayPreferenceFlow = PreferenceUtil.displayPreferenceFlow
val LocalMusicQuality = compositionLocalOf { STANDARD }

@Composable
fun PreferenceProvider(content: @Composable () -> Unit) {
    val displayPreferenceFlowState = displayPreferenceFlow.collectAsState().value
    CompositionLocalProvider(
        LocalDarkTheme provides displayPreferenceFlowState.darkModePreference,
        LocalSeedColor provides displayPreferenceFlowState.seedColor,
        DynamicColorSwitch provides displayPreferenceFlowState.dynamicPreference,
        CurrentAlbumColor provides displayPreferenceFlowState.currentAlbumColor,
        FollowAlbumSwitch provides displayPreferenceFlowState.followAlbumSwitch,
        LocalMusicQuality provides displayPreferenceFlowState.musicQuality,
        LocalDensity provides Density(
            density = LocalContext.current.resources.displayMetrics.widthPixels / 400.0f
        ),
        content = content
    )
}
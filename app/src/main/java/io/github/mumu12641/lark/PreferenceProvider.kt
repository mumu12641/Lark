package io.github.mumu12641.lark

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.DEFAULT_SEED_COLOR
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.FOLLOW_SYSTEM
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.ON

val LocalDarkTheme = compositionLocalOf { FOLLOW_SYSTEM }
val LocalSeedColor = compositionLocalOf { DEFAULT_SEED_COLOR }
@SuppressLint("CompositionLocalNaming")
val CurrentAlbumColor = compositionLocalOf { DEFAULT_SEED_COLOR }
@SuppressLint("CompositionLocalNaming")
val FollowAlbumSwitch = compositionLocalOf { ON }
@SuppressLint("CompositionLocalNaming")
val DynamicColorSwitch = compositionLocalOf { PreferenceUtil.DynamicPreference() }
val displayPreferenceFlow = PreferenceUtil.displayPreferenceFlow

@Composable
fun PreferenceProvider(content: @Composable () -> Unit) {
    val displayPreferenceFlowState = displayPreferenceFlow.collectAsState().value
    CompositionLocalProvider(
        LocalDarkTheme provides displayPreferenceFlowState.darkModePreference,
        LocalSeedColor provides displayPreferenceFlowState.seedColor,
        DynamicColorSwitch provides displayPreferenceFlowState.dynamicPreference,
        CurrentAlbumColor provides displayPreferenceFlowState.currentAlbumColor,
        FollowAlbumSwitch provides displayPreferenceFlowState.followAlbumSwitch,
        content = content
    )
}
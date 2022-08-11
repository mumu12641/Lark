package io.github.mumu12641.lark

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.DEFAULT_SEED_COLOR
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.FOLLOW_SYSTEM

val LocalDarkTheme = compositionLocalOf { FOLLOW_SYSTEM }
val LocalSeedColor = compositionLocalOf { DEFAULT_SEED_COLOR }
val displayPreferenceFlow = PreferenceUtil.displayPreferenceFlow

@Composable
fun PreferenceProvider(content: @Composable () -> Unit) {
    val displayPreferenceFlowState = displayPreferenceFlow.collectAsState().value
    CompositionLocalProvider(
        LocalDarkTheme provides displayPreferenceFlowState.darkModePreference,
        LocalSeedColor provides displayPreferenceFlowState.seedColor,
        content = content
    )
}
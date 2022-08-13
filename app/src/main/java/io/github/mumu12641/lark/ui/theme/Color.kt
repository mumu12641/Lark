package io.github.mumu12641.lark.ui.theme

import androidx.compose.ui.graphics.Color

fun Color.applyOpacity(enabled: Boolean): Color {
    return if (enabled) this else this.copy(alpha = 0.62f)
}
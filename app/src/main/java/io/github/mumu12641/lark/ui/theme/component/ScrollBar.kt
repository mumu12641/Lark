package io.github.mumu12641.lark.ui.theme.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


private fun ContentDrawScope.drawScrollbar(
    colorContainer: Color,
    width: Dp,
    colorBar: Color,
    scrollbarOffsetY: Float,
    scrollbarHeight: Float,
    alpha: Float,
    needDrawScrollbar: Boolean
) {
    if (needDrawScrollbar) {
        drawRoundRect(
            color = colorContainer,
            topLeft = Offset(this.size.width - width.toPx(), 0f),
            size = Size(width.toPx(), this.size.height),
            cornerRadius = CornerRadius(25f, 25f)
        )

        drawRoundRect(
            color = colorBar,
            topLeft = Offset(this.size.width - width.toPx(), scrollbarOffsetY),
            size = Size(width.toPx(), scrollbarHeight),
            alpha = alpha,
            cornerRadius = CornerRadius(25f, 25f)
        )
    }
}


@Composable
fun Modifier.Scrollbar(state: ScrollableState, width: Dp = 8.dp): Modifier {
    val targetAlpha = if (state.isScrollInProgress) 1f else 0f
    val duration = if (state.isScrollInProgress) 150 else 500
    val colorBar = MaterialTheme.colorScheme.primary
    val colorContainer = MaterialTheme.colorScheme.secondaryContainer
    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(duration)
    )
    var scrollbarOffsetY = 0f
    var scrollbarHeight = 0f
    return drawWithContent {
        drawContent()
        val needDrawScrollbar = state.isScrollInProgress || alpha > 0.0f
        when (state) {
            is LazyListState -> {
                val firstVisibleElementIndex =
                    state.layoutInfo.visibleItemsInfo.firstOrNull()?.index
                if (needDrawScrollbar && firstVisibleElementIndex != null) {
                    val elementHeight = this.size.height / state.layoutInfo.totalItemsCount
                    scrollbarOffsetY = firstVisibleElementIndex * elementHeight
                    scrollbarHeight = state.layoutInfo.visibleItemsInfo.size * elementHeight

                }
            }
            is LazyGridState -> {
                val firstVisibleElementIndex =
                    state.layoutInfo.visibleItemsInfo.firstOrNull()?.index
                if (needDrawScrollbar && firstVisibleElementIndex != null) {
                    val elementHeight = this.size.height / state.layoutInfo.totalItemsCount
                    scrollbarOffsetY = firstVisibleElementIndex * elementHeight
                    scrollbarHeight = state.layoutInfo.visibleItemsInfo.size * elementHeight
                }
            }
        }
        drawScrollbar(
            colorContainer,
            width,
            colorBar,
            scrollbarOffsetY,
            scrollbarHeight,
            alpha,
            needDrawScrollbar
        )

    }
}
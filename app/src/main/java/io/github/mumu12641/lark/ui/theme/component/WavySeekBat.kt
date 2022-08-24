package io.github.mumu12641.lark.ui.theme.component
/*
* https://github.com/Iamlooker/Howl/blob/stable/core-ui/src/main/java/com/looker/core_ui/components/Seekbar.kt
* */

import androidx.annotation.FloatRange
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.SliderColors
import androidx.compose.material.SliderDefaults
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.*


private const val waveWidth = 75F
private const val waveHeight = 15F

internal val ThumbRadius = 8.dp
internal val TrackHeight = 4.dp
private val SliderHeight = 48.dp
private val SliderMinWidth = 144.dp

val DefaultSliderConstraints =
    Modifier
        .widthIn(min = SliderMinWidth)
        .heightIn(max = SliderHeight)

@Composable
fun WavySeekbar(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: SliderColors = SliderDefaults.colors()
) {
    val onValueChangeState = rememberUpdatedState(onValueChange)
    BoxWithConstraints(
        modifier
            .requiredSizeIn(minWidth = ThumbRadius * 2, minHeight = ThumbRadius * 2)
            .sliderSemantics(value, enabled, onValueChange, valueRange, steps)
            .focusable(enabled, interactionSource)
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val maxPx: Float
        val minPx: Float

        with(LocalDensity.current) {
            maxPx = max(widthPx - ThumbRadius.toPx(), 0f)
            minPx = min(ThumbRadius.toPx(), maxPx)
        }

        fun scaleToUserValue(offset: Float) =
            scale(minPx, maxPx, offset, valueRange.start, valueRange.endInclusive)

        fun scaleToOffset(userValue: Float) =
            scale(valueRange.start, valueRange.endInclusive, userValue, minPx, maxPx)

        val rawOffset = remember { mutableStateOf(scaleToOffset(value)) }
        val pressOffset = remember { mutableStateOf(0f) }

        val draggableState = remember(minPx, maxPx, valueRange) {
            SliderDraggableState {
                rawOffset.value = (rawOffset.value + it + pressOffset.value)
                pressOffset.value = 0f
                val offsetInTrack = rawOffset.value.coerceIn(minPx, maxPx)
                onValueChangeState.value.invoke(scaleToUserValue(offsetInTrack))
            }
        }

        CorrectValueSideEffect(::scaleToOffset, valueRange, minPx..maxPx, rawOffset, value)

        val gestureEndAction = rememberUpdatedState { _: Float ->
            if (!draggableState.isDragging) {
                onValueChangeFinished?.invoke()
            }
        }

        val press = Modifier.sliderTapModifier(
            draggableState = draggableState,
            interactionSource = interactionSource,
            maxPx = widthPx,
            rawOffset = rawOffset,
            gestureEndAction = gestureEndAction,
            pressOffset = pressOffset,
            enabled = enabled
        )

        val drag = Modifier.draggable(
            orientation = Orientation.Horizontal,
            reverseDirection = false,
            enabled = enabled,
            interactionSource = interactionSource,
            onDragStopped = { velocity -> gestureEndAction.value.invoke(velocity) },
            startDragImmediately = draggableState.isDragging,
            state = draggableState
        )

        val coerced = value.coerceIn(valueRange.start, valueRange.endInclusive)
        val fraction = calcFraction(valueRange.start, valueRange.endInclusive, coerced)
        CustomSliderImpl(
            enabled,
            fraction,
            colors,
            maxPx - minPx,
            interactionSource,
            modifier = press.then(drag)
        )
    }
}

@Composable
private fun CorrectValueSideEffect(
    scaleToOffset: (Float) -> Float,
    valueRange: ClosedFloatingPointRange<Float>,
    trackRange: ClosedFloatingPointRange<Float>,
    valueState: MutableState<Float>,
    value: Float
) {
    SideEffect {
        val error = (valueRange.endInclusive - valueRange.start) / 1000
        val newOffset = scaleToOffset(value)
        if (abs(newOffset - valueState.value) > error) {
            if (valueState.value in trackRange) {
                valueState.value = newOffset
            }
        }
    }
}

@Composable
private fun CustomSliderImpl(
    enabled: Boolean,
    positionFraction: Float,
    colors: SliderColors,
    width: Float,
    interactionSource: MutableInteractionSource,
    modifier: Modifier
) {
    Box(modifier.then(DefaultSliderConstraints)) {
        val trackStrokeWidth: Float
        val thumbPx: Float
        val widthDp: Dp
        with(LocalDensity.current) {
            trackStrokeWidth = (TrackHeight / 2).toPx()
            thumbPx = ThumbRadius.toPx()
            widthDp = width.toDp()
        }

        val thumbSize = ThumbRadius * 2
        val offset = widthDp * positionFraction

        CustomTrack(
            Modifier.fillMaxSize(),
            colors,
            enabled,
            positionFraction,
            thumbPx,
            trackStrokeWidth
        )
        SliderThumb(Modifier, offset, interactionSource, colors, enabled, thumbSize)
    }
}

@Composable
private fun CustomTrack(
    modifier: Modifier,
    colors: SliderColors,
    enabled: Boolean,
    positionFractionEnd: Float,
    thumbPx: Float,
    trackStrokeWidth: Float
) {
    val inactiveTrackColor = colors.trackColor(enabled, active = false)
    val activeTrackColor = colors.trackColor(enabled, active = true)
    val deltaXAnim = rememberInfiniteTransition()
    val dx by deltaXAnim.animateFloat(
        initialValue = 0f,
        targetValue = waveWidth,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing)
        )
    )
    Canvas(modifier) {
        val sliderLeft = Offset(thumbPx, center.y)
        val sliderRight = Offset(size.width - thumbPx, center.y)
        val sliderValueEnd = Offset(
            sliderLeft.x + (sliderRight.x - sliderLeft.x) * positionFractionEnd,
            center.y
        )
        drawLine(
            inactiveTrackColor.value,
            sliderValueEnd,
            sliderRight,
            trackStrokeWidth,
            StrokeCap.Round
        )

        val sliderValueStart = Offset(
            sliderLeft.x + (sliderRight.x - sliderLeft.x) * 0f,
            center.y
        )
        clipRect(left = sliderValueStart.x, right = sliderValueEnd.x) {
            drawWave(
                from = sliderValueStart,
                to = sliderRight,
                strokeWidth = trackStrokeWidth,
                color = activeTrackColor,
                translate = dx
            )
        }
    }
}

@Composable
private fun BoxScope.SliderThumb(
    modifier: Modifier,
    offset: Dp,
    interactionSource: MutableInteractionSource,
    colors: SliderColors,
    enabled: Boolean,
    thumbSize: Dp
) {
    Box(
        Modifier
            .padding(start = offset)
            .align(Alignment.CenterStart)
    ) {
        val interactions = remember { mutableStateListOf<Interaction>() }
        LaunchedEffect(interactionSource) {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> interactions.add(interaction)
                    is PressInteraction.Release -> interactions.remove(interaction.press)
                    is PressInteraction.Cancel -> interactions.remove(interaction.press)
                    is DragInteraction.Start -> interactions.add(interaction)
                    is DragInteraction.Stop -> interactions.remove(interaction.start)
                    is DragInteraction.Cancel -> interactions.remove(interaction.start)
                }
            }
        }

        Spacer(
            modifier
                .size(thumbSize, thumbSize)
                .indication(
                    interactionSource = interactionSource,
                    indication = rememberRipple(bounded = false, radius = 24.dp)
                )
                .hoverable(interactionSource = interactionSource)
                .background(colors.thumbColor(enabled).value, CircleShape)
        )
    }
}

private fun scale(a1: Float, b1: Float, x1: Float, a2: Float, b2: Float) =
    lerp(a2, b2, calcFraction(a1, b1, x1))

private fun calcFraction(a: Float, b: Float, pos: Float) =
    (if (b - a == 0f) 0f else (pos - a) / (b - a)).coerceIn(0f, 1f)

private fun Modifier.sliderTapModifier(
    draggableState: DraggableState,
    interactionSource: MutableInteractionSource,
    maxPx: Float,
    rawOffset: State<Float>,
    gestureEndAction: State<(Float) -> Unit>,
    pressOffset: MutableState<Float>,
    enabled: Boolean
) = composed(
    factory = {
        if (enabled) {
            val scope = rememberCoroutineScope()
            pointerInput(draggableState, interactionSource, maxPx) {
                detectTapGestures(
                    onPress = { pos ->
                        val to = pos.x
                        pressOffset.value = to - rawOffset.value
                        try {
                            awaitRelease()
                        } catch (_: GestureCancellationException) {
                            pressOffset.value = 0f
                        }
                    },
                    onTap = {
                        scope.launch {
                            draggableState.drag(MutatePriority.UserInput) {
                                dragBy(0f)
                            }
                            gestureEndAction.value.invoke(0f)
                        }
                    }
                )
            }
        } else {
            this
        }
    },
    inspectorInfo = debugInspectorInfo {
        name = "sliderTapModifier"
        properties["draggableState"] = draggableState
        properties["interactionSource"] = interactionSource
        properties["maxPx"] = maxPx
        properties["rawOffset"] = rawOffset
        properties["gestureEndAction"] = gestureEndAction
        properties["pressOffset"] = pressOffset
        properties["enabled"] = enabled
    })

private fun Modifier.sliderSemantics(
    value: Float,
    enabled: Boolean,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0
): Modifier {
    val coerced = value.coerceIn(valueRange.start, valueRange.endInclusive)
    return semantics {
        if (!enabled) disabled()
        setProgress(
            action = { targetValue ->
                val newValue = targetValue.coerceIn(valueRange.start, valueRange.endInclusive)
                if (newValue == coerced) {
                    false
                } else {
                    onValueChange(newValue)
                    true
                }
            }
        )
    }.progressSemantics(value, valueRange, steps)
}

private class SliderDraggableState(
    val onDelta: (Float) -> Unit
) : DraggableState {

    var isDragging by mutableStateOf(false)
        private set

    private val dragScope: DragScope = object : DragScope {
        override fun dragBy(pixels: Float): Unit = onDelta(pixels)
    }

    private val scrollMutex = MutatorMutex()

    override suspend fun drag(
        dragPriority: MutatePriority,
        block: suspend DragScope.() -> Unit
    ): Unit = coroutineScope {
        isDragging = true
        scrollMutex.mutateWith(dragScope, dragPriority, block)
        isDragging = false
    }

    override fun dispatchRawDelta(delta: Float) {
        return onDelta(delta)
    }
}

fun DrawScope.drawWave(
    from: Offset,
    to: Offset,
    strokeWidth: Float,
    color: State<Color>,
    translate: Float
) {
    val points = mutableListOf<Offset>()

    for (x in (from.x.toInt())..(to.x.toInt() + waveWidth.toInt())) {
        val offsetY =
            ((sin(x * (2f * PI / waveWidth)) * (waveHeight / (2)) + (waveHeight / 2)).toFloat()
                    + (from.y - (waveHeight / 2)))
        val offsetX = x.toFloat()
        points.add(Offset(offsetX, offsetY))
    }
    translate(-translate) {
        drawPoints(
            points = points,
            strokeWidth = strokeWidth,
            pointMode = PointMode.Points,
            color = color.value,
            cap = StrokeCap.Round
        )
    }
}
fun lerp(
    startValue: Float,
    endValue: Float,
    @FloatRange(from = 0.0, to = 1.0) fraction: Float
): Float {
    return startValue + fraction * (endValue - startValue)
}
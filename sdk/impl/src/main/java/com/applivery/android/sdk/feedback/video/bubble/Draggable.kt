package com.applivery.android.sdk.feedback.video.bubble

import android.content.res.Configuration
import android.graphics.Point
import android.graphics.PointF
import android.view.WindowManager
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Transition.Segment
import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt

private val draggingTransitionSpec: (Segment<Point>.() -> FiniteAnimationSpec<Int>) = {
    spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessHigh
    )
}
private val snapToEdgeTransitionSpec: (Segment<Point>.() -> FiniteAnimationSpec<Int>) = {
    spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
}

internal enum class AnimationState {
    DRAGGING,
    SNAP_TO_EDGE,
}

internal enum class InterruptMovState {
    DRAGGING,
}

private const val DraggableZIndex = 10f

@Composable
internal fun Draggable(
    modifier: Modifier = Modifier,
    windowManager: WindowManager,
    containerView: ComposeView,
    layoutParams: WindowManager.LayoutParams,
    updateSize: (size: IntSize) -> Unit,
    onDragStart: ((offset: Offset) -> Unit)? = null,
    onDrag: ((
        change: PointerInputChange?,
        dragAmount: Offset,
        newPoint: Point,
        newAnimatedPoint: Point?
    ) -> Unit)? = null,
    onDragEnd: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val orientation by remember(configuration) {
        derivedStateOf {
            when (configuration.orientation) {
                Configuration.ORIENTATION_PORTRAIT -> "portrait"
                Configuration.ORIENTATION_LANDSCAPE -> "landscape"
                else -> "undefined"
            }
        }
    }
    var screenSize by remember {
        mutableStateOf(getScreenSizeWithoutInsets(context))
    }
    var contentSize by remember { mutableStateOf(IntSize.Zero) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = modifier
            .zIndex(DraggableZIndex)
            .layout { measurable, constraints ->
                val newConstraints = constraints.copy(
                    maxWidth = Int.MAX_VALUE,
                    maxHeight = Int.MAX_VALUE
                )
                val placeable = measurable.measure(newConstraints)
                layout(placeable.width, placeable.height) {
                    placeable.placeRelative(0, 0)
                }
            }
            .onSizeChanged { size ->
                contentSize = size
                updateSize(size)
            }
            .systemGestureExclusion()
            .focusRequester(focusRequester)
            .focusable()
            .let { mod ->
                var dragAmountState by remember { mutableStateOf<Offset?>(null) }

                var initialPoint by remember {
                    mutableStateOf(Point(layoutParams.x, layoutParams.y))
                }
                var crrPoint by remember { mutableStateOf(Point(layoutParams.x, layoutParams.y)) }
                var accDrag by remember { mutableStateOf(PointF(0f, 0f)) }
                var constrainedCrrPoint by remember {
                    mutableStateOf(Point(layoutParams.x, layoutParams.y))
                }
                var animPoint by remember { mutableStateOf(Point(layoutParams.x, layoutParams.y)) }

                val transitionSpec: Segment<Point>.() -> FiniteAnimationSpec<Int>
                var animationState by remember { mutableStateOf(AnimationState.DRAGGING) }

                var newChange by remember { mutableStateOf<PointerInputChange?>(null) }
                var newDragAmount by remember { mutableStateOf(Offset.Zero) }

                val interruptMovState by remember { mutableStateOf<InterruptMovState?>(null) }

                // snap main float to some edge when screen orientation or contentSize changes
                LaunchedEffect(orientation, contentSize) {
                    val oldScreenSize = screenSize
                    screenSize = getScreenSizeWithoutInsets(context)

                    // snap to edge
                    if (oldScreenSize != screenSize) {
                        if (oldScreenSize.width != 0 && oldScreenSize.height != 0) {
                            val wasOnRightEdge =
                                crrPoint.x + contentSize.width >= oldScreenSize.width
                            val wasOnBottomEdge =
                                crrPoint.y + contentSize.height >= oldScreenSize.height

                            // adjust main float position to new screen size
                            if (wasOnRightEdge) {
                                crrPoint = Point(
                                    screenSize.width - contentSize.width,
                                    crrPoint.y.coerceIn(
                                        0,
                                        coerceInMax(screenSize.height - contentSize.height)
                                    )
                                )
                            }
                            if (wasOnBottomEdge) {
                                crrPoint = Point(
                                    crrPoint.x.coerceIn(
                                        0,
                                        coerceInMax(screenSize.width - contentSize.width)
                                    ),
                                    screenSize.height - contentSize.height
                                )
                            }

                            animationState = AnimationState.SNAP_TO_EDGE
                            animPoint = crrPoint
                            initialPoint = crrPoint
                        }
                    }
                }

                val transition = updateTransition(targetState = animPoint, label = "transition")
                transitionSpec = when (animationState) {
                    AnimationState.SNAP_TO_EDGE -> snapToEdgeTransitionSpec
                    AnimationState.DRAGGING -> draggingTransitionSpec
                }

                val animatedX by transition.animateInt(
                    transitionSpec = { transitionSpec() },
                    label = "x"
                ) { it.x }
                val animatedY by transition.animateInt(
                    transitionSpec = { transitionSpec() },
                    label = "y"
                ) { it.y }

                LaunchedEffect(key1 = animatedX, key2 = animatedY) {
                    windowManager.updateViewLayout(containerView, layoutParams.apply {
                        x = animatedX
                        y = animatedY
                    })
                    onDrag?.invoke(
                        newChange,
                        newDragAmount,
                        constrainedCrrPoint,
                        Point(animatedX, animatedY)
                    )
                }

                mod.pointerInput(Unit) {
                    val decay = splineBasedDecay<Float>(this)
                    val velocityTracker = VelocityTracker()

                    detectDragGestures(
                        onDragStart = { offset ->
                            onDragStart?.invoke(offset)
                        },
                        onDrag = { change, dragAmount ->
                            dragAmountState = dragAmount
                            accDrag = PointF(
                                accDrag.x + dragAmount.x,
                                accDrag.y + dragAmount.y,
                            )
                            crrPoint = Point(
                                initialPoint.x + accDrag.x.roundToInt(),
                                initialPoint.y + accDrag.y.roundToInt()
                            )
                            constrainedCrrPoint = Point(
                                crrPoint.x.coerceIn(
                                    0,
                                    coerceInMax(screenSize.width - contentSize.width)
                                ),
                                crrPoint.y.coerceIn(
                                    0,
                                    coerceInMax(screenSize.height - contentSize.height)
                                )
                            )

                            if (interruptMovState != InterruptMovState.DRAGGING) {
                                animationState = AnimationState.DRAGGING
                                animPoint = crrPoint
                                newChange = change
                                newDragAmount = dragAmount
                            }

                            velocityTracker.addPosition(change.uptimeMillis, change.position)

                            onDrag?.invoke(
                                change,
                                dragAmount,
                                constrainedCrrPoint,
                                null,
                            )
                        },
                        onDragEnd = {
                            onDragEnd?.invoke()
                            accDrag = PointF(0f, 0f)
                            var newPoint = Point(0, constrainedCrrPoint.y)
                            animationState = AnimationState.SNAP_TO_EDGE

                            val decayCenterX = decay.calculateTargetValue(
                                initialValue = (crrPoint.x + contentSize.width / 2).toFloat(),
                                initialVelocity = velocityTracker.calculateVelocity().x
                            )
                            if (decayCenterX >= screenSize.width * 0.5f) {
                                newPoint = Point(screenSize.width - contentSize.width, newPoint.y)
                            }
                            animPoint = newPoint
                            initialPoint = newPoint
                            constrainedCrrPoint = newPoint
                            crrPoint = newPoint
                        }
                    )
                }
            }
    ) {
        content()
    }
}

private fun coerceInMax(max: Int): Int = if (max < 0) 0 else max

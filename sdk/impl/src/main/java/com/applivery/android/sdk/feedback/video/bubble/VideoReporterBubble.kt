package com.applivery.android.sdk.feedback.video.bubble

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.applivery.android.sdk.R
import com.applivery.android.sdk.ui.theme.AppliveryTheme
import com.applivery.android.sdk.ui.theme.colorError
import kotlinx.coroutines.withContext

private val ProgressWidth = 3.dp
private val FloatingActionButtonSize = 76.dp

@Composable
fun VideoReporterBubble(
    countDowTimeInSeconds: Int = 10,
    modifier: Modifier = Modifier,
    onFinished: () -> Unit
) {
    AppliveryTheme {
        val progress = remember { Animatable(0f) }
        LaunchedEffect(countDowTimeInSeconds) {
            withContext(FixedMotionDurationScale) {
                progress.animateTo(
                    targetValue = 360f,
                    animationSpec = tween(
                        durationMillis = countDowTimeInSeconds * 1000,
                        easing = LinearEasing
                    )
                )
            }
        }
        FloatingActionButton(
            modifier = modifier
                .size(FloatingActionButtonSize)
                .drawWithContent {
                    drawContent()
                    val offset = Offset(ProgressWidth.toPx() / 2f, ProgressWidth.toPx() / 2f)
                    drawArc(
                        color = Color.DarkGray,
                        style = Stroke(width = ProgressWidth.toPx()),
                        topLeft = offset,
                        size = Size(
                            size.width - offset.x - ProgressWidth.toPx() / 2f,
                            size.height - offset.y - ProgressWidth.toPx() / 2f
                        ),
                        startAngle = 270f,
                        sweepAngle = progress.value,
                        useCenter = false
                    )
                },
            onClick = onFinished,
            shape = CircleShape,
            contentColor = colorError,
            containerColor = Color.LightGray
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_stop),
                contentDescription = null
            )
        }
    }
}

private object FixedMotionDurationScale : MotionDurationScale {
    override val scaleFactor: Float get() = 1f
}


@Preview
@Composable
private fun ScreenRecorderBubblePreview() {
    VideoReporterBubble { }
}

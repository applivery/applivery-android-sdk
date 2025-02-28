package com.applivery.android.sdk.feedback.video.bubble

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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

private val ProgressWidth = 3.dp

@Composable
fun VideoReporterBubble(
    countDowTimeInSeconds: Int = 10,
    modifier: Modifier = Modifier,
    onFinished: () -> Unit
) {
    AppliveryTheme {
        var progress by remember { mutableFloatStateOf(0f) }
        val progressAnimation by animateFloatAsState(
            progress,
            animationSpec = tween(
                durationMillis = countDowTimeInSeconds * 1000,
                easing = LinearEasing
            )
        )
        LaunchedEffect(countDowTimeInSeconds) { progress = 360f }
        LargeFloatingActionButton(
            modifier = modifier.drawWithContent {
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
                    sweepAngle = progressAnimation,
                    useCenter = false
                )
            },
            onClick = onFinished,
            shape = CircleShape,
            contentColor = colorError,
            containerColor = Color.LightGray,
            elevation = FloatingActionButtonDefaults.elevation(0.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_stop),
                contentDescription = null
            )
        }
    }
}

@Preview
@Composable
private fun ScreenRecorderBubblePreview() {
    VideoReporterBubble { }
}

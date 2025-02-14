package com.applivery.android.sdk.feedback.draw

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.applivery.android.sdk.R
import kotlin.math.roundToInt

@Composable
internal fun DrawingPropertiesMenu(
    modifier: Modifier = Modifier,
    pathProperties: PathProperties,
    drawMode: DrawMode,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onApply: () -> Unit,
    onDrawModeChanged: (DrawMode) -> Unit,
    onPropertiesChanged: (PathProperties) -> Unit,
) {

    val properties by rememberUpdatedState(newValue = pathProperties)

    var showColorDialog by remember { mutableStateOf(false) }
    var showPropertiesDialog by remember { mutableStateOf(false) }
    var currentDrawMode = drawMode

    ElevatedCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                onClick = {
                    currentDrawMode = if (currentDrawMode == DrawMode.Touch) {
                        DrawMode.Draw
                    } else {
                        DrawMode.Touch
                    }
                    onDrawModeChanged(currentDrawMode)
                }
            ) {
                Icon(
                    modifier = Modifier
                        .alpha(if (currentDrawMode == DrawMode.Touch) 1f else 0.3f),
                    painter = painterResource(id = R.drawable.ic_touch),
                    contentDescription = null,
                )
            }
            IconButton(
                onClick = {
                    currentDrawMode = if (currentDrawMode == DrawMode.Erase) {
                        DrawMode.Draw
                    } else {
                        DrawMode.Erase
                    }
                    onDrawModeChanged(currentDrawMode)
                }
            ) {
                Icon(
                    modifier = Modifier
                        .alpha(if (currentDrawMode == DrawMode.Erase) 1f else 0.3f),
                    painter = painterResource(id = R.drawable.ic_eraser),
                    contentDescription = null
                )
            }


            IconButton(onClick = { showColorDialog = !showColorDialog }) {
                ColorWheel(modifier = Modifier.size(24.dp))
            }

            IconButton(onClick = { showPropertiesDialog = !showPropertiesDialog }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_brush),
                    contentDescription = null
                )
            }

            IconButton(onClick = onUndo) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_undo),
                    contentDescription = null
                )
            }

            IconButton(onClick = onRedo) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_redo),
                    contentDescription = null
                )
            }

            IconButton(onClick = onApply) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_check),
                    contentDescription = null
                )
            }
        }
    }

    if (showColorDialog) {
        ColorSelectionDialog(
            initialColor = properties.color,
            onDismiss = { showColorDialog = !showColorDialog },
            onNegativeClick = { showColorDialog = !showColorDialog },
            onPositiveClick = { color: Color ->
                showColorDialog = !showColorDialog
                onPropertiesChanged(properties.copy(color = color))
            }
        )
    }

    if (showPropertiesDialog) {
        PropertiesMenuDialog(
            properties = properties,
            onPropertiesChanged = onPropertiesChanged,
            onDismiss = { showPropertiesDialog = !showPropertiesDialog }
        )
    }
}

@Composable
internal fun PropertiesMenuDialog(
    properties: PathProperties,
    onPropertiesChanged: (PathProperties) -> Unit,
    onDismiss: () -> Unit
) {

    var strokeWidth by remember { mutableFloatStateOf(properties.strokeWidth) }
    var strokeCap by remember { mutableStateOf(properties.strokeCap) }
    var strokeJoin by remember { mutableStateOf(properties.strokeJoin) }

    Dialog(onDismissRequest = onDismiss) {

        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(all = 8.dp)
        ) {

            Text(
                modifier = Modifier.padding(start = 12.dp, top = 12.dp),
                text = stringResource(id = R.string.appliveryFeedbackImageEditorProperties),
                style = MaterialTheme.typography.titleLarge
            )

            Canvas(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 20.dp)
                    .height(40.dp)
                    .fillMaxWidth()
            ) {
                val path = Path()
                path.moveTo(0f, size.height / 2)
                path.lineTo(size.width, size.height / 2)

                drawPath(
                    color = properties.color,
                    path = path,
                    style = Stroke(
                        width = strokeWidth,
                        cap = strokeCap,
                        join = strokeJoin
                    )
                )
            }

            Text(
                modifier = Modifier.padding(horizontal = 12.dp),
                text = stringResource(
                    id = R.string.appliveryFeedbackImageEditorStrokeWidth,
                    strokeWidth.toInt()
                ),
            )

            Slider(
                value = strokeWidth,
                onValueChange = {
                    strokeWidth = it
                    onPropertiesChanged(properties.copy(strokeWidth = strokeWidth))
                },
                valueRange = 1f..100f,
                onValueChangeFinished = {}
            )


            ExposedSelectionMenu(title = stringResource(R.string.appliveryFeedbackImageEditorStrokeCap),
                index = when (strokeCap) {
                    StrokeCap.Butt -> 0
                    StrokeCap.Round -> 1
                    else -> 2
                },
                options = listOf(
                    stringResource(R.string.appliveryFeedbackImageEditorStrokeCapButt),
                    stringResource(R.string.appliveryFeedbackImageEditorStrokeCapRound),
                    stringResource(R.string.appliveryFeedbackImageEditorStrokeCapSquare)
                ),
                onSelected = {
                    strokeCap = when (it) {
                        0 -> StrokeCap.Butt
                        1 -> StrokeCap.Round
                        else -> StrokeCap.Square
                    }
                    onPropertiesChanged(properties.copy(strokeCap = strokeCap))
                }
            )

            ExposedSelectionMenu(title = stringResource(R.string.appliveryFeedbackImageEditorStrokeJoin),
                index = when (strokeJoin) {
                    StrokeJoin.Miter -> 0
                    StrokeJoin.Round -> 1
                    else -> 2
                },
                options = listOf(
                    stringResource(R.string.appliveryFeedbackImageEditorStrokeJoinMiter),
                    stringResource(R.string.appliveryFeedbackImageEditorStrokeJoinRound),
                    stringResource(R.string.appliveryFeedbackImageEditorStrokeJoinBevel)
                ),
                onSelected = {
                    strokeJoin = when (it) {
                        0 -> StrokeJoin.Miter
                        1 -> StrokeJoin.Round
                        else -> StrokeJoin.Bevel
                    }
                    onPropertiesChanged(properties.copy(strokeJoin = strokeJoin))
                }
            )
        }
    }
}


@Composable
internal fun ColorSelectionDialog(
    initialColor: Color,
    onDismiss: () -> Unit,
    onNegativeClick: () -> Unit,
    onPositiveClick: (Color) -> Unit
) {
    var red by remember { mutableFloatStateOf(initialColor.red * 255) }
    var green by remember { mutableFloatStateOf(initialColor.green * 255) }
    var blue by remember { mutableFloatStateOf(initialColor.blue * 255) }
    var alpha by remember { mutableFloatStateOf(initialColor.alpha * 255) }

    val color = Color(
        red = red.roundToInt(),
        green = green.roundToInt(),
        blue = blue.roundToInt(),
        alpha = alpha.roundToInt()
    )

    Dialog(onDismissRequest = onDismiss) {

        BoxWithConstraints(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(all = 8.dp)
        ) {

            val widthInDp = LocalDensity.current.run { maxWidth }
            Column {

                Text(
                    modifier = Modifier.padding(start = 12.dp, top = 12.dp),
                    text = stringResource(R.string.appliveryFeedbackImageEditorColor),
                    style = MaterialTheme.typography.titleLarge
                )

                // Initial and Current Colors
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 50.dp, vertical = 20.dp)
                ) {

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .background(
                                color = initialColor,
                                shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                            )
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .background(
                                color,
                                shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                            )
                    )
                }

                ColorWheel(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(widthInDp * .8f)
                        .aspectRatio(1f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Sliders
                ColorSlider(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 12.dp)
                        .fillMaxWidth(),
                    title = stringResource(R.string.appliveryFeedbackImageEditorColorRed),
                    titleColor = Color.Red,
                    rgb = red,
                    onColorChanged = { red = it }
                )
                Spacer(modifier = Modifier.height(4.dp))
                ColorSlider(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 12.dp)
                        .fillMaxWidth(),
                    title = stringResource(R.string.appliveryFeedbackImageEditorColorGreen),
                    titleColor = Color.Green,
                    rgb = green,
                    onColorChanged = { green = it }
                )
                Spacer(modifier = Modifier.height(4.dp))

                ColorSlider(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 12.dp)
                        .fillMaxWidth(),
                    title = stringResource(R.string.appliveryFeedbackImageEditorColorBlue),
                    titleColor = Color.Blue,
                    rgb = blue,
                    onColorChanged = { blue = it }
                )

                Spacer(modifier = Modifier.height(4.dp))

                ColorSlider(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 12.dp)
                        .fillMaxWidth(),
                    title = stringResource(R.string.appliveryFeedbackImageEditorColorAlpha),
                    titleColor = Color.Black,
                    rgb = alpha,
                    onColorChanged = {
                        alpha = it
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically

                ) {

                    TextButton(
                        modifier = Modifier.weight(1f),
                        onClick = onNegativeClick,
                    ) {
                        Text(text = stringResource(R.string.appliveryFeedbackImageEditorCancel))
                    }
                    TextButton(
                        modifier = Modifier.weight(1f),
                        onClick = { onPositiveClick(color) },
                    ) {
                        Text(text = stringResource(R.string.appliveryFeedbackImageEditorApply))
                    }
                }
            }
        }
    }
}

/**
 * Expandable selection menu
 * @param title of the displayed item on top
 * @param index index of selected item
 * @param options list of [String] options
 * @param onSelected lambda to be invoked when an item is selected that returns
 * its index.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ExposedSelectionMenu(
    title: String,
    index: Int,
    options: List<String>,
    onSelected: (Int) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(options[index]) }

    ExposedDropdownMenuBox(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = selectedOptionText,
            onValueChange = { },
            label = { Text(title) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            )
        )
        DropdownMenu(
            modifier = Modifier.exposedDropdownSize(),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEachIndexed { index: Int, selectionOption: String ->
                DropdownMenuItem(
                    onClick = {
                        selectedOptionText = selectionOption
                        expanded = false
                        onSelected(index)
                    },
                    text = { Text(text = selectionOption) }
                )
            }
        }
    }
}
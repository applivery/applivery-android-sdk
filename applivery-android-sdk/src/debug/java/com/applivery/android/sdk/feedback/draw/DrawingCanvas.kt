package com.applivery.android.sdk.feedback.draw

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.positionChange

@Stable
internal class DrawingCanvasState {

    val paths = mutableStateListOf<Pair<Path, PathProperties>>()
    val pathsUndone = mutableStateListOf<Pair<Path, PathProperties>>()
    val pathProperties = mutableStateOf(PathProperties())
    val motionEvent = mutableStateOf(MotionEvent.Idle)
    val drawMode = mutableStateOf(DrawMode.Draw)

    fun undo() {
        if (paths.isNotEmpty()) {
            val lastItem = paths.last()
            val lastPath = lastItem.first
            val lastPathProperty = lastItem.second
            paths.remove(lastItem)
            pathsUndone.add(Pair(lastPath, lastPathProperty))
        }
    }

    fun redo() {
        if (pathsUndone.isNotEmpty()) {
            val lastPath = pathsUndone.last().first
            val lastPathProperty = pathsUndone.last().second
            pathsUndone.removeAt(pathsUndone.lastIndex)
            paths.add(Pair(lastPath, lastPathProperty))
        }
    }

    fun setDrawMode(mode: DrawMode) {
        motionEvent.value = MotionEvent.Idle
        drawMode.value = mode
        setDrawProperties(pathProperties.value.copy(eraseMode = (mode == DrawMode.Erase)))
    }

    fun setDrawProperties(properties: PathProperties) {
        pathProperties.value = properties
    }
}

@Composable
internal fun rememberDrawingCanvasState(): DrawingCanvasState {
    return remember { DrawingCanvasState() }
}

@Composable
internal fun DrawingCanvas(
    modifier: Modifier = Modifier,
    state: DrawingCanvasState = rememberDrawingCanvasState(),
) {

    /**
     * Current position of the pointer that is pressed or being moved
     */
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }

    /**
     * Previous motion event before next touch is saved into this current position.
     */
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }

    /**
     * Path that is being drawn between [MotionEvent.Down] and [MotionEvent.Up]. When
     * pointer is up this path is saved to **paths** and new instance is created
     */
    var currentPath by remember { mutableStateOf(Path()) }

    var pathProperties by state.pathProperties
    var motionEvent by state.motionEvent
    val drawMode by state.drawMode

    val drawModifier = modifier.dragMotionEvent(
        onDragStart = { pointerInputChange ->
            motionEvent = MotionEvent.Down
            currentPosition = pointerInputChange.position
            if (pointerInputChange.pressed != pointerInputChange.previousPressed) {
                pointerInputChange.consume()
            }

        },
        onDrag = { pointerInputChange ->
            motionEvent = MotionEvent.Move
            currentPosition = pointerInputChange.position

            if (drawMode == DrawMode.Touch) {
                val change = pointerInputChange.positionChange()
                state.paths.forEach { entry ->
                    val path: Path = entry.first
                    path.translate(change)
                }
                currentPath.translate(change)
            }
            if (pointerInputChange.positionChange() != Offset.Zero) {
                pointerInputChange.consume()
            }

        },
        onDragEnd = { pointerInputChange ->
            motionEvent = MotionEvent.Up
            if (pointerInputChange.pressed != pointerInputChange.previousPressed) {
                pointerInputChange.consume()
            }
        }
    )

    Canvas(modifier = drawModifier) {

        when (motionEvent) {

            MotionEvent.Down -> {
                if (drawMode != DrawMode.Touch) {
                    currentPath.moveTo(currentPosition.x, currentPosition.y)
                }

                previousPosition = currentPosition

            }

            MotionEvent.Move -> {

                if (drawMode != DrawMode.Touch) {
                    currentPath.quadraticBezierTo(
                        previousPosition.x,
                        previousPosition.y,
                        (previousPosition.x + currentPosition.x) / 2,
                        (previousPosition.y + currentPosition.y) / 2

                    )
                }

                previousPosition = currentPosition
            }

            MotionEvent.Up -> {
                if (drawMode != DrawMode.Touch) {
                    currentPath.lineTo(currentPosition.x, currentPosition.y)

                    // Pointer is up save current path
                    //paths[currentPath] = currentPathProperty
                    state.paths.add(Pair(currentPath, state.pathProperties.value))

                    // Since paths are keys for map, use new one for each key
                    // and have separate path for each down-move-up gesture cycle
                    currentPath = Path()

                    // Create new instance of path properties to have new path and properties
                    // only for the one currently being drawn
                    pathProperties = PathProperties(
                        strokeWidth = pathProperties.strokeWidth,
                        color = pathProperties.color,
                        strokeCap = pathProperties.strokeCap,
                        strokeJoin = pathProperties.strokeJoin,
                        eraseMode = pathProperties.eraseMode
                    )
                }

                // Since new path is drawn no need to store paths to undone
                state.pathsUndone.clear()

                // If we leave this state at MotionEvent.Up it causes current path to draw
                // line from (0,0) if this composable recomposes when draw mode is changed
                currentPosition = Offset.Unspecified
                previousPosition = currentPosition
                motionEvent = MotionEvent.Idle
            }

            else -> Unit
        }

        with(drawContext.canvas.nativeCanvas) {

            val checkPoint = saveLayer(null, null)

            state.paths.forEach {

                val path = it.first
                val property = it.second

                if (!property.eraseMode) {
                    drawPath(
                        color = property.color,
                        path = path,
                        style = Stroke(
                            width = property.strokeWidth,
                            cap = property.strokeCap,
                            join = property.strokeJoin
                        )
                    )
                } else {

                    // Source
                    drawPath(
                        color = Color.Transparent,
                        path = path,
                        style = Stroke(
                            width = pathProperties.strokeWidth,
                            cap = pathProperties.strokeCap,
                            join = pathProperties.strokeJoin
                        ),
                        blendMode = BlendMode.Clear
                    )
                }
            }

            if (motionEvent != MotionEvent.Idle) {

                if (!pathProperties.eraseMode) {
                    drawPath(
                        color = pathProperties.color,
                        path = currentPath,
                        style = Stroke(
                            width = pathProperties.strokeWidth,
                            cap = pathProperties.strokeCap,
                            join = pathProperties.strokeJoin
                        )
                    )
                } else {
                    drawPath(
                        color = Color.Transparent,
                        path = currentPath,
                        style = Stroke(
                            width = pathProperties.strokeWidth,
                            cap = pathProperties.strokeCap,
                            join = pathProperties.strokeJoin
                        ),
                        blendMode = BlendMode.Clear
                    )
                }
            }
            restoreToCount(checkPoint)
        }
    }

}

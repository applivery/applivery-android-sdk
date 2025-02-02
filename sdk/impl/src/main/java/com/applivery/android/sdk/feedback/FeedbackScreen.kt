package com.applivery.android.sdk.feedback

import android.graphics.Bitmap
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.applivery.android.sdk.R
import com.applivery.android.sdk.feedback.draw.DrawingCanvas
import com.applivery.android.sdk.feedback.draw.DrawingPropertiesMenu
import com.applivery.android.sdk.feedback.draw.rememberDrawingCanvasState
import com.applivery.android.sdk.presentation.ViewModelIntentSender
import com.applivery.android.sdk.presentation.viewModelIntentSender
import com.applivery.android.sdk.ui.theme.AppliveryTheme
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FeedbackScreen(
    state: FeedbackState,
    intentSender: ViewModelIntentSender<FeedbackIntent>
) {
    var showDrawingScreenshot by remember { mutableStateOf(false) }
    AppliveryTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                val containerColor = MaterialTheme.colorScheme.primary
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.applivery_name)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = containerColor,
                        titleContentColor = contentColorFor(containerColor),
                        actionIconContentColor = contentColorFor(containerColor),
                        navigationIconContentColor = contentColorFor(containerColor)
                    ),
                    actions = {
                        IconButton(
                            onClick = { intentSender.sendIntent(FeedbackIntent.SendFeedback) },
                            enabled = state.isSendEnabled
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_send),
                                contentDescription = stringResource(id = R.string.appliverySendFeedbackButtonDesc)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { intentSender.sendIntent(FeedbackIntent.CancelFeedback) }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_close),
                                contentDescription = stringResource(id = R.string.appliveryCancelFeedbackButtonDesc)
                            )
                        }
                    }
                )
            },
            content = { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(all = 16.dp)
                    ) {
                        val focusManager = LocalFocusManager.current

                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            val options = listOf(FeedbackType.Feedback, FeedbackType.Bug)
                            options.forEachIndexed { index, feedbackType ->
                                val textResId = when (feedbackType) {
                                    FeedbackType.Feedback -> R.string.appliveryFeedbackSelectorText
                                    FeedbackType.Bug -> R.string.appliveryBugSelectorText
                                }
                                SegmentedButton(
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = options.size
                                    ),
                                    onClick = {
                                        intentSender.sendIntent(
                                            FeedbackIntent.FeedbackTypeChanged(feedbackType)
                                        )
                                    },
                                    selected = state.feedbackType == feedbackType,
                                    label = {
                                        Text(text = stringResource(id = textResId))
                                    },
                                    icon = {}
                                )
                            }
                        }

                        TextField(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .fillMaxWidth(),
                            value = state.email.orEmpty(),
                            onValueChange = {
                                intentSender.sendIntent(FeedbackIntent.EmailChanged(it))
                            },
                            placeholder = {
                                Text(text = stringResource(id = R.string.appliveryFeedbackEmailHint))
                            },
                            label = {
                                Text(text = stringResource(id = R.string.appliveryFeedbackEmailLabel))
                            },
                            readOnly = state.isEmailReadOnly,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions { focusManager.clearFocus() },
                            isError = state.isEmailInvalid
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            TextField(
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .fillMaxWidth(),
                                value = state.feedback.orEmpty(),
                                onValueChange = {
                                    intentSender.sendIntent(FeedbackIntent.FeedbackChanged(it))
                                },
                                label = {
                                    Text(text = stringResource(id = R.string.appliveryFeedbackSelectorText))
                                },
                                singleLine = false
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .align(Alignment.Bottom),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier.padding(end = 8.dp, top = 8.dp),
                                text = stringResource(id = R.string.appliveryAttachScreenshotSwithText)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Switch(
                                checked = state.screenshot != null,
                                onCheckedChange = {
                                    intentSender.sendIntent(FeedbackIntent.AttachScreenshot(it))
                                }
                            )
                        }
                        state.screenshot?.let { bitmap ->
                            ElevatedCard(
                                modifier = Modifier
                                    .padding(20.dp)
                                    .widthIn(max = 100.dp)
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(size = 12.dp),
                                onClick = { showDrawingScreenshot = true }
                            ) {
                                Image(
                                    modifier = Modifier,
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                    if (state.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
        )

        if (showDrawingScreenshot) {
            /*Its important to call it here so it calculate available height based on the current
            * insets, because in the Dialog context there are no insets to consume*/
            ScreenshotDrawCanvasDialog(
                modifier = Modifier.availableHeight(),
                screenshot = requireNotNull(state.screenshot),
                onDismiss = {
                    showDrawingScreenshot = false
                },
                onApply = { bitmap ->
                    showDrawingScreenshot = false
                    bitmap?.let { intentSender.sendIntent(FeedbackIntent.ScreenshotModified(it)) }
                }
            )
        }
    }
}

@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalComposeApi::class
)
@Composable
private fun ScreenshotDrawCanvasDialog(
    modifier: Modifier = Modifier,
    screenshot: Bitmap,
    onDismiss: () -> Unit = {},
    onApply: (Bitmap?) -> Unit
) {
    Dialog(
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        ),
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val density = LocalDensity.current
            val canvasState = rememberDrawingCanvasState()
            var imageSize by remember { mutableStateOf(DpSize.Zero) }
            val coroutineScope = rememberCoroutineScope()
            val captureController = rememberCaptureController()
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .capturable(captureController)
            ) {
                Image(
                    modifier = Modifier.onSizeChanged {
                        imageSize = with(density) { it.toSize().toDpSize() }
                    },
                    bitmap = screenshot.asImageBitmap(),
                    contentDescription = null
                )
                DrawingCanvas(
                    modifier = Modifier.size(imageSize),
                    state = canvasState
                )
            }
            DrawingPropertiesMenu(
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxWidth(),
                pathProperties = canvasState.pathProperties.value,
                drawMode = canvasState.drawMode.value,
                onUndo = canvasState::undo,
                onRedo = canvasState::redo,
                onDrawModeChanged = canvasState::setDrawMode,
                onPropertiesChanged = canvasState::setDrawProperties,
                onApply = {
                    coroutineScope.launch {
                        val bitmapAsync = captureController.captureAsync()
                        try {
                            onApply(bitmapAsync.await().asAndroidBitmap())
                        } catch (error: Throwable) {
                            onApply(null)
                        }
                    }
                }
            )
        }
    }
}

/**
 * Temporary workaround to fix WindowInsets bug on Dialogs on Android 15
 * (https://issuetracker.google.com/issues/391393405)
 * We use a @Composable instead a composed Modifier because we want the value at the call place
 */
@Composable
fun Modifier.availableHeight() =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        val insets = WindowInsets.systemBars.asPaddingValues()
        val screenHeight = LocalConfiguration.current.screenHeightDp
        val availableHeight =
            screenHeight.dp - insets.calculateTopPadding() - insets.calculateBottomPadding()

        heightIn(max = availableHeight)
    } else {
        this
    }

@Preview
@Composable
private fun FeedbackScreenPreview() {
    FeedbackScreen(
        state = FeedbackState(
            screenshot = Bitmap.createBitmap(
                1080,
                1920,
                Bitmap.Config.ARGB_8888
            )
        ),
        intentSender = viewModelIntentSender { }
    )
}
package com.applivery.android.sdk.feedback

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.applivery.android.sdk.BaseActivity
import com.applivery.android.sdk.R
import com.applivery.android.sdk.presentation.ViewModelIntentSender
import com.applivery.android.sdk.presentation.launchAndCollectIn
import com.applivery.android.sdk.presentation.viewModelIntentSender
import com.applivery.android.sdk.ui.theme.AppliveryTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

internal class FeedbackActivity : BaseActivity() {

    private val arguments get() = intent.getParcelableExtra<FeedbackArguments>(ExtraArguments)
    private val viewModel: FeedbackViewModel by viewModel { parametersOf(arguments) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state by viewModel.viewState.collectAsState(FeedbackState())
            FeedbackScreen(
                state = state,
                intentSender = viewModel
            )
        }

        viewModel.viewActions.launchAndCollectIn(this) { action ->
            when (action) {
                is FeedbackAction.Exit -> finish()
            }
        }

        if (savedInstanceState == null) {
            viewModel.load()
        }
    }

    companion object {

        private const val ExtraArguments = "extra:arguments"

        fun getIntent(context: Context, arguments: FeedbackArguments): Intent {
            return Intent(context, FeedbackActivity::class.java)
                .putExtra(ExtraArguments, arguments)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedbackScreen(
    state: FeedbackState,
    intentSender: ViewModelIntentSender<FeedbackIntent>
) {
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
                        IconButton(onClick = { intentSender.sendIntent(FeedbackIntent.SendFeedback) }) {
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
                            keyboardActions = KeyboardActions { focusManager.clearFocus() }
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
                                onClick = {
                                    intentSender.sendIntent(FeedbackIntent.ClickScreenshot)
                                }
                            ) {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = null
                                )
                            }
                        }
                    }

                }
            }
        )
    }
}

@Preview
@Composable
private fun FeedbackScreenPreview() {
    FeedbackScreen(
        state = FeedbackState(),
        intentSender = viewModelIntentSender { }
    )
}
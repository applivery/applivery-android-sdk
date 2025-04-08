package com.applivery.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.applivery.android.sdk.Applivery
import com.applivery.android.sdk.domain.model.ShakeFeedbackBehavior
import com.applivery.android.sdk.getInstance
import com.applivery.sample.theme.AppliveryTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class MainActivity : ComponentActivity() {

    private val isUpToDate = MutableStateFlow(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val isUpToDate by isUpToDate.collectAsState()
            var isShakeFeedbackEnabled by remember { mutableStateOf(false) }
            var isScreenshotFeedbackEnabled by remember { mutableStateOf(false) }
            var isCheckForUpdatesBackgroundEnabled by remember { mutableStateOf(false) }
            var shakeFeedbackBehavior by remember { mutableStateOf(ShakeFeedbackBehavior.Normal) }
            MainScreen(
                isUpToDate = isUpToDate,
                isShakeFeedbackEnabled = isShakeFeedbackEnabled,
                isScreenshotFeedbackEnabled = isScreenshotFeedbackEnabled,
                isCheckForUpdatesBackgroundEnabled = isCheckForUpdatesBackgroundEnabled,
                shakeFeedbackBehavior = shakeFeedbackBehavior,
                onEnableShakeFeedback = {
                    if (it) {
                        Applivery.getInstance().enableShakeFeedback(shakeFeedbackBehavior)
                    } else {
                        Applivery.getInstance().disableShakeFeedback()
                    }
                    isShakeFeedbackEnabled = it

                },
                onEnableScreenshotFeedback = {
                    if (it) {
                        Applivery.getInstance().enableScreenshotFeedback()
                    } else {
                        Applivery.getInstance().disableScreenshotFeedback()
                    }
                    isScreenshotFeedbackEnabled = it
                },
                onEnableCheckForUpdatesBackground = {
                    Applivery.getInstance().setCheckForUpdatesBackground(true)
                    isCheckForUpdatesBackgroundEnabled = it
                },
                onCheckForUpdates = {
                    Applivery.getInstance().checkForUpdates(forceUpdate = it)
                },
                onDownloadLastBuild = {
                    Applivery.getInstance().update()
                },
                onUserClick = {
                    UserActivity.open(this)
                },
                onSelectShakeFeedbackBehavior = { behavior ->
                    Applivery.getInstance().enableShakeFeedback(behavior)
                    shakeFeedbackBehavior = behavior
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        Applivery.getInstance().isUpToDate { upToDate ->
            isUpToDate.update { upToDate }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    isUpToDate: Boolean,
    isShakeFeedbackEnabled: Boolean,
    isScreenshotFeedbackEnabled: Boolean,
    isCheckForUpdatesBackgroundEnabled: Boolean,
    shakeFeedbackBehavior: ShakeFeedbackBehavior,
    onEnableShakeFeedback: (Boolean) -> Unit,
    onEnableScreenshotFeedback: (Boolean) -> Unit,
    onEnableCheckForUpdatesBackground: (Boolean) -> Unit,
    onCheckForUpdates: (Boolean) -> Unit,
    onDownloadLastBuild: () -> Unit,
    onUserClick: () -> Unit,
    onSelectShakeFeedbackBehavior: (ShakeFeedbackBehavior) -> Unit
) {
    AppliveryTheme {
        Scaffold(
            topBar = {
                val containerColor = MaterialTheme.colorScheme.primaryContainer
                val contentColor = contentColorFor(containerColor)
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.app_name)) },
                    actions = {
                        IconButton(onClick = onUserClick) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_user),
                                contentDescription = null
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = containerColor,
                        titleContentColor = contentColor,
                        actionIconContentColor = contentColor,
                        navigationIconContentColor = contentColor
                    )
                )
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 24.dp)
                ) {

                    val text = if (isUpToDate) {
                        stringResource(id = R.string.is_up_to_date_text_updated)
                    } else {
                        stringResource(id = R.string.is_up_to_date_text_no_updated)
                    }
                    Text(
                        modifier = Modifier.padding(top = 24.dp),
                        text = text
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = stringResource(id = R.string.enable_shake_feedback)
                        )
                        Switch(
                            checked = isShakeFeedbackEnabled,
                            onCheckedChange = onEnableShakeFeedback
                        )
                    }
                    if (isShakeFeedbackEnabled) {
                        val options = ShakeFeedbackBehavior.entries.toList()
                        var expanded by remember { mutableStateOf(false) }

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = it },
                        ) {
                            TextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                value = shakeFeedbackBehavior.name,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Shake feedback behavior") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                },
                                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                            ) {
                                options.forEach { option ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = option.name,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        },
                                        onClick = {
                                            onSelectShakeFeedbackBehavior(option)
                                            expanded = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = stringResource(id = R.string.enable_screenshot_feedback)
                        )
                        Switch(
                            checked = isScreenshotFeedbackEnabled,
                            onCheckedChange = onEnableScreenshotFeedback
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = stringResource(id = R.string.check_for_updates_background)
                        )
                        Switch(
                            checked = isCheckForUpdatesBackgroundEnabled,
                            onCheckedChange = onEnableCheckForUpdatesBackground
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        onClick = { onCheckForUpdates(false) }
                    ) {
                        Text(text = stringResource(id = R.string.main_view_check_for_updates))
                    }
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        onClick = { onCheckForUpdates(true) }
                    ) {
                        Text(text = stringResource(id = R.string.main_view_force_check_for_updates))
                    }
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        onClick = onDownloadLastBuild
                    ) {
                        Text(text = stringResource(id = R.string.main_view_download_last_build))
                    }
                }
            }
        )
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    MainScreen(
        isUpToDate = false,
        isShakeFeedbackEnabled = false,
        isScreenshotFeedbackEnabled = false,
        isCheckForUpdatesBackgroundEnabled = false,
        shakeFeedbackBehavior = ShakeFeedbackBehavior.Normal,
        onEnableShakeFeedback = {},
        onEnableScreenshotFeedback = {},
        onEnableCheckForUpdatesBackground = {},
        onCheckForUpdates = {},
        onDownloadLastBuild = {},
        onUserClick = {},
        onSelectShakeFeedbackBehavior = {}
    )
}
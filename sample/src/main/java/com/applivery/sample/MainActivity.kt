package com.applivery.sample

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.applivery.android.sdk.Applivery
import com.applivery.android.sdk.domain.model.CachedAppUpdate
import com.applivery.android.sdk.getInstance
import com.applivery.sample.theme.AppliveryTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val isUpToDate = MutableStateFlow(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val isUpToDate by isUpToDate.collectAsState()
            var isScreenshotFeedbackEnabled by remember { mutableStateOf(false) }
            var isCheckForUpdatesBackgroundEnabled by remember { mutableStateOf(false) }
            var isLoading by remember { mutableStateOf(false) }
            var cachedAppUpdate by remember { mutableStateOf<CachedAppUpdate?>(null) }
            MainScreen(
                isUpToDate = isUpToDate,
                isScreenshotFeedbackEnabled = isScreenshotFeedbackEnabled,
                isCheckForUpdatesBackgroundEnabled = isCheckForUpdatesBackgroundEnabled,
                isLoading = isLoading,
                cachedAppUpdate = cachedAppUpdate,
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
                onUpdateLastBuild = {
                    Applivery.getInstance().update()
                },
                onUserClick = {
                    UserActivity.open(this)
                },
                openFeedbackEvent = {
                    Applivery.getInstance().feedbackEvent()
                },
                onDownloadLastBuild = {
                    isLoading = true
                    lifecycleScope.launch {
                        Applivery.getInstance().downloadLastUpdate().fold(
                            onSuccess = { update ->
                                isLoading = false
                                cachedAppUpdate = update
                            },
                            onFailure = {
                                isLoading = false
                                Toast.makeText(
                                    context,
                                    "Error downloading update",
                                    Toast.LENGTH_SHORT
                                ).show()
                                it.printStackTrace()
                            }
                        )
                    }
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
    isScreenshotFeedbackEnabled: Boolean,
    isCheckForUpdatesBackgroundEnabled: Boolean,
    isLoading: Boolean,
    cachedAppUpdate: CachedAppUpdate?,
    onEnableScreenshotFeedback: (Boolean) -> Unit,
    onEnableCheckForUpdatesBackground: (Boolean) -> Unit,
    onCheckForUpdates: (Boolean) -> Unit,
    onUpdateLastBuild: () -> Unit,
    openFeedbackEvent: () -> Unit,
    onUserClick: () -> Unit,
    onDownloadLastBuild: () -> Unit
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
                Box {
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
                            text = text,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
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
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            onClick = onUpdateLastBuild
                        ) {
                            Text(text = stringResource(id = R.string.main_view_update_last_build))
                        }
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            onClick = openFeedbackEvent
                        ) {
                            Text(text = stringResource(id = R.string.open_feedback_event))
                        }
                        cachedAppUpdate?.let {
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                onClick = { it.install() }
                            ) {
                                Text(
                                    text = stringResource(
                                        id = R.string.main_view_install_last_build,
                                        "versionCode ${it.appUpdate.buildVersion}"
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            text = "Applivery SDK Sample App v${BuildConfig.VERSION_NAME}",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.BottomCenter)
                        )
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
        isScreenshotFeedbackEnabled = false,
        isCheckForUpdatesBackgroundEnabled = false,
        isLoading = false,
        cachedAppUpdate = null,
        onEnableScreenshotFeedback = {},
        onEnableCheckForUpdatesBackground = {},
        onCheckForUpdates = {},
        onDownloadLastBuild = {},
        onUserClick = {},
        openFeedbackEvent = {},
        onUpdateLastBuild = {}
    )
}
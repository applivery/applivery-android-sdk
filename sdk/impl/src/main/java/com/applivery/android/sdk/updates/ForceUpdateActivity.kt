package com.applivery.android.sdk.updates

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.applivery.android.sdk.SdkBaseActivity
import com.applivery.android.sdk.R
import com.applivery.android.sdk.domain.HostAppPackageInfoProvider
import com.applivery.android.sdk.ui.theme.AppliveryTheme
import org.koin.core.component.inject

internal class ForceUpdateActivity : SdkBaseActivity() {

    private val hostAppPackageInfoProvider: HostAppPackageInfoProvider by inject()

    private val progressObserver: UpdateInstallProgressObserver by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val installStep by progressObserver.step.collectAsState(UpdateInstallStep.Idle)
            ForceUpdateScreen(
                hostAppName = hostAppPackageInfoProvider.packageInfo.appName,
                installStep = installStep,
                onUpdateClick = ::startDownloadService
            )
        }
    }

    private fun startDownloadService() = DownloadBuildService.start(this)

    @Suppress("OVERRIDE_DEPRECATION")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() = Unit

    companion object {

        fun getIntent(context: Context): Intent {
            return Intent(context, ForceUpdateActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
    }
}

private fun UpdateInstallStep.isIdle(): Boolean {
    return this in listOf(UpdateInstallStep.Idle, UpdateInstallStep.Done)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ForceUpdateScreen(
    hostAppName: String,
    installStep: UpdateInstallStep,
    onUpdateClick: () -> Unit
) {
    AppliveryTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text(hostAppName) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = contentColorFor(MaterialTheme.colorScheme.primary)
                    )
                )
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(all = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center

                ) {
                    Text(
                        text = stringResource(id = R.string.appliveryMustUpdateAppLocked)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    AnimatedVisibility(visible = !installStep.isIdle()) {
                        CircularProgressIndicator(modifier = Modifier.padding(all = 24.dp))
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onUpdateClick,
                        enabled = installStep.isIdle()
                    ) {
                        Text(text = stringResource(id = R.string.appliveryUpdate))
                    }
                }
            }
        )
    }
}

@Preview
@Composable
private fun ForceUpdateScreenPreview() {
    ForceUpdateScreen(
        hostAppName = "Applivery host app",
        installStep = UpdateInstallStep.Idle,
        onUpdateClick = {}
    )
}
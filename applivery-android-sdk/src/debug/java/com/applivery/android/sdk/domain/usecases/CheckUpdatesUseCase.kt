package com.applivery.android.sdk.domain.usecases

import com.applivery.android.sdk.HostActivityProvider
import com.applivery.android.sdk.domain.HostAppPackageInfoProvider
import com.applivery.android.sdk.domain.model.AppConfig
import com.applivery.android.sdk.domain.model.UpdateType
import com.applivery.android.sdk.domain.repository.AppliveryRepository
import com.applivery.android.sdk.updates.ForceUpdateActivity
import com.applivery.android.sdk.updates.SuggestedUpdateActivity

internal interface CheckUpdatesUseCase {

    suspend operator fun invoke()
}

internal class CheckUpdates(
    private val repository: AppliveryRepository,
    private val hostActivityProvider: HostActivityProvider,
    hostAppPackageInfoProvider: HostAppPackageInfoProvider
) : CheckUpdatesUseCase {

    private val hostInfo = hostAppPackageInfoProvider.packageInfo

    override suspend fun invoke() {
        val config = repository.getConfig().getOrNull() ?: return
        when (config.toUpdateType()) {
            UpdateType.ForceUpdate -> {
                val activity = hostActivityProvider.activity ?: return
                activity.startActivity(ForceUpdateActivity.getIntent(activity))
            }

            UpdateType.SuggestedUpdate -> {
                val activity = hostActivityProvider.activity ?: return
                activity.startActivity(SuggestedUpdateActivity.getIntent(activity))
            }

            UpdateType.None -> Unit
        }
    }

    private fun AppConfig.toUpdateType(): UpdateType {
        return if (forceUpdate && minVersion > hostInfo.versionCode) {
            UpdateType.ForceUpdate
        } else if (ota && lastBuildVersion > hostInfo.versionCode) {
            UpdateType.SuggestedUpdate
        } else {
            UpdateType.None
        }
    }
}
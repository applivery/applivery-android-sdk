package com.applivery.android.sdk.domain.usecases

import com.applivery.android.sdk.HostActivityProvider
import com.applivery.android.sdk.domain.DomainLogger
import com.applivery.android.sdk.domain.HostAppPackageInfoProvider
import com.applivery.android.sdk.domain.PostponedUpdateLogic
import com.applivery.android.sdk.domain.model.AppConfig
import com.applivery.android.sdk.domain.model.UpdateType
import com.applivery.android.sdk.domain.repository.AppliveryRepository
import com.applivery.android.sdk.updates.ForceUpdateActivity
import com.applivery.android.sdk.updates.SuggestedUpdateActivity

internal interface CheckUpdatesUseCase {

    suspend operator fun invoke(forceUpdate: Boolean)
}

internal class CheckUpdates(
    private val repository: AppliveryRepository,
    private val hostActivityProvider: HostActivityProvider,
    private val logger: DomainLogger,
    private val postponedUpdateLogic: PostponedUpdateLogic,
    hostAppPackageInfoProvider: HostAppPackageInfoProvider
) : CheckUpdatesUseCase {

    private val hostInfo = hostAppPackageInfoProvider.packageInfo

    override suspend fun invoke(forceUpdate: Boolean) {
        val config = repository.getConfig().getOrNull() ?: return
        when (config.toUpdateType().also(logger::updateType)) {
            UpdateType.ForceUpdate -> {
                val activity = hostActivityProvider.activity ?: return
                activity.startActivity(ForceUpdateActivity.getIntent(activity))
            }

            UpdateType.SuggestedUpdate -> {
                val activity = hostActivityProvider.activity ?: return
                if (forceUpdate || !postponedUpdateLogic.isUpdatePostponed()) {
                    activity.startActivity(SuggestedUpdateActivity.getIntent(activity))
                } else {
                    logger.updatePostponed()
                }
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
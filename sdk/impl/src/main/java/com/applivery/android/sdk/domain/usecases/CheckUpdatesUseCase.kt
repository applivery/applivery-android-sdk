package com.applivery.android.sdk.domain.usecases

import com.applivery.android.sdk.domain.DomainLogger
import com.applivery.android.sdk.domain.HostAppPackageInfoProvider
import com.applivery.android.sdk.domain.PostponedUpdateLogic
import com.applivery.android.sdk.domain.ScreenRouter
import com.applivery.android.sdk.domain.model.AppConfig
import com.applivery.android.sdk.domain.model.UpdateType
import com.applivery.android.sdk.domain.repository.AppliveryRepository

internal interface CheckUpdatesUseCase {

    suspend operator fun invoke(forceUpdate: Boolean)
}

internal class CheckUpdates(
    private val repository: AppliveryRepository,
    private val screenRouter: ScreenRouter,
    private val logger: DomainLogger,
    private val postponedUpdateLogic: PostponedUpdateLogic,
    private val hostAppPackageInfoProvider: HostAppPackageInfoProvider
) : CheckUpdatesUseCase {

    private val hostInfo get() = hostAppPackageInfoProvider.packageInfo

    override suspend fun invoke(forceUpdate: Boolean) {
        val config = repository.getConfig().getOrNull() ?: return
        when (config.toUpdateType().also(logger::updateType)) {
            UpdateType.ForceUpdate -> screenRouter.navigateToForceUpdateScreen()

            UpdateType.SuggestedUpdate -> {
                if (forceUpdate || !postponedUpdateLogic.isUpdatePostponed()) {
                    screenRouter.navigateToSuggestedUpdateScreen()
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
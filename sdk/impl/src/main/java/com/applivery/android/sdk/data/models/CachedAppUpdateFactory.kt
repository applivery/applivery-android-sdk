package com.applivery.android.sdk.data.models

import com.applivery.android.sdk.domain.model.AppUpdate
import com.applivery.android.sdk.domain.model.BuildMetadata
import com.applivery.android.sdk.domain.model.CachedAppUpdate

internal object CachedAppUpdateFactory {
    fun from(
        build: BuildMetadata,
        installAction: () -> Unit
    ): CachedAppUpdate {
        return object : CachedAppUpdate {
            override val appUpdate: AppUpdate = AppUpdate(
                buildId = build.id,
                buildVersion = build.version
            )

            override fun install() = installAction()
        }
    }
}

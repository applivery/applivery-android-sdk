package com.applivery.android.sdk.updates

import android.content.Context
import arrow.core.Either
import com.applivery.android.sdk.data.models.CachedAppUpdateFactory
import com.applivery.android.sdk.domain.model.BuildMetadata
import com.applivery.android.sdk.domain.model.CachedAppUpdate
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.model.NoVersionToUpdate
import com.applivery.android.sdk.domain.model.NoVersionToUpdateError

internal fun Either<DomainError, BuildMetadata>.asCachedAppUpdateResult(context: Context): Result<CachedAppUpdate> {
    return fold(
        ifLeft = { error ->
            val error = when (error) {
                is NoVersionToUpdateError -> NoVersionToUpdate()
                else -> Throwable(error.message)
            }
            Result.failure(error)
        },
        ifRight = { build ->
            val cachedAppUpdate = CachedAppUpdateFactory.from(build) {
                val params = BuildDownloadParams(build.id, build.version)
                DownloadBuildService.start(context = context, params = params)
            }
            Result.success(cachedAppUpdate)
        }
    )
}
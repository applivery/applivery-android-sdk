package com.applivery.android.sdk.domain.usecases

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.applivery.android.sdk.domain.HostAppPackageInfoProvider
import com.applivery.android.sdk.domain.model.BuildMetadata
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.model.NoVersionToUpdateError
import com.applivery.android.sdk.domain.repository.DownloadsRepository

internal interface DownloadBuildUseCase {

    suspend operator fun invoke(
        buildId: String,
        buildVersion: Int
    ): Either<DomainError, BuildMetadata>
}

internal class DownloadBuild(
    private val downloadsRepository: DownloadsRepository,
    private val hostAppPackageInfoProvider: HostAppPackageInfoProvider,
) : DownloadBuildUseCase {

    override suspend fun invoke(
        buildId: String,
        buildVersion: Int
    ): Either<DomainError, BuildMetadata> {
        return either {
            val hostInfo = hostAppPackageInfoProvider.packageInfo
            ensure(buildVersion > hostInfo.versionCode) { NoVersionToUpdateError() }
            val file = downloadsRepository.downloadBuild(buildId, buildVersion).bind()
            BuildMetadata(id = buildId, version = buildVersion, filePath = file.path)
        }
    }
}


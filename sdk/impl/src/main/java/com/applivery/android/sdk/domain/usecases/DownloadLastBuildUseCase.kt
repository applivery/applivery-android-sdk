package com.applivery.android.sdk.domain.usecases

import arrow.core.Either
import arrow.core.raise.either
import com.applivery.android.sdk.domain.model.BuildMetadata
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.repository.AppliveryRepository

internal interface DownloadLastBuildUseCase {

    suspend operator fun invoke(): Either<DomainError, BuildMetadata>
}

internal class DownloadLastBuild(
    private val appliveryRepository: AppliveryRepository,
    private val downloadBuildUseCase: DownloadBuildUseCase
) : DownloadLastBuildUseCase {

    override suspend fun invoke(): Either<DomainError, BuildMetadata> = either {
        val config = appliveryRepository.getConfig().bind()
        downloadBuildUseCase(config.lastBuildId, config.lastBuildVersion).bind()
    }
}


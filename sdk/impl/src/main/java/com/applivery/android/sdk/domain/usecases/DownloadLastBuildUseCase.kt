package com.applivery.android.sdk.domain.usecases

import arrow.core.Either
import arrow.core.raise.either
import com.applivery.android.sdk.domain.model.BuildMetadata
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.repository.AppliveryRepository
import com.applivery.android.sdk.domain.repository.DownloadsRepository
import java.io.File

internal interface DownloadLastBuildUseCase {

    suspend operator fun invoke(): Either<DomainError, File>
}

internal class DownloadLastBuild(
    private val appliveryRepository: AppliveryRepository,
    private val downloadsRepository: DownloadsRepository
) : DownloadLastBuildUseCase {

    override suspend fun invoke(): Either<DomainError, File> = either {
        val config = appliveryRepository.getConfig().bind()
        downloadsRepository.downloadBuild(config.lastBuildId, config.lastBuildVersion).bind()
    }
}


package com.applivery.android.sdk.domain.usecases

import arrow.core.Either
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.repository.DownloadsRepository

internal interface PurgeDownloadsUseCase {

    suspend operator fun invoke(): Either<DomainError, Unit>
}

internal class PurgeDownloads(
    private val downloadsRepository: DownloadsRepository
) : PurgeDownloadsUseCase {

    override suspend fun invoke(): Either<DomainError, Unit> {
        return downloadsRepository.purgeDownloads()
    }
}

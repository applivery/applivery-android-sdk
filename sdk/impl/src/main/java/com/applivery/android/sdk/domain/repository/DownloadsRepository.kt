package com.applivery.android.sdk.domain.repository

import arrow.core.Either
import com.applivery.android.sdk.domain.model.DomainError
import java.io.File

internal interface DownloadsRepository {

    suspend fun downloadBuild(buildId: String): Either<DomainError, File>
}
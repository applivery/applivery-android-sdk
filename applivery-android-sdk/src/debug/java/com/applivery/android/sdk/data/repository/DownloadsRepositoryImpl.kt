package com.applivery.android.sdk.data.repository

import arrow.core.Either
import com.applivery.android.sdk.data.api.ApiDataSource
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.repository.DownloadsRepository
import java.io.File

internal class DownloadsRepositoryImpl(
    private val apiDataSource: ApiDataSource
) : DownloadsRepository {

    override suspend fun downloadBuild(buildId: String): Either<DomainError, File> {
        return apiDataSource.downloadBuild(buildId)
    }
}
package com.applivery.android.sdk.data.repository

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.raise.catch
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.recover
import arrow.core.toOption
import com.applivery.android.sdk.data.api.ApiDataSource
import com.applivery.android.sdk.data.persistence.BuildMetadataDatastore
import com.applivery.android.sdk.data.persistence.BuildMetadataDs
import com.applivery.android.sdk.data.persistence.toDs
import com.applivery.android.sdk.domain.model.BuildMetadata
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.repository.DownloadsRepository
import kotlinx.coroutines.flow.firstOrNull
import java.io.File

internal class DownloadsRepositoryImpl(
    private val apiDataSource: ApiDataSource,
    private val dataStore: BuildMetadataDatastore
) : DownloadsRepository {

    override suspend fun downloadBuild(
        buildId: String,
        buildVersion: Int
    ): Either<DomainError, File> {
        return getFromCache(buildId).recover {
            downloadFromApi(buildId)
                .onRight { it.saveInCache(buildId, buildVersion) }
                .bind()
        }
    }

    private suspend fun getFromCache(buildId: String): Either<DomainError, File> {
        return dataStore.get(buildId).firstOrNull()?.getOrNull().toOption()
            .toEither { BuildNotFoundInCache(buildId) }
            .flatMap { it.asFile() }
    }

    private suspend fun downloadFromApi(buildId: String): Either<DomainError, File> {
        return apiDataSource.downloadBuild(buildId)
    }

    private fun BuildMetadataDs.asFile(): Either<DomainError, File> = either {
        val file = catch(
            block = { File(filePath) },
            catch = { raise(BuildFileCorruptedError(id)) }
        )
        ensure(file.exists()) { BuildFileCorruptedError(id) }
        ensure(file.isFile) { BuildFileCorruptedError(id) }
        ensure(file.canRead()) { BuildFileCorruptedError(id) }
        ensure(file.length() > 0) { BuildFileCorruptedError(id) }
        file
    }

    private suspend fun File.saveInCache(id: String, version: Int) {
        val buildMetadata = BuildMetadata(id = id, version = version, filePath = canonicalPath)
        dataStore.set(buildMetadata.id, buildMetadata.toDs())
    }
}

internal class BuildNotFoundInCache(buildId: String) : DomainError(
    "Build with ID $buildId not found in cache"
)

internal class BuildFileCorruptedError(buildId: String) : DomainError(
    "Build file for ID $buildId is corrupted"
)
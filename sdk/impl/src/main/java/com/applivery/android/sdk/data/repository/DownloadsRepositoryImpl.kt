package com.applivery.android.sdk.data.repository

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.raise.catch
import arrow.core.raise.either
import arrow.core.recover
import arrow.core.toOption
import com.applivery.android.sdk.data.api.ApiDataSource
import com.applivery.android.sdk.data.persistence.BuildMetadataDatastore
import com.applivery.android.sdk.data.persistence.BuildMetadataDs
import com.applivery.android.sdk.data.persistence.toDs
import com.applivery.android.sdk.domain.HostAppPackageInfoProvider
import com.applivery.android.sdk.domain.ensure
import com.applivery.android.sdk.domain.ensureNotNull
import com.applivery.android.sdk.domain.model.BuildMetadata
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.repository.DownloadsRepository
import kotlinx.coroutines.flow.firstOrNull
import java.io.File

internal class DownloadsRepositoryImpl(
    private val apiDataSource: ApiDataSource,
    private val dataStore: BuildMetadataDatastore,
    private val packageInfoProvider: HostAppPackageInfoProvider
) : DownloadsRepository {

    override suspend fun downloadBuild(
        buildId: String,
        buildVersion: Int
    ): Either<DomainError, File> {
        return getDownloadFromCache(buildId).recover {
            getDownloadFromApi(buildId)
                .onRight { it.saveInCache(buildId, buildVersion) }
                .bind()
        }
    }

    override suspend fun purgeDownloads(): Either<DomainError, Unit> = either {
        val currentBuildVersion = packageInfoProvider.packageInfo.versionCode
        ensureNotNull(dataStore.getAll().firstOrNull())
            .filter { it.version <= currentBuildVersion }
            .forEach { metadata ->
                metadata.asFile().onRight {
                    runCatching { it.delete() }.onSuccess { dataStore.delete(metadata.id) }
                }
            }
    }

    private suspend fun getDownloadFromCache(buildId: String): Either<DomainError, File> {
        return dataStore.get(buildId).firstOrNull()?.getOrNull().toOption()
            .toEither { BuildNotFoundInCache(buildId) }
            .flatMap { it.asFile() }
    }

    private suspend fun getDownloadFromApi(buildId: String): Either<DomainError, File> {
        return apiDataSource.downloadBuild(buildId)
    }

    private fun BuildMetadataDs.asFile(): Either<DomainError, File> = either {
        val file = catch(
            block = { File(filePath) },
            catch = { raise(BuildFileCorruptedError(id)) }
        )
        ensure(file.exists())
        ensure(file.isFile)
        ensure(file.canRead())
        ensure(file.length() > 0)
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
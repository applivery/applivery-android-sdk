package com.applivery.android.sdk.data.api

import android.content.Context
import arrow.core.Either
import arrow.core.raise.catch
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import com.applivery.android.sdk.data.api.service.AppliveryApiService
import com.applivery.android.sdk.data.api.service.AppliveryDownloadService
import com.applivery.android.sdk.data.models.AppConfigApi
import com.applivery.android.sdk.data.models.AuthenticationUriApi
import com.applivery.android.sdk.data.models.toDomain
import com.applivery.android.sdk.domain.UnifiedErrorHandler
import com.applivery.android.sdk.domain.model.AppConfig
import com.applivery.android.sdk.domain.model.AuthenticationUri
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.model.InternalError
import com.applivery.android.sdk.domain.model.mapNotNull
import com.applivery.android.sdk.updates.createContentFile
import com.applivery.android.sdk.updates.write
import java.io.File

internal class ApiDataSource(
    private val context: Context,
    private val apiService: AppliveryApiService,
    private val downloadService: AppliveryDownloadService,
    private val unifiedErrorHandler: UnifiedErrorHandler
) {

    suspend fun getAppConfig(): Either<DomainError, AppConfig> {
        return apiService.getConfig()
            .toDomain()
            .map(AppConfigApi::toDomain)
            .onLeft(unifiedErrorHandler::handle)
    }

    suspend fun getAuthenticationUri(): Either<DomainError, AuthenticationUri> {
        return apiService.getAuthenticationUri()
            .toDomain()
            .mapNotNull(AuthenticationUriApi::toDomain)
            .onLeft(unifiedErrorHandler::handle)
    }

    suspend fun downloadBuild(buildId: String): Either<DomainError, File> {
        return either {
            val token = apiService.getBuildToken(buildId)
                .toDomain()
                .mapNotNull { it.token }
                .onLeft(unifiedErrorHandler::handle)
                .bind()
            val body = catch(
                block = { ensureNotNull(downloadService.downloadBuild(token)) { InternalError() } },
                catch = { raise(InternalError()) }
            )
            val file = ensureNotNull(context.createContentFile(buildId)) { InternalError() }
            file.write(body.byteStream()).bind()
        }
    }
}
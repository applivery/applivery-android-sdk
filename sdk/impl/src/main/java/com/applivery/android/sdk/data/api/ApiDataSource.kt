package com.applivery.android.sdk.data.api

import android.content.Context
import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.raise.catch
import arrow.core.raise.either
import arrow.core.right
import com.applivery.android.sdk.data.api.service.AppliveryApiService
import com.applivery.android.sdk.data.api.service.AppliveryDownloadService
import com.applivery.android.sdk.data.auth.SessionManager
import com.applivery.android.sdk.data.models.AppConfigApi
import com.applivery.android.sdk.data.models.AuthenticationUriApi
import com.applivery.android.sdk.data.models.SendFeedbackResponseApi
import com.applivery.android.sdk.data.models.UserApi
import com.applivery.android.sdk.data.models.toApi
import com.applivery.android.sdk.data.models.toDomain
import com.applivery.android.sdk.domain.AppPreferences
import com.applivery.android.sdk.domain.UnifiedErrorHandler
import com.applivery.android.sdk.domain.ensureNotNull
import com.applivery.android.sdk.domain.model.AppConfig
import com.applivery.android.sdk.domain.model.AppUpdateError
import com.applivery.android.sdk.domain.model.AppUpdateError.CreateBuildFile
import com.applivery.android.sdk.domain.model.AuthenticationUri
import com.applivery.android.sdk.domain.model.BindUser
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.model.Feedback
import com.applivery.android.sdk.domain.model.InternalError
import com.applivery.android.sdk.domain.model.UnauthorizedError
import com.applivery.android.sdk.domain.model.User
import com.applivery.android.sdk.domain.model.ignore
import com.applivery.android.sdk.domain.model.mapNotNull
import com.applivery.android.sdk.updates.createContentFile
import com.applivery.android.sdk.updates.write
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

internal class ApiDataSource(
    private val context: Context,
    private val apiService: AppliveryApiService,
    private val downloadService: AppliveryDownloadService,
    private val unifiedErrorHandler: UnifiedErrorHandler,
    private val sessionManager: SessionManager,
    private val appPreferences: AppPreferences
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
            val buildStream = catch(
                block = { downloadService.downloadBuild(token).byteStream() },
                catch = { raise(AppUpdateError.DownloadBuild(it.stackTraceToString())) }
            )
            val file = catch(
                block = { context.createContentFile(buildId) },
                catch = { raise(CreateBuildFile(it.stackTraceToString())) }
            )
            file.write(buildStream).bind()
        }
    }

    suspend fun bindUser(bindUser: BindUser): Either<DomainError, Unit> {
        return apiService.bindUser(bindUser.toApi())
            .toDomain()
            .mapNotNull { it.bearer }
            .onRight(::onSaveToken)
            .ignore()
    }

    fun unbindUser(): Either<DomainError, Unit> {
        sessionManager.logOut()
        appPreferences.anonymousEmail = null
        return Unit.right()
    }

    suspend fun getUser(): Either<DomainError, User> {
        if (!sessionManager.isLoggedIn) return UnauthorizedError().left()
        return apiService.getUser().toDomain().map(UserApi::toDomain)
    }

    suspend fun sendFeedback(feedback: Feedback): Either<DomainError, Unit> {
        return apiService
            .sendFeedback(feedback.toApi()).toDomain()
            .flatMap { feedback.processResponse(it) }
    }

    private suspend fun Feedback.processResponse(
        responseApi: SendFeedbackResponseApi
    ): Either<DomainError, Unit> = when (this) {
        is Feedback.Video -> processResponse(responseApi)
        is Feedback.Screenshot,
        is Feedback.Simple -> Unit.right()
    }

    private suspend fun Feedback.Video.processResponse(
        response: SendFeedbackResponseApi
    ): Either<DomainError, Unit> = either {
        val videoLocationUrl = ensureNotNull(response.videoFile?.locationUrl)
        val videoFile = catch(
            block = { File(ensureNotNull(uri.path)) },
            catch = { raise(InternalError()) }
        )
        val requestBody = videoFile.asRequestBody("video/mp4".toMediaTypeOrNull())
        catch(
            block = { apiService.uploadVideo(videoLocationUrl, requestBody) },
            catch = { raise(InternalError()) }
        )
    }

    private fun onSaveToken(token: String) {
        sessionManager.saveToken(token)
        appPreferences.anonymousEmail = null
    }
}
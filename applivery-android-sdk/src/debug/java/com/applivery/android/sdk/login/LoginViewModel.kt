package com.applivery.android.sdk.login

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import arrow.core.Either
import com.applivery.android.sdk.BuildConfig
import com.applivery.android.sdk.data.auth.SessionManager
import com.applivery.android.sdk.domain.AppPreferences
import com.applivery.android.sdk.domain.HostAppPackageInfoProvider
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.usecases.GetAuthenticationUriUseCase

// TODO: migrate to BaseViewModel
internal class LoginViewModel(
    private val getAuthenticationUri: GetAuthenticationUriUseCase,
    private val packageInfoProvider: HostAppPackageInfoProvider,
    private val sessionManager: SessionManager,
    private val appPreferences: AppPreferences
) : ViewModel() {

    suspend fun getAuthenticationUri(): Either<DomainError, Uri> {
        val applicationId = packageInfoProvider.packageInfo.packageName
        val redirectScheme = "${applicationId}.${BuildConfig.AuthSchemeSuffix}"
        return getAuthenticationUri.invoke().map {
            it.uri.toUri()
                .buildUpon()
                .appendQueryParameter("scheme", redirectScheme)
                .build()
        }
    }

    fun onAuthenticated(bearer: String) {
        sessionManager.saveToken(bearer)
        appPreferences.anonymousEmail = null
    }
}
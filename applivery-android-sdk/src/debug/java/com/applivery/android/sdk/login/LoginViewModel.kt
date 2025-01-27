package com.applivery.android.sdk.login

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.applivery.android.sdk.BuildConfig
import com.applivery.android.sdk.data.auth.SessionManager
import com.applivery.android.sdk.domain.AppPreferences
import com.applivery.android.sdk.domain.HostAppPackageInfoProvider
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.usecases.GetAuthenticationUriUseCase
import com.applivery.android.sdk.presentation.BaseViewModel
import com.applivery.android.sdk.presentation.ViewAction
import com.applivery.android.sdk.presentation.ViewIntent
import com.applivery.android.sdk.presentation.ViewState
import kotlinx.coroutines.launch

internal sealed interface LoginAction : ViewAction {
    class OpenAuthorizationUri(val uri: Uri) : LoginAction
    data object Finish : LoginAction
}

internal sealed interface LoginIntent : ViewIntent {
    data object AuthorizationStarted : LoginIntent
    class AuthenticationFinished(val bearer: String?) : LoginIntent
}

internal data object LoginState : ViewState

internal class LoginViewModel(
    private val getAuthenticationUri: GetAuthenticationUriUseCase,
    private val packageInfoProvider: HostAppPackageInfoProvider,
    private val sessionManager: SessionManager,
    private val appPreferences: AppPreferences
) : BaseViewModel<LoginState, LoginIntent, LoginAction>() {

    override val initialViewState = LoginState

    override fun load() = Unit

    override fun sendIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.AuthorizationStarted -> onAuthorizationStarted()
            is LoginIntent.AuthenticationFinished -> onAuthenticationFinished(intent.bearer)
        }
    }

    private fun onAuthorizationStarted() {
        viewModelScope.launch {
            getAuthenticationUri().onRight {
                dispatchAction(LoginAction.OpenAuthorizationUri(it))
            }
        }
    }

    private fun onAuthenticationFinished(bearer: String?) {
        if (bearer != null) {
            sessionManager.saveToken(bearer)
            appPreferences.anonymousEmail = null
            LoginCallbacks.onLogin()
        } else {
            LoginCallbacks.onCanceled()
        }
        dispatchAction(LoginAction.Finish)
    }

    private suspend fun getAuthenticationUri(): Either<DomainError, Uri> {
        val applicationId = packageInfoProvider.packageInfo.packageName
        val redirectScheme = "${applicationId}.${BuildConfig.AuthSchemeSuffix}"
        return getAuthenticationUri.invoke().map {
            it.uri.toUri()
                .buildUpon()
                .appendQueryParameter("scheme", redirectScheme)
                .build()
        }
    }
}
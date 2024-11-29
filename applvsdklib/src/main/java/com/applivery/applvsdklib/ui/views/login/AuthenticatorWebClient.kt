package com.applivery.applvsdklib.ui.views.login

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.net.toUri
import com.applivery.base.util.AppliveryLog

class AuthenticatorWebClient(
    private val onLoadFinished: () -> Unit,
    private val onAuthenticationSucceeded: (String) -> Unit
) : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        AppliveryLog.debug(TAG, "shouldOverrideUrlLoading: $url")
        val uri = url.toUri()
        if (uri.scheme != ReplyScheme) return false
        if (uri.host != ReplyHost) return false
        val bearer = uri.getQueryParameter(ReplyQueryBearerKey) ?: return false
        onAuthenticationSucceeded(bearer)
        return true
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        onLoadFinished()
    }

    companion object {
        private const val ReplyQueryBearerKey = "bearer"
        private const val ReplyScheme = "applivery"
        private const val ReplyHost = "login-sdk-success"
        private const val TAG = "AppliveryWebClient"
    }
}
/*
 * Copyright (c) 2019 Applivery
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.applivery.applvsdklib.ui.views.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.applivery.applvsdklib.R
import com.applivery.applvsdklib.tools.injection.Injection
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {

    private val presenter by lazy {
        Injection.provideLoginPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.applivery_activity_login)
        val webView = findViewById<WebView>(R.id.webView)
        val loadingView = findViewById<View>(R.id.loadingView)
        with(webView) {
            with(settings) {
                javaScriptEnabled = true
                allowFileAccess = false
                allowContentAccess = false
                cacheMode = android.webkit.WebSettings.LOAD_NO_CACHE
                databaseEnabled = true
                domStorageEnabled = true
                userAgentString = "applivery-sdk-android"
            }
            scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY
            isScrollbarFadingEnabled = false
            webChromeClient = WebChromeClient()
            webViewClient = AuthenticatorWebClient(
                onLoadFinished = { loadingView.visibility = View.GONE },
                onAuthenticationSucceeded = {
                    presenter.onAuthenticated(it)
                    finish()
                    LoginCallbacks.onLogin()
                }
            )
        }

        lifecycleScope.launch {
            val uri = presenter.getAuthenticationUri().getOrNull()?.uri ?: return@launch
            webView.loadUrl(uri)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        LoginCallbacks.onCanceled()
    }

    companion object {

        fun getIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
    }
}

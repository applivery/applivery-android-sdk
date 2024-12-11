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
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.applivery.applvsdklib.tools.injection.Injection
import com.applivery.applvsdklib.ui.views.login.customtabs.CustomTabsManager
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {

    private val presenter by lazy {
        Injection.provideLoginPresenter()
    }

    private var authorizationStarted = false

    private val customTabsManager by lazy { CustomTabsManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycle.addObserver(customTabsManager)
    }

    override fun onResume() {
        super.onResume()

        /*
         * If this is the first run of the activity, start the authorization intent.
         * Note that we do not finish the activity at this point, in order to remain on the back
         * stack underneath the authorization activity.
         */
        if (!authorizationStarted) {
            lifecycleScope.launch {
                val uri = presenter.getAuthenticationUri().getOrNull() ?: return@launch
                customTabsManager.launch(uri)
                authorizationStarted = true
            }
            return
        }

        /*
         * On a subsequent run, it must be determined whether we have returned to this activity
         * due to an OAuth2 redirect, or the user canceling the authorization flow. This can
         * be done by checking whether a response URI is available, which would be provided by
         * RedirectUriReceiverActivity. If it is not, we have returned here due to the user
         * pressing the back button, or the authorization activity finishing without
         * RedirectUriReceiverActivity having been invoked - this can occur when the user presses
         * the back button, or closes the browser tab.
         */
        val responseUri = intent.data
        if (responseUri != null) {
            val bearer = responseUri.getQueryParameter(ReplyQueryBearerKey) ?: return
            presenter.onAuthenticated(bearer)
            LoginCallbacks.onLogin()
        } else {
            LoginCallbacks.onCanceled()
        }
        finish()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    companion object {

        private const val ReplyQueryBearerKey = "bearer"

        fun getIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }

        fun createResponseHandlingIntent(context: Context, responseUri: Uri?): Intent {
            return getIntent(context)
                .setData(responseUri)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
    }
}

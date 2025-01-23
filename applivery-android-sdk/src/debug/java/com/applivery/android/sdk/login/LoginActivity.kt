package com.applivery.android.sdk.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.addCallback
import androidx.lifecycle.lifecycleScope
import com.applivery.android.sdk.SdkBaseActivity
import com.applivery.android.sdk.login.customtabs.CustomTabsManager
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

internal class LoginActivity : SdkBaseActivity() {

    private val viewModel: LoginViewModel by viewModel()
    private var authorizationStarted = false

    private val customTabsManager by lazy { CustomTabsManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycle.addObserver(customTabsManager)

        onBackPressedDispatcher.addCallback { LoginCallbacks.onCanceled() }
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
                val uri = viewModel.getAuthenticationUri().getOrNull() ?: return@launch
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
        val bearer = intent.data?.getQueryParameter(ReplyQueryBearerKey)
        if (bearer != null) {
            viewModel.onAuthenticated(bearer)
            LoginCallbacks.onLogin()
        } else {
            LoginCallbacks.onCanceled()
        }
        finish()
    }

    override fun onNewIntent(intent: Intent) {
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

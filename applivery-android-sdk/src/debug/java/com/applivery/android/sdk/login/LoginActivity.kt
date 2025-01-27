package com.applivery.android.sdk.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.addCallback
import com.applivery.android.sdk.SdkBaseActivity
import com.applivery.android.sdk.login.customtabs.CustomTabsManager
import com.applivery.android.sdk.presentation.launchAndCollectIn
import org.koin.androidx.viewmodel.ext.android.viewModel

internal class LoginActivity : SdkBaseActivity() {

    private val viewModel: LoginViewModel by viewModel()
    private var authorizationStarted = false

    private val customTabsManager by lazy { CustomTabsManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycle.addObserver(customTabsManager)

        onBackPressedDispatcher.addCallback { LoginCallbacks.onCanceled() }

        viewModel.viewActions.launchAndCollectIn(this) { action ->
            when (action) {
                is LoginAction.OpenAuthorizationUri -> {
                    customTabsManager.launch(action.uri)
                    authorizationStarted = true
                }

                is LoginAction.Finish -> finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        /*
         * If this is the first run of the activity, start the authorization intent.
         * Note that we do not finish the activity at this point, in order to remain on the back
         * stack underneath the authorization activity.
         */
        if (!authorizationStarted) {
            viewModel.sendIntent(LoginIntent.AuthorizationStarted)
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
        viewModel.sendIntent(LoginIntent.AuthenticationFinished(bearer))
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

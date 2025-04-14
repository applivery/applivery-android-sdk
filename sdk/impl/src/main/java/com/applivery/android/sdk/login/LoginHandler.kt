package com.applivery.android.sdk.login

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.applivery.android.sdk.HostActivityProvider
import com.applivery.android.sdk.R
import com.applivery.android.sdk.configuration.Configuration
import com.applivery.android.sdk.domain.HostAppPackageInfoProvider
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.ui.configureIf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume


internal class LoginHandler(
    private val activityProvider: HostActivityProvider,
    private val hostAppPackageInfoProvider: HostAppPackageInfoProvider,
    private val configuration: Configuration
) {

    private var currentDialog: AlertDialog? = null

    suspend operator fun invoke(): Either<DomainError, Unit> {
        val activity = activityProvider.activity ?: return LoginCanceled().left()
        if (currentDialog?.isShowing == true) return LoginCanceled().left()
        return withContext(Dispatchers.Main.immediate) {
            suspendCancellableCoroutine { cont ->
                currentDialog = AlertDialog.Builder(activity, R.style.Theme_Applivery_Dialog)
                    .setTitle(hostAppPackageInfoProvider.packageInfo.appName)
                    .setMessage(R.string.appliveryLoginRequiredText)
                    .setCancelable(false)
                    .setOnDismissListener { currentDialog = null }
                    .setPositiveButton(
                        R.string.appliveryLogin,
                        onPositiveButtonClick(activity, cont)
                    )
                    .configureIf(!configuration.enforceAuthentication) {
                        setNegativeButton(
                            R.string.appliveryLoginCancel,
                            onNegativeButtonClick(cont)
                        )
                    }.show()
            }
        }
    }


    private fun onPositiveButtonClick(
        activity: Activity,
        cont: Continuation<Either<DomainError, Unit>>
    ): DialogInterface.OnClickListener {
        return DialogInterface.OnClickListener { _, _ ->
            LoginCallbacks.add(cont.toLoginCallback())
            activity.startActivity(LoginActivity.getIntent(activity))
        }
    }

    private fun onNegativeButtonClick(
        cont: Continuation<Either<DomainError, Unit>>
    ): DialogInterface.OnClickListener {
        return DialogInterface.OnClickListener { _, _ -> cont.resume(LoginCanceled().left()) }
    }

    private fun Continuation<Either<DomainError, Unit>>.toLoginCallback(): LoginCallback {
        return LoginCallback(
            onLogin = { resume(Unit.right()) },
            onCanceled = { resume(LoginCanceled().left()) }
        )
    }
}

internal class LoginCanceled : DomainError()
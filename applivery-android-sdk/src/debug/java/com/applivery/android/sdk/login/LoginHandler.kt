package com.applivery.android.sdk.login

import androidx.appcompat.app.AlertDialog
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.applivery.android.sdk.CurrentActivityProvider
import com.applivery.android.sdk.R
import com.applivery.android.sdk.domain.model.DomainError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume


internal class LoginHandler(
    private val activityProvider: CurrentActivityProvider
) {

    private var currentDialog: AlertDialog? = null

    suspend operator fun invoke(): Either<DomainError, Unit> {
        val activity = activityProvider.activity ?: return LoginCanceled().left()
        if (currentDialog?.isShowing == true) return LoginCanceled().left()
        return withContext(Dispatchers.Main.immediate) {
            suspendCancellableCoroutine { cont ->
                currentDialog = AlertDialog.Builder(activity)
                    .setTitle(R.string.appliveryError)
                    .setCancelable(false)
                    .setMessage(R.string.appliveryLoginRequiredText)
                    .setPositiveButton(R.string.appliveryLogin) { _, _ ->
                        currentDialog?.dismiss()
                        val intent = LoginActivity.getIntent(activity)
                        LoginCallbacks.add(
                            LoginCallback(
                                onLogin = { cont.resume(Unit.right()) },
                                onCanceled = { cont.resume(LoginCanceled().left()) }
                            )
                        )
                        activity.startActivity(intent)
                    }
                    .show()
            }
        }
    }
}

class LoginCanceled : DomainError()
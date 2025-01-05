package com.applivery.applvsdklib.presentation

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.applivery.applvsdklib.AppliverySdk
import com.applivery.applvsdklib.R
import com.applivery.applvsdklib.ui.views.login.LoginView
import com.applivery.base.AppliveryLifecycleCallbacks
import com.applivery.base.domain.model.Failure
import com.applivery.base.util.AppliveryLog

class ErrorManager {

    var currentDialog: AlertDialog? = null

    fun showError(failure: Failure) {
        when (failure) {
            is Failure.DevError -> showDevError(failure)
            is Failure.UnauthorizedError -> showUnauthorizedError()
            else -> AppliverySdk.Logger.loge(failure.message)
        }
    }

    private fun showUnauthorizedError() {
        val activity = AppliveryLifecycleCallbacks.activity
        if (activity == null) {
            AppliveryLog.error("Session Error without valid activity")
            return
        }
        if (currentDialog?.isShowing == true) return

        currentDialog = AlertDialog.Builder(activity)
            .setTitle(R.string.appliveryError)
            .setCancelable(false)
            .setMessage(R.string.appliveryLoginRequiredText)
            .setPositiveButton(R.string.appliveryLogin) { _, _ ->
                currentDialog?.dismiss()
                showLoginView(activity)
            }
            .show()
    }

    private fun showLoginView(activity: Activity) {
        val loginView = LoginView(activity) { AppliverySdk.updateAppConfig() }
        loginView.show()
    }

    private fun showDevError(error: Failure.DevError) {
        when (error) {
            is Failure.SubscriptionError -> {
                AppliverySdk.getApplicationContext()
                    ?.getString(R.string.applivery_error_subscription)?.let { text ->
                        AppliverySdk.Logger.loge(text)
                    }
            }

            else -> {
                AppliverySdk.Logger.loge(error.message)
            }
        }
    }
}

package com.applivery.applvsdklib.presentation

import android.app.Activity
import android.app.AlertDialog
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
            is Failure.UnauthorizedError -> showUnauthorizedError(failure)
            else -> AppliverySdk.Logger.loge(failure.message)
        }
    }

    private fun showUnauthorizedError(error: Failure.UnauthorizedError) {
        clearDialog()

        AppliveryLifecycleCallbacks.activity?.let {
            currentDialog = AlertDialog.Builder(it)
                .setTitle(R.string.appliveryError)
                .setCancelable(false)
                .setMessage(error.message)
                .setPositiveButton(R.string.appliveryLogin) { _, _ ->
                    clearDialog()
                    requestLogin(it)
                }
                .show()
        } ?: also {
            AppliveryLog.error("Session Error without valid activity")
        }
    }

    private fun showDevError(error: Failure.DevError) {
        when (error) {
            is Failure.SubscriptionError -> {
                if (AppliverySdk.isContextAvailable()) {
                    AppliverySdk.Logger.loge(
                        AppliverySdk.getApplicationContext()
                            .getString(R.string.applivery_error_subscription)
                    )
                }
            }
            else -> {
                AppliverySdk.Logger.loge(error.message)
            }
        }
    }

    private fun clearDialog() {
        currentDialog?.run { dismiss() }
    }

    private fun requestLogin(activity: Activity) {
        val loginView = LoginView(activity) {
            AppliverySdk.updateAppConfig()
        }
        loginView.presenter.requestLogin()
    }
}

package com.applivery.applvsdklib.domain.login

import android.util.Log
import com.applivery.applvsdklib.domain.model.ErrorObject
import com.applivery.applvsdklib.tools.executor.InteractorExecutor
import com.applivery.applvsdklib.tools.executor.MainThread
import com.applivery.base.domain.SessionManager
import com.applivery.base.domain.model.UserProfile
import com.applivery.data.AppliveryApiService
import com.applivery.data.response.toDomain
import java.io.IOException

class GetProfileInteractor(
    private val interactorExecutor: InteractorExecutor,
    private val mainThread: MainThread,
    private val apiService: AppliveryApiService,
    private val sessionManager: SessionManager
) : Runnable {

    lateinit var onSuccess: (UserProfile) -> Unit
    lateinit var onError: (error: ErrorObject) -> Unit

    fun getProfile(
        onSuccess: (UserProfile) -> Unit = {},
        onError: (error: ErrorObject) -> Unit = {}
    ) {

        this.onSuccess = onSuccess
        this.onError = onError

        interactorExecutor.run(this)
    }

    override fun run() {

        try {
            if (!sessionManager.hasSession()) {
                notifyError(ErrorObject("Unauthorized"))
            }
            val user = apiService.getProfile().execute().body()?.data?.toDomain()
            if (user != null) {
                notifySuccess(user)
            } else {
                Log.e(TAG, "Get profile response error")
                notifyError(ErrorObject())
            }
        } catch (exception: IOException) {
            Log.e(TAG, "getProfile() -> ${exception.message}")
            notifyError(ErrorObject())
        }
    }

    private fun notifySuccess(userProfile: UserProfile) {
        mainThread.execute(Runnable { onSuccess(userProfile) })
    }

    private fun notifyError(error: ErrorObject) {
        mainThread.execute(Runnable { onError(error) })
    }

    companion object {
        private const val TAG = "GetProfileInteractor"
    }
}

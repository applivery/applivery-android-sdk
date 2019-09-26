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
package com.applivery.applvsdklib.domain.login

import android.util.Log
import com.applivery.applvsdklib.domain.model.ErrorObject
import com.applivery.applvsdklib.domain.model.UserData
import com.applivery.applvsdklib.network.api.AppliveryApiService
import com.applivery.applvsdklib.network.api.model.LoginEntity
import com.applivery.applvsdklib.tools.executor.InteractorExecutor
import com.applivery.applvsdklib.tools.executor.MainThread
import com.applivery.base.domain.SessionManager
import java.io.IOException

class LoginInteractor(
    private val interactorExecutor: InteractorExecutor,
    private val mainThread: MainThread,
    private val apiService: AppliveryApiService,
    private val sessionManager: SessionManager
) : Runnable {

    lateinit var userData: UserData
    lateinit var onSuccess: () -> Unit
    lateinit var onError: (error: ErrorObject) -> Unit

    fun makeLogin(
        userData: UserData,
        onSuccess: () -> Unit = {},
        onError: (error: ErrorObject) -> Unit = {}
    ) {

        this.userData = userData
        this.onSuccess = onSuccess
        this.onError = onError

        interactorExecutor.run(this)
    }

    override fun run() {

        try {
            val apiLoginResponse = apiService.makeLogin(
                LoginEntity.fromUserData(userData.username, userData.password)
            ).execute().body()

            if (apiLoginResponse != null && apiLoginResponse.data != null) {
                sessionManager.saveSession(apiLoginResponse.data.bearer)
                notifySuccess()
            } else {
                Log.e(TAG, "Make login response error")
                notifyError(ErrorObject())
            }
        } catch (exception: IOException) {
            Log.e(TAG, "makeLogin() -> ${exception.message}")
            notifyError(ErrorObject())
        }
    }

    private fun notifySuccess() {
        mainThread.execute(Runnable { onSuccess() })
    }

    private fun notifyError(error: ErrorObject) {
        mainThread.execute(Runnable { onError(error) })
    }

    companion object {
        private const val TAG = "LoginInteractor"
    }
}


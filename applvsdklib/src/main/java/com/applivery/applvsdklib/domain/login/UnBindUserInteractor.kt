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
import com.applivery.applvsdklib.tools.executor.InteractorExecutor
import com.applivery.applvsdklib.tools.executor.MainThread
import com.applivery.base.domain.SessionManager

class UnBindUserInteractor(
    private val interactorExecutor: InteractorExecutor,
    private val mainThread: MainThread,
    private val sessionManager: SessionManager
) : Runnable {

    lateinit var onSuccess: () -> Unit
    lateinit var onError: (error: ErrorObject) -> Unit

    fun unBindUser(
        onSuccess: () -> Unit = {},
        onError: (error: ErrorObject) -> Unit = {}
    ) {
        this.onSuccess = onSuccess
        this.onError = onError

        interactorExecutor.run(this)
    }

    override fun run() {

        try {

            sessionManager.clearSession()
            notifySuccess()
        } catch (exception: Exception) {

            Log.e(TAG, "unBindUser() -> ${exception.message}")
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
        private const val TAG = "UnBindUserInteractor"
    }
}


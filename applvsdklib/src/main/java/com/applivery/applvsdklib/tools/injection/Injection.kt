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
package com.applivery.applvsdklib.tools.injection

import com.applivery.applvsdklib.domain.login.BindUserInteractor
import com.applivery.applvsdklib.domain.login.GetProfileInteractor
import com.applivery.applvsdklib.domain.login.LoginInteractor
import com.applivery.applvsdklib.domain.login.UnBindUserInteractor
import com.applivery.applvsdklib.tools.executor.InteractorExecutor
import com.applivery.applvsdklib.tools.executor.MainThread
import com.applivery.applvsdklib.tools.executor.MainThreadImp
import com.applivery.applvsdklib.tools.executor.ThreadExecutor
import com.applivery.applvsdklib.ui.views.feedback.FeedbackView
import com.applivery.applvsdklib.ui.views.feedback.UserFeedbackPresenter
import com.applivery.applvsdklib.ui.views.login.LoginPresenter
import com.applivery.base.domain.SessionManager
import com.applivery.data.AppliveryApiService

internal object Injection {

    fun provideLoginPresenter(): LoginPresenter {
        return LoginPresenter(provideLoginInteractor())
    }

    fun provideFeedbackPresenter(feedbackView: FeedbackView): UserFeedbackPresenter {
        return UserFeedbackPresenter(feedbackView, provideSessionManager())
    }

    private fun provideLoginInteractor(): LoginInteractor {
        val interactorExecutor = provideInteractorExecutor()
        val mainThread = provideMainThread()
        val apiService = AppliveryApiService.getInstance()
        val sessionManager = provideSessionManager()

        return LoginInteractor(interactorExecutor, mainThread, apiService, sessionManager)
    }

    fun provideBindUserInteractor(): BindUserInteractor {
        val interactorExecutor = provideInteractorExecutor()
        val mainThread = provideMainThread()
        val apiService = AppliveryApiService.getInstance()
        val sessionManager = provideSessionManager()

        return BindUserInteractor(interactorExecutor, mainThread, apiService, sessionManager)
    }

    fun provideUnBindUserInteractor(): UnBindUserInteractor {
        val interactorExecutor = provideInteractorExecutor()
        val mainThread = provideMainThread()
        val sessionManager = provideSessionManager()

        return UnBindUserInteractor(interactorExecutor, mainThread, sessionManager)
    }

    fun provideGetProfileInteractor(): GetProfileInteractor {
        val interactorExecutor = provideInteractorExecutor()
        val mainThread = provideMainThread()
        val apiService = AppliveryApiService.getInstance()
        val sessionManager = provideSessionManager()

        return GetProfileInteractor(interactorExecutor, mainThread, apiService, sessionManager)
    }

    private fun provideInteractorExecutor(): InteractorExecutor {
        return ThreadExecutor()
    }

    private fun provideMainThread(): MainThread {
        return MainThreadImp()
    }

    fun provideSessionManager(): SessionManager {
        return SessionManager.getInstance()
    }
}

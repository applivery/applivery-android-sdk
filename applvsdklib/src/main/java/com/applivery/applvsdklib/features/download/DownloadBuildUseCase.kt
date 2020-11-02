/*
 * Copyright (c) 2020 Applivery
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
package com.applivery.applvsdklib.features.download

import com.applivery.applvsdklib.AppliverySdk
import com.applivery.applvsdklib.presentation.ErrorManager
import com.applivery.applvsdklib.ui.views.login.LoginView
import com.applivery.base.AppliveryDataManager
import com.applivery.base.domain.SessionManager
import com.applivery.base.domain.model.AppConfig
import com.applivery.base.domain.model.AppData
import com.applivery.base.util.AppliveryLog
import com.applivery.data.AppliveryApiService
import com.applivery.data.response.ServerResponse
import com.applivery.updates.DownloadService.Companion.startDownloadService
import com.google.gson.JsonParseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class DownloadBuildUseCase(
    private val apiService: AppliveryApiService,
    private val errorManager: ErrorManager,
    private val sessionManager: SessionManager
) {

    fun downloadLastBuild() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val appConfigResponse = withContext(Dispatchers.IO) { apiService.obtainAppConfig() }
                val appData = appConfigResponse.data.toAppData()
                AppliveryDataManager.appData = appData
                checkLoginAndInitDownload(appData)

            } catch (parseException: JsonParseException) {
                AppliveryLog.error("Parse app config error", parseException)
            } catch (httpException: HttpException) {
                AppliveryLog.error("Get config - Network error", httpException)
                handleError(httpException)
            } catch (ioException: IOException) {
                AppliveryLog.error("Get app config error", ioException)
            }
        }
    }

    fun downloadLastBuild(appData: AppData) {
        checkLoginAndInitDownload(appData)
    }

    private fun checkLoginAndInitDownload(appData: AppData) {
        if (needLogin(appData.appConfig)) {
            showLogin()
        } else {
            updateApp()
        }
    }

    private fun updateApp() {
        if (AppliverySdk.isContextAvailable()) {
            val context = AppliverySdk.getApplicationContext()
            startDownloadService(context!!)
        } else {
            AppliveryLog.error("Cannot init Download service")
        }
    }

    private fun needLogin(appConfig: AppConfig): Boolean {
        val isAuthUpdate = appConfig.forceAuth
        return isAuthUpdate && !sessionManager.hasSession()
    }

    private fun showLogin() {
        val loginView = LoginView(AppliverySdk.getCurrentActivity()) {
            updateApp()
        }
        loginView.presenter.requestLogin()
    }

    private fun handleError(exception: HttpException) {
        val error = ServerResponse.parseError(exception)
        error?.toFailure()?.let { errorManager.showError(it) }
    }

    companion object {
        @Volatile
        private var instance: DownloadBuildUseCase? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: buildDownloadBuildUseCase().also {
                    instance = it
                }
            }

        private fun buildDownloadBuildUseCase() = DownloadBuildUseCase(
            apiService = AppliveryApiService.getInstance(),
            errorManager = ErrorManager(),
            sessionManager = SessionManager.getInstance()
        )
    }
}

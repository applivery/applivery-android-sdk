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
package com.applivery.applvsdklib.features.appconfig

import com.applivery.applvsdklib.AppliverySdk
import com.applivery.applvsdklib.IsUpToDateCallback
import com.applivery.applvsdklib.domain.model.PackageInfo
import com.applivery.applvsdklib.tools.androidimplementations.AndroidCurrentAppInfo
import com.applivery.base.AppliveryDataManager
import com.applivery.base.domain.model.AppConfig
import com.applivery.base.util.AppliveryLog
import com.applivery.data.AppliveryApiService
import com.google.gson.JsonParseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class AppConfigUseCase(
    private val apiService: AppliveryApiService,
    private val updateManager: UpdateManager,
    private val packageInfo: PackageInfo
) {

    fun getAppConfig(checkForUpdates: Boolean, upToDateCallback: IsUpToDateCallback?) =
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val appConfig = withContext(Dispatchers.IO) { apiService.obtainAppConfig() }.data
                AppliveryDataManager.appData = appConfig.toAppData()
                val appData = appConfig.toAppData()

                upToDateCallback?.onIsUpToDateCheck(appData.appConfig.isUpdated())

                if (checkForUpdates) {
                    updateManager.checkForUpdates(appData)
                }

            } catch (parseException: JsonParseException) {
                AppliveryLog.error("Parse app config error", parseException)
            } catch (httpException: HttpException) {
                AppliveryLog.error("Get config - Network error", httpException)
            } catch (ioException: IOException) {
                AppliveryLog.error("Get app config error", ioException)
            }
        }

    private fun AppConfig.isUpdated() = with(this) {
        packageInfo.version >= lastBuildVersion
    }

    companion object {
        @Volatile
        private var instance: AppConfigUseCase? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: buildAppConfigUseCase().also {
                    instance = it
                }
            }

        private fun buildAppConfigUseCase() = AppConfigUseCase(
            apiService = AppliveryApiService.getInstance(),
            updateManager = UpdateManager.getInstance(),
            packageInfo = AndroidCurrentAppInfo.getPackageInfo(AppliverySdk.getApplicationContext())
        )
    }
}
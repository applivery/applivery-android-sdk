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
import com.applivery.applvsdklib.domain.appconfig.update.UpdateListenerImpl
import com.applivery.applvsdklib.tools.androidimplementations.AndroidCurrentAppInfo.Companion.getPackageInfo
import com.applivery.applvsdklib.ui.views.update.UpdateListener
import com.applivery.applvsdklib.ui.views.update.UpdateViewPresenter
import com.applivery.base.domain.SessionManager
import com.applivery.base.domain.model.AppConfig
import com.applivery.base.domain.model.AppData
import com.applivery.base.domain.model.PackageInfo
import com.applivery.base.domain.model.UpdateType

class UpdateManager(
    private val packageInfo: PackageInfo,
    private val sessionManager: SessionManager
) {

    fun checkForUpdates(appData: AppData) {
        val updateType = appData.appConfig.getUpdateType()
        val presenter = UpdateViewPresenter(getUpdateListener(appData))

        showUpdate(presenter, updateType, appData.name)
    }

    private fun showUpdate(
        updateViewPresenter: UpdateViewPresenter,
        updateType: UpdateType,
        name: String
    ) {
        when (updateType) {
            UpdateType.FORCED_UPDATE -> {
                //TODO add update msg
                updateViewPresenter.showForcedUpdate(name, "")
            }
            UpdateType.SUGGESTED_UPDATE -> {
                //TODO add update msg
                updateViewPresenter.showSuggestedUpdate(name, "TODO")
            }
            else -> {
            }
        }
    }

    private fun getUpdateListener(appData: AppData): UpdateListener {
        return UpdateListenerImpl.getInstance(appData)
    }

    private fun AppConfig.getUpdateType(): UpdateType = with(this) {
        if (forceUpdate && minVersion > packageInfo.version) {
            UpdateType.FORCED_UPDATE

        } else if (ota && lastBuildVersion > packageInfo.version) {
            UpdateType.SUGGESTED_UPDATE

        } else {
            UpdateType.NO_UPDATE
        }
    }

    companion object {
        @Volatile
        private var instance: UpdateManager? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: buildUpdateManager().also {
                    instance = it
                }
            }

        private fun buildUpdateManager() = UpdateManager(
            packageInfo = getPackageInfo(AppliverySdk.getApplicationContext()),
            sessionManager = SessionManager.getInstance()
        )
    }
}

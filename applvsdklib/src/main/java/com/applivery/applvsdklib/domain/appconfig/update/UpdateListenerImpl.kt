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
package com.applivery.applvsdklib.domain.appconfig.update

import com.applivery.applvsdklib.features.download.DownloadBuildUseCase
import com.applivery.applvsdklib.ui.views.update.UpdateListener
import com.applivery.base.domain.model.AppData

/**
 * Created by Sergio Martinez Rodriguez
 * Date 10/1/16.
 */
class UpdateListenerImpl(
    private val appData: AppData,
    private val downloadBuildUseCase: DownloadBuildUseCase
) : UpdateListener {

    override fun onUpdateButtonClick() {
        downloadBuildUseCase.downloadLastBuild(appData)
    }

    companion object {
        @Volatile
        private var instance: UpdateListener? = null

        fun getInstance(appData: AppData) =
            instance ?: synchronized(this) {
                instance ?: buildUpdateListenerImpl(appData).also {
                    instance = it
                }
            }

        private fun buildUpdateListenerImpl(appData: AppData) = UpdateListenerImpl(
            appData = appData,
            downloadBuildUseCase = DownloadBuildUseCase.getInstance()
        )
    }
}

/*
 * Copyright (c) 2016 Applivery
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

package com.applivery.applvsdklib.domain.model

import com.applivery.base.util.AppliveryLog

class AppConfig(
    val description: String,
    val id: String,
    val name: String,
    val oss: List<String>,
    val sdk: Sdk,
    val slug: String
) : BusinessObject<AppConfig> {

    override fun getObject(): AppConfig {
        return this
    }
}

data class Sdk(
    val android: Android
)

data class Android(
    val forceAuth: Boolean,
    val forceUpdate: Boolean,
    val lastBuildId: String,
    val lastBuildVersion: String,
    val minVersion: String,
    val ota: Boolean
) {

    fun isUpdated(currentVersion: Long): Boolean {

        var lastVersion: Long = -1

        try {
            lastVersion = Integer.valueOf(lastBuildVersion).toLong()
        } catch (n: NumberFormatException) {
            AppliveryLog.error("isUpdated() - value $lastBuildVersion")
        }

        return currentVersion >= lastVersion
    }
}

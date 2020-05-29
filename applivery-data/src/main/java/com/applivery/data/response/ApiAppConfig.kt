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
package com.applivery.data.response

import com.applivery.base.domain.model.AppConfig
import com.applivery.base.domain.model.AppData
import com.google.gson.annotations.SerializedName

data class ApiAppConfig(
    @SerializedName("description")
    val description: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("oss")
    val oss: List<String>?,
    @SerializedName("sdk")
    val sdk: SdkEntity,
    @SerializedName("slug")
    val slug: String?
) {

    fun toAppData() = AppData(
        id = id ?: "",
        name = name ?: "",
        description = description ?: "",
        slug = slug ?: "",
        appConfig = AppConfig(
            forceAuth = sdk.android.forceAuth ?: false,
            forceUpdate = sdk.android.forceUpdate ?: false,
            lastBuildId = sdk.android.lastBuildId ?: "",
            lastBuildVersion = sdk.android.lastBuildVersion?.toIntOrNull() ?: -1,
            minVersion = sdk.android.minVersion?.toIntOrNull() ?: -1,
            ota = sdk.android.ota ?: false
        )
    )
}

data class SdkEntity(
    @SerializedName("android")
    val android: AndroidEntity
)

data class AndroidEntity(
    @SerializedName("forceAuth")
    val forceAuth: Boolean?,
    @SerializedName("forceUpdate")
    val forceUpdate: Boolean?,
    @SerializedName("lastBuildId")
    val lastBuildId: String?,
    @SerializedName("lastBuildVersion")
    val lastBuildVersion: String?,
    @SerializedName("minVersion")
    val minVersion: String?,
    @SerializedName("ota")
    val ota: Boolean?
)
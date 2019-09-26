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
package com.applivery.applvsdklib.network.api.model

import com.applivery.applvsdklib.domain.model.Android
import com.applivery.applvsdklib.domain.model.AppConfig
import com.applivery.applvsdklib.domain.model.Sdk
import com.applivery.base.domain.model.AppData
import com.google.gson.annotations.SerializedName

data class AppDataEntity(
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
    fun toAppConfig() = AppConfig(
        description ?: "",
        id ?: "",
        name ?: "",
        oss ?: emptyList(),
        sdk.toSdk(),
        slug ?: ""
    )

    fun toAppData() = AppData(
        id = id ?: "",
        name = name ?: "",
        description = description ?: "",
        slug = slug ?: "",
        appConfig = com.applivery.base.domain.model.AppConfig(
            forceAuth = sdk.android.forceAuth ?: false,
            forceUpdate = sdk.android.forceUpdate ?: false,
            lastBuildId = sdk.android.lastBuildId ?: "",
            lastBuildVersion = sdk.android.lastBuildVersion ?: "",
            minVersion = sdk.android.minVersion ?: "",
            ota = sdk.android.ota ?: false
        )
    )
}

data class SdkEntity(
    @SerializedName("android")
    val android: AndroidEntity
) {
    fun toSdk() = Sdk(android.toAndroid())
}

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
) {
    fun toAndroid() = Android(
        forceAuth ?: false,
        forceUpdate ?: false,
        lastBuildId ?: "",
        lastBuildVersion ?: "",
        minVersion ?: "",
        ota ?: false
    )
}

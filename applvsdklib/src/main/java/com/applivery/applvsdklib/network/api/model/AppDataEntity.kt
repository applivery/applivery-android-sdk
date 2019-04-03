package com.applivery.applvsdklib.network.api.model

import com.applivery.applvsdklib.domain.model.Android
import com.applivery.applvsdklib.domain.model.AppConfig
import com.applivery.applvsdklib.domain.model.Sdk
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
package com.applivery.applvsdklib.network.api.model

import com.applivery.applvsdklib.domain.model.DownloadToken
import com.google.gson.annotations.SerializedName


data class DownloadTokenEntity(
  @SerializedName("downloadToken")
  val downloadToken: String?,
  @SerializedName("expiresAt")
  val expiresAt: String?
) {
  fun toDownloadToken(buildId: String) = DownloadToken(
    downloadToken ?: "",
    buildId,
    expiresAt ?: ""
  )
}
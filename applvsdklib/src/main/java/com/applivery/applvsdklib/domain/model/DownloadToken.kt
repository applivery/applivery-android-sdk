package com.applivery.applvsdklib.domain.model


data class DownloadToken(
  val downloadToken: String,
  val buildId: String,
  val expiresAt: String
) : BusinessObject<DownloadToken> {

  override fun getObject(): DownloadToken {
    return this
  }
}
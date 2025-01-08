package com.applivery.android.sdk.data.models

import com.google.gson.annotations.SerializedName

data class BuildTokenApi(
    @SerializedName("token") val token: String?,
    @SerializedName("expiresAt") val expiresAt: String?
)

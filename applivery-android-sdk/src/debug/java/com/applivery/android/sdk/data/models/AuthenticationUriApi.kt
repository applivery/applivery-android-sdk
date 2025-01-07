package com.applivery.android.sdk.data.models

import com.google.gson.annotations.SerializedName

data class AuthenticationUriApi(
    @SerializedName("redirectUrl") val uri: String?,
)

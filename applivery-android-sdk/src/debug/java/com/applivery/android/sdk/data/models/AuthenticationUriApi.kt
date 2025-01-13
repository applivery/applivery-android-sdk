package com.applivery.android.sdk.data.models

import com.google.gson.annotations.SerializedName

internal data class AuthenticationUriApi(
    @SerializedName("redirectUrl") val uri: String?,
)

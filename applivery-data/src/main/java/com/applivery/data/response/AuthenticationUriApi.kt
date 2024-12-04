package com.applivery.data.response

import com.google.gson.annotations.SerializedName

data class AuthenticationUriApi(
    @SerializedName("redirectUrl") val uri: String?,
)

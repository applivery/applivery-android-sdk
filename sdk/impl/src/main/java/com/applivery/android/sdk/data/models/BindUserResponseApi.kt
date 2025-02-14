package com.applivery.android.sdk.data.models

import com.google.gson.annotations.SerializedName

internal data class BindUserResponseApi(
    @SerializedName("bearer") val bearer: String?,
    @SerializedName("member") val user: UserApi?
)

package com.applivery.android.sdk.data.models

import com.google.gson.annotations.SerializedName

internal data class BindUserApi(
    @SerializedName("email") val email: String,
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("lastName") val lastName: String?,
    @SerializedName("tags") val tags: Collection<String>?
) 
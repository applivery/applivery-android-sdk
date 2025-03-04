package com.applivery.android.sdk.data.models

import com.google.gson.annotations.SerializedName

internal class ApiResponseSchema<T>(@SerializedName("data") val data: T?)
package com.applivery.android.sdk.data.models

import com.google.gson.annotations.SerializedName

class ServerResponseSchema<T>(@SerializedName("data") val data: T?)
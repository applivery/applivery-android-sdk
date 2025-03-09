package com.applivery.android.sdk.data.models

import com.google.gson.annotations.SerializedName

data class SendFeedbackResponseApi(
    @SerializedName("videoFile") val videoFile: VideoFileStorageApi?
)

data class VideoFileStorageApi(
    @SerializedName("location") val locationUrl: String?
)

package com.applivery.android.sdk.data.api.service

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming

internal interface AppliveryDownloadService {

    @GET("v1/download/{download_token}")
    @Streaming
    suspend fun downloadBuild(@Path("download_token") downloadToken: String): ResponseBody
}

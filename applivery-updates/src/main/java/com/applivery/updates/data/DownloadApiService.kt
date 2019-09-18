package com.applivery.updates.data

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming

private const val API_VERSION = "/v1"

interface DownloadApiService {

    @GET("$API_VERSION/download/{download_token}")
    @Streaming
    fun downloadBuild(@Path("download_token") downloadToken: String): Call<ResponseBody>
}

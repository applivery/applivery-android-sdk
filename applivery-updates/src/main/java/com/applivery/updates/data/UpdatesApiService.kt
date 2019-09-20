package com.applivery.updates.data

import com.applivery.base.data.ServerResponse
import com.applivery.updates.data.response.ApiBuildToken
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

private const val API_VERSION = "/v1"

interface UpdatesApiService {

    @GET("$API_VERSION/build/{build_id}/downloadToken")
    fun obtainBuildToken(@Path("build_id") buildId: String): Call<ServerResponse<ApiBuildToken>>
}


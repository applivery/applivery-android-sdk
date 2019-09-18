package com.applivery.updates.data

import com.applivery.updates.data.response.ApiBuildTokenResponse
import retrofit2.http.GET
import retrofit2.http.Path

private const val API_VERSION = "/v1"

interface UpdatesApiService {

    @GET("$API_VERSION/build/{build_id}/downloadToken")
    suspend fun obtainBuildToken(@Path("build_id") buildId: String): ApiBuildTokenResponse
}


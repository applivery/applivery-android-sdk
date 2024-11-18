package com.applivery.data

import com.applivery.base.AppliveryDataManager
import com.applivery.data.ApiUriBuilder.buildUponTenant
import com.applivery.data.di.InjectorUtils
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming

private const val API_VERSION = "/v1"

interface DownloadApiService {

    @GET("$API_VERSION/download/{download_token}")
    @Streaming
    fun downloadBuild(@Path("download_token") downloadToken: String): Call<ResponseBody>

    companion object {
        @Volatile
        private var instance: DownloadApiService? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: provideDownloadApiService().also {
                    instance = it
                }
            }

        private fun provideDownloadApiService(): DownloadApiService {
            val url = BuildConfig.DOWNLOAD_API_URL.buildUponTenant(AppliveryDataManager.tenant)
            return Retrofit.Builder().baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(InjectorUtils.provideOkHttpClient())
                .build()
                .create(DownloadApiService::class.java)
        }
    }
}

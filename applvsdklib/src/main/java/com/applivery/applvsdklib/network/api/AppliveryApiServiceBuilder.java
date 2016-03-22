package com.applivery.applvsdklib.network.api;

import com.applivery.applvsdklib.BuildConfig;
import com.applivery.applvsdklib.tools.androidimplementations.AndroidCurrentAppInfo;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 18/10/15.
 */
public class AppliveryApiServiceBuilder {

  public static AppliveryApiService getAppliveryApiInstance(AndroidCurrentAppInfo androidCurrentAppInfo){

    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();

    OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
    okHttpClientBuilder.interceptors().add(new HeadersInterceptor(androidCurrentAppInfo));
    if (BuildConfig.DEBUG){ okHttpClientBuilder.interceptors().add(loggingInterceptor);}

    return new Retrofit.Builder().baseUrl(BuildConfig.API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClientBuilder.build())
        .build()
        .create(AppliveryApiService.class);
  }
}

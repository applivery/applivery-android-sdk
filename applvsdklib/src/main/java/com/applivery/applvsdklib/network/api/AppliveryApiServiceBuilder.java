package com.applivery.applvsdklib.network.api;

import com.applivery.applvsdklib.BuildConfig;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 18/10/15.
 */
public class AppliveryApiServiceBuilder {

  public static AppliveryApiService getAppliveryApiInstance(){

    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();

    OkHttpClient okHttpClient = new OkHttpClient();
    okHttpClient.interceptors().add(new HeadersInterceptor());
    if (BuildConfig.DEBUG){ okHttpClient.interceptors().add(loggingInterceptor);}

    return new Retrofit.Builder().baseUrl(BuildConfig.API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()
        .create(AppliveryApiService.class);
  }
}

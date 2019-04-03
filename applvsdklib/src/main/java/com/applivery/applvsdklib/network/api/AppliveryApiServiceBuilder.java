/*
 * Copyright (c) 2016 Applivery
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.applivery.applvsdklib.network.api;

import com.applivery.applvsdklib.BuildConfig;
import com.applivery.applvsdklib.network.api.interceptor.HeadersInterceptor;
import com.applivery.applvsdklib.tools.injection.Injection;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 18/10/15.
 */
public class AppliveryApiServiceBuilder {

  public static AppliveryApiService getAppliveryApiInstance() {

    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

    OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
    okHttpClientBuilder.interceptors().add(new HeadersInterceptor());
    okHttpClientBuilder.interceptors().add(Injection.INSTANCE.provideSessionInterceptor());

    if (BuildConfig.DEBUG) {
      okHttpClientBuilder.interceptors().add(loggingInterceptor);
    }

    return new Retrofit.Builder().baseUrl(BuildConfig.API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClientBuilder.build())
        .build()
        .create(AppliveryApiService.class);
  }

  public static DownloadApiService getDownloadApiServiceInstance() {

    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

    OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
    okHttpClientBuilder.interceptors().add(new HeadersInterceptor());
    okHttpClientBuilder.interceptors().add(Injection.INSTANCE.provideSessionInterceptor());

    if (BuildConfig.DEBUG) {
      okHttpClientBuilder.interceptors().add(loggingInterceptor);
    }

    return new Retrofit.Builder().baseUrl(BuildConfig.DOWNLOAD_API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClientBuilder.build())
        .build()
        .create(DownloadApiService.class);
  }
}

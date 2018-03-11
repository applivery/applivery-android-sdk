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

package com.applivery.applvsdklib.network.api.interceptor;

import com.applivery.applvsdklib.AppliverySdk;
import java.io.IOException;
import java.util.Locale;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 11/1/16.
 */
public class HeadersInterceptor implements Interceptor {

  public HeadersInterceptor() {

  }

  @Override public Response intercept(Chain chain) throws IOException {
    return chain.proceed(composeRequest(chain));
  }

  private Request composeRequest(Chain chain) {
    Request original = chain.request();
    return original.newBuilder()
        .url(chain.request().url())
        .header("Accept-Language", Locale.getDefault().getLanguage())
        .header("x_sdk_version", "ANDROID_" + AppliverySdk.getVersionName())
        .method(original.method(), original.body())
        .build();
  }
}

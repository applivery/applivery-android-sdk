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

import com.applivery.applvsdklib.network.api.model.ApiFeedbackData;
import com.applivery.applvsdklib.network.api.model.ApiLogin;
import com.applivery.applvsdklib.network.api.responses.ApiAppConfigResponse;
import com.applivery.applvsdklib.network.api.responses.ApiBuildTokenResponse;
import com.applivery.applvsdklib.network.api.responses.ApiFeedbackResponse;
import com.applivery.applvsdklib.network.api.responses.ApiLoginResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 18/10/15.
 */
public interface AppliveryApiService {

  @GET("/api/apps/{app_id}") Call<ApiAppConfigResponse> obtainAppConfig(@Path("app_id") String appId);

  @GET("/api/builds/{build_id}/token")
  Call<ApiBuildTokenResponse> obtainBuildToken(@Path("build_id") String buildId);

  @GET("/download/{build_id}/manifest/{download_token}")
  @Streaming
  Call<ResponseBody> downloadBuild(@Path("build_id") String buildId, @Path("download_token") String download_token);

  @POST("/api/feedback")
  Call<ApiFeedbackResponse> sendFeedback(@Body ApiFeedbackData feedback);

  @POST("/api/auth")
  Call<ApiLoginResponse> makeLogin(@Body ApiLogin userData);


  //TODO this will be implemented in second phase
  //@POST("/api/crashes/")
  //Call<ApiCrashResponse> reportCrash(ApiCrashRequest crashRequest);

}


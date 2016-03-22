package com.applivery.applvsdklib.network.api;

import com.applivery.applvsdklib.network.api.requests.ApiFeedbackRequestData;
import com.applivery.applvsdklib.network.api.responses.ApiAppConfigResponse;
import com.applivery.applvsdklib.network.api.responses.ApiBuildTokenResponse;
import com.applivery.applvsdklib.network.api.responses.ApiFeedbackResponse;
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

  @POST("/api/bugs")
  Call<ApiFeedbackResponse> sendFeedback(@Body ApiFeedbackRequestData bugRequest);


  //TODO this will be implemented in second phase
  //@POST("/api/crashes/")
  //Call<ApiCrashResponse> reportCrash(ApiCrashRequest crashRequest);

}


package com.applivery.applvsdklib.utils;

import com.applivery.applvsdklib.network.api.responses.ApiAppConfigResponse;
import com.applivery.applvsdklib.network.api.responses.ApiBuildTokenResponse;
import com.squareup.okhttp.ResponseBody;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Streaming;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 25/12/15.
 */
public interface AppliveryTestApi {

  @GET("/api/apps/{app_id}") Call<ApiAppConfigResponse> obtainAppConfig(@Path("app_id") String appId);

  @GET("/api/builds/{build_id}/token")
  Call<ApiBuildTokenResponse> obtainBuildToken(@Path("build_id") String buildId);

  @Streaming @GET("/download/{build_id}/manifest/{download_token}")
  Call<ResponseBody> downloadBuild(@Path("build_id") String buildId, @Path("download_token") String download_token);

}

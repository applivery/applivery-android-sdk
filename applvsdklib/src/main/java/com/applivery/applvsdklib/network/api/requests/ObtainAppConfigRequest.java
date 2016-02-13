package com.applivery.applvsdklib.network.api.requests;

import com.applivery.applvsdklib.network.api.AppliveryApiService;
import com.applivery.applvsdklib.domain.model.BusinessObject;
import com.applivery.applvsdklib.network.api.requests.mappers.AndroidMapper;
import com.applivery.applvsdklib.network.api.requests.mappers.ApiAppConfigResponseMapper;
import com.applivery.applvsdklib.network.api.requests.mappers.SdkMapper;
import com.applivery.applvsdklib.network.api.responses.ApiAppConfigResponse;
import retrofit.Call;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 18/10/15.
 */
public class ObtainAppConfigRequest extends ServerRequest {

  private final AppliveryApiService apiService;
  private final String appId;
  private final String token;
  private final ApiAppConfigResponseMapper apiAppConfigResponseMapper;

  public ObtainAppConfigRequest(AppliveryApiService apiService, String appId, String token) {
    this.apiService = apiService;
    this.appId = appId;
    this.token = token;

    SdkMapper sdkMapper = new SdkMapper(new AndroidMapper());
    this.apiAppConfigResponseMapper = new ApiAppConfigResponseMapper(sdkMapper);
  }

  @Override protected BusinessObject performRequest() {
    Call<ApiAppConfigResponse> response = apiService.obtainAppConfig(appId);
    ApiAppConfigResponse apiAppConfigResponse = super.performRequest(response);
    BusinessObject businessObject = apiAppConfigResponseMapper.map(apiAppConfigResponse);
    return businessObject;
  }



}

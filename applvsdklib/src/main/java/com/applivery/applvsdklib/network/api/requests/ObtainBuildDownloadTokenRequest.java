package com.applivery.applvsdklib.network.api.requests;

import com.applivery.applvsdklib.network.api.AppliveryApiService;
import com.applivery.applvsdklib.domain.model.BusinessObject;
import com.applivery.applvsdklib.network.api.requests.mappers.ObtainBuildDownloadTokenResponseMapper;
import com.applivery.applvsdklib.network.api.responses.ApiBuildTokenResponse;
import retrofit2.Call;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 9/1/16.
 */
public class ObtainBuildDownloadTokenRequest extends ServerRequest {

  private final AppliveryApiService apiService;
  private final String buildId;
  private final ObtainBuildDownloadTokenResponseMapper obtainBuildTokenResponseMapper;

  public ObtainBuildDownloadTokenRequest(AppliveryApiService apiService, String buildId) {
    this.apiService = apiService;
    this.buildId = buildId;
    this.obtainBuildTokenResponseMapper = new ObtainBuildDownloadTokenResponseMapper();
  }

  @Override protected BusinessObject performRequest() {
    Call<ApiBuildTokenResponse> response = apiService.obtainBuildToken(buildId);
    ApiBuildTokenResponse apiBuildTokenResponse = super.performRequest(response);
    BusinessObject businessObject = obtainBuildTokenResponseMapper.map(apiBuildTokenResponse);
    return businessObject;
  }

}

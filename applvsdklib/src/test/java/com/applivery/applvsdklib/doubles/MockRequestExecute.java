package com.applivery.applvsdklib.doubles;

import com.applivery.applvsdklib.domain.model.AppConfig;
import com.applivery.applvsdklib.domain.model.BusinessObject;
import com.applivery.applvsdklib.network.api.requests.ServerRequest;
import com.applivery.applvsdklib.network.api.requests.mappers.BaseMapper;
import com.applivery.applvsdklib.network.api.responses.ApiAppConfigResponse;
import com.applivery.applvsdklib.network.api.responses.ServerResponse;
import com.applivery.applvsdklib.utils.AppliveryTestApi;
import retrofit.Call;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 25/12/15.
 */
public class MockRequestExecute extends ServerRequest {

    private AppliveryTestApi apiService;
    private String requestPath;
    private BaseMapper requestMapper;

  public MockRequestExecute(AppliveryTestApi apiService, String requestPath) {
    this.apiService = apiService;
    this.requestPath = requestPath;
    requestMapper = new BaseMapper() {
      @Override protected BusinessObject mapBusinessObject(Object serverResponse) {
        return new AppConfig();
      }
    };
  }

  @Override protected BusinessObject performRequest() {
      Call<ApiAppConfigResponse> response = apiService.obtainAppConfig(requestPath);
      ApiAppConfigResponse apiAppConfigResponse = super.performRequest(response);
      BusinessObject businessObject = requestMapper.map(apiAppConfigResponse);
      return businessObject;
    }

  public <T extends ServerResponse> T performSuperRequest(Call<T> call){
    return super.performRequest(call);
  }


}

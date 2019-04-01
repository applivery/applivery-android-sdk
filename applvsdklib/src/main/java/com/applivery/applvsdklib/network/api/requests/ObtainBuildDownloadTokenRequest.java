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

package com.applivery.applvsdklib.network.api.requests;

import com.applivery.applvsdklib.domain.model.BusinessObject;
import com.applivery.applvsdklib.network.api.AppliveryApiService;
import com.applivery.applvsdklib.network.api.model.DownloadTokenEntity;
import com.applivery.applvsdklib.network.api.responses.ApiBuildTokenResponse;
import retrofit2.Call;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 9/1/16.
 */
public class ObtainBuildDownloadTokenRequest extends ServerRequest {

  private final AppliveryApiService apiService;
  private final String buildId;

  public ObtainBuildDownloadTokenRequest(AppliveryApiService apiService, String buildId) {
    this.apiService = apiService;
    this.buildId = buildId;
  }

  @Override protected BusinessObject performRequest() {
    Call<ApiBuildTokenResponse> response = apiService.obtainBuildToken(buildId);
    ApiBuildTokenResponse apiBuildTokenResponse = super.performRequest(response);

    DownloadTokenEntity downloadTokenEntity = apiBuildTokenResponse.getData();

    return downloadTokenEntity.toDownloadToken(buildId);
  }
}

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
import com.applivery.applvsdklib.domain.model.ErrorObject;
import com.applivery.applvsdklib.network.api.AppliveryApiService;
import com.applivery.applvsdklib.network.api.model.AppDataEntity;
import com.applivery.applvsdklib.network.api.responses.ApiAppConfigResponse;
import com.applivery.base.AppliveryDataManager;
import com.google.gson.JsonParseException;

import retrofit2.Call;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 18/10/15.
 */
public class ObtainAppConfigRequest extends ServerRequest {

    private final AppliveryApiService apiService;

    public ObtainAppConfigRequest(AppliveryApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    protected BusinessObject performRequest() {

        BusinessObject appConfig;

        try {
            Call<ApiAppConfigResponse> response = apiService.obtainAppConfig();

            ApiAppConfigResponse apiAppConfigResponse = super.performRequest(response);
            AppDataEntity appDataEntity = apiAppConfigResponse.getData();
            AppliveryDataManager.INSTANCE.setAppData(appDataEntity.toAppData());

            appConfig = appDataEntity.toAppConfig();
        } catch (JsonParseException exception) {
            appConfig = new ErrorObject();
        }

        return appConfig;
    }
}

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
package com.applivery.applvsdklib.domain.appconfig;

import com.applivery.applvsdklib.domain.BaseInteractor;
import com.applivery.applvsdklib.domain.InteractorCallback;
import com.applivery.applvsdklib.domain.model.AppConfig;
import com.applivery.applvsdklib.domain.model.BusinessObject;
import com.applivery.applvsdklib.domain.model.ErrorObject;
import com.applivery.applvsdklib.domain.model.PackageInfo;
import com.applivery.applvsdklib.network.api.AppliveryApiService;
import com.applivery.applvsdklib.network.api.requests.ObtainAppConfigRequest;
import com.applivery.base.domain.SessionManager;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 7/11/15.
 */
public class ObtainAppConfigInteractor extends BaseInteractor<AppConfig> {

    private final ObtainAppConfigRequest obtainAppConfigRequest;
    private final InteractorCallback appConfigInteractorCallback;

    private ObtainAppConfigInteractor(AppliveryApiService apiService,
                                      SessionManager sessionManager,
                                      PackageInfo packageInfo,
                                      Boolean checkForUpdates) {
        this.obtainAppConfigRequest = new ObtainAppConfigRequest(apiService);
        this.appConfigInteractorCallback =
                new ObtainAppConfigInteractorCallback(sessionManager, packageInfo, checkForUpdates);
    }

    @Override
    protected void receivedResponse(BusinessObject obj) {
        super.receivedResponse(obj, AppConfig.class);
    }

    @Override
    protected void error(ErrorObject errorObject) {
        appConfigInteractorCallback.onError(errorObject);
    }

    @Override
    protected void success(AppConfig appConfig) {
        appConfigInteractorCallback.onSuccess(appConfig);
    }

    @Override
    protected BusinessObject performRequest() {
        return obtainAppConfigRequest.execute();
    }

    public static Runnable getInstance(AppliveryApiService appliveryApiService,
                                       SessionManager sessionManager,
                                       PackageInfo packageInfo,
                                       Boolean checkForUpdates) {

        return new ObtainAppConfigInteractor(appliveryApiService, sessionManager,
                packageInfo, checkForUpdates);
    }
}

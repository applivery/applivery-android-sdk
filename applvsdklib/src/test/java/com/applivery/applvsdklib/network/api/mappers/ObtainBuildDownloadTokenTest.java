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

package com.applivery.applvsdklib.network.api.mappers;

import com.applivery.applvsdklib.domain.model.BuildTokenInfo;
import com.applivery.applvsdklib.domain.model.BusinessObject;
import com.applivery.applvsdklib.network.api.AppliveryApiService;
import com.applivery.applvsdklib.network.api.requests.ObtainBuildDownloadTokenRequest;
import com.applivery.applvsdklib.utils.MockAppliveryInstance;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 1/1/16.
 */
public class ObtainBuildDownloadTokenTest {

  private AppliveryApiService appliveryApiService;

  @Before public void setUp(){
    appliveryApiService = MockAppliveryInstance.getApiServiceInstance();
  }

  @Test public void requestTest(){

    ObtainBuildDownloadTokenRequest obtainBuildDownloadTokenRequest =
        new ObtainBuildDownloadTokenRequest(appliveryApiService, "1");

    BusinessObject<BuildTokenInfo> businessObject = obtainBuildDownloadTokenRequest.execute();

    assertNotNull(businessObject);
    assertNotNull(businessObject.getObject());
    assertTrue(businessObject.getObject() instanceof BuildTokenInfo);

  }

}

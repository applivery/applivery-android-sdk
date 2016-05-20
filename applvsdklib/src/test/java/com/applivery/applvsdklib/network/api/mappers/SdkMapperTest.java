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

import com.applivery.applvsdklib.builders.ApiAndroidBuilder;
import com.applivery.applvsdklib.builders.ApiSdkBuilder;
import com.applivery.applvsdklib.domain.model.Android;
import com.applivery.applvsdklib.domain.model.Sdk;
import com.applivery.applvsdklib.network.api.model.ApiAndroid;
import com.applivery.applvsdklib.network.api.model.ApiSdK;
import com.applivery.applvsdklib.network.api.requests.mappers.AndroidMapper;
import com.applivery.applvsdklib.network.api.requests.mappers.SdkMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 24/1/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class SdkMapperTest {

  @Mock Android android;
  @Mock AndroidMapper androidMapper;

  @Test
  public void shouldReturnNotNullSdkModelInstanceWhenApiSdkIsNotNull()  {
    when(androidMapper.dataToModel(isA(ApiAndroid.class))).thenReturn(android);
    ApiSdK apiSdK = ApiSdkBuilder.Builder().build();
    SdkMapper sdkMapper = new SdkMapper(androidMapper);
    Sdk sdk = sdkMapper.dataToModel(apiSdK);
    assertNotNull(sdk.getAndroid());
  }

}

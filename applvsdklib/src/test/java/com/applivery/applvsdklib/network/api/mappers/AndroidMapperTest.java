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
import com.applivery.applvsdklib.domain.model.Android;
import com.applivery.applvsdklib.network.api.model.ApiAndroid;
import com.applivery.applvsdklib.network.api.requests.mappers.AndroidMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 24/1/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class AndroidMapperTest {

  @Test
  public void shouldReturnAndroidModelInstanceWhenApiAndroidIsNotNull()  {
    ApiAndroid apiAndroid = ApiAndroidBuilder.Builder().build();

    AndroidMapper androidMapper = new AndroidMapper();

    Android android = androidMapper.dataToModel(apiAndroid);

    assertThat(android, instanceOf(Android.class));

    assertEquals(android.getLastBuildId(), ApiAndroidBuilder.LAST_BUILD_ID);
    assertEquals(android.getLastBuildVersion(), ApiAndroidBuilder.LAST_BUILD_VERSION);
    assertEquals(android.getMinVersion(), ApiAndroidBuilder.MIN_VERSION);
    assertEquals(android.getMustUpdateMsg(), ApiAndroidBuilder.MUST_UPDATE_MSG);
    assertEquals(android.getUpdateMsg(), ApiAndroidBuilder.UPDATE_MSG);
    assertEquals(android.isForceUpdate(), ApiAndroidBuilder.FORCE_UPDATE);
    assertEquals(android.isOta(), ApiAndroidBuilder.OTA);

  }

}

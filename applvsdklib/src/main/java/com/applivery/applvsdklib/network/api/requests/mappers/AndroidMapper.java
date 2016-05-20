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

package com.applivery.applvsdklib.network.api.requests.mappers;

import com.applivery.applvsdklib.domain.model.Android;
import com.applivery.applvsdklib.network.api.model.ApiAndroid;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 2/1/16.
 */
public class AndroidMapper implements ResponseMapper<Android, ApiAndroid> {

  @Override public Android dataToModel(ApiAndroid apiAndroid) {
    Android android = new Android();
    android.setLastBuildId(apiAndroid.getLastBuildId());
    android.setLastBuildVersion(apiAndroid.getLastBuildVersion());
    android.setMinVersion(apiAndroid.getMinVersion());
    android.setForceUpdate(apiAndroid.isForceUpdate());
    android.setMustUpdateMsg(apiAndroid.getMustUpdateMsg());
    android.setOta(apiAndroid.isOta());
    android.setUpdateMsg(apiAndroid.getUpdateMsg());
    return android;
  }
}

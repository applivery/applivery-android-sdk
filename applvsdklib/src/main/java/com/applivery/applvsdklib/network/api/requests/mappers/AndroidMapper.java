package com.applivery.applvsdklib.network.api.requests.mappers;

import com.applivery.applvsdklib.domain.model.Android;
import com.applivery.applvsdklib.network.api.model.ApiAndroid;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 2/1/16.
 */
public class AndroidMapper implements ResponseMapper<Android, ApiAndroid>{

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

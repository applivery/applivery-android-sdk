package com.applivery.applvsdklib.network.api.requests.mappers;

import com.applivery.applvsdklib.domain.model.Sdk;
import com.applivery.applvsdklib.network.api.model.ApiSdK;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 2/1/16.
 */
public class SdkMapper implements ResponseMapper<Sdk, ApiSdK> {

  private AndroidMapper androidMapper;

  public SdkMapper(AndroidMapper androidMapper) {
    this.androidMapper = androidMapper;
  }

  @Override public Sdk dataToModel(ApiSdK apiSdK) {
    Sdk sdk = new Sdk();
    sdk.setAndroid(androidMapper.dataToModel(apiSdK.getAndroid()));
    return sdk;
  }
}

package com.applivery.applvsdklib.builders;

import com.applivery.applvsdklib.network.api.model.ApiAndroid;
import com.applivery.applvsdklib.network.api.model.ApiSdK;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 2/1/16.
 */
public class ApiSdkBuilder {

    public static ApiSdkBuilder Builder() {
      return new ApiSdkBuilder();
    }

    public ApiSdK build() {
        ApiSdK apiSdK = new ApiSdK();
        apiSdK.setAndroid(new ApiAndroid());
        return apiSdK;
    }

    }

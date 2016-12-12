package com.applivery.applvsdklib.domain.download.permissions;

import android.Manifest;
import com.applivery.applvsdklib.R;
import com.applivery.applvsdklib.tools.permissions.Permission;

public class AccessNetworkStatePermission implements Permission {
  @Override public String getAndroidPermissionStringType() {
    return Manifest.permission.ACCESS_NETWORK_STATE;
  }

  @Override public int getPermissionSettingsDeniedFeedback() {
    return R.string.applivery_accessNetworkStatePermissionSettingsDeniedFeedback;
  }

  @Override public int getPermissionDeniedFeedback() {
    return R.string.applivery_accessNetworkStatePermissionDeniedFeedback;
  }

  @Override public int getPermissionRationaleTitle() {
    return R.string.applivery_accessNetworkStatePermissionRationaleTitle;
  }

  @Override public int getPermissionRationaleMessage() {
    return R.string.applivery_accessNetworkStatePermissionRationalMessage;
  }
}
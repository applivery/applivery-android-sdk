package com.applivery.applvsdklib.domain.download.permissions;

import android.Manifest;
import com.applivery.applvsdklib.R;
import com.applivery.applvsdklib.tools.permissions.Permission;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 16/1/16.
 */
public class ReadExternalPermission implements Permission {
  @Override public String getAndroidPermissionStringType() {
    return Manifest.permission.READ_EXTERNAL_STORAGE;
  }

  @Override public int getPermissionSettingsDeniedFeedback() {
    return R.string.readOnExternalPermissionSettingsDeniedFeedback;
  }

  @Override public int getPermissionDeniedFeedback() {
    return R.string.readOnExternalPermissionDeniedFeedback;
  }

  @Override public int getPermissionRationaleTitle() {
    return R.string.readOnExternalPermissionRationaleTitle;
  }

  @Override public int getPermissionRationaleMessage() {
    return R.string.readOnExternalPermissionRationaleMessage;
  }
}

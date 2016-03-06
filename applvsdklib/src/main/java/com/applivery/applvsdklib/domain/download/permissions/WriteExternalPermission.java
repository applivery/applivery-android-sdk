package com.applivery.applvsdklib.domain.download.permissions;

import android.Manifest;
import com.applivery.applvsdklib.R;
import com.applivery.applvsdklib.tools.permissions.Permission;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 16/1/16.
 */
public class WriteExternalPermission implements Permission {

  @Override public String getAndroidPermissionStringType() {
    return Manifest.permission.WRITE_EXTERNAL_STORAGE;
  }

  @Override public int getPermissionSettingsDeniedFeedback() {
    return R.string.writeOnExternalPermissionSettingsDeniedFeedback;
  }

  @Override public int getPermissionDeniedFeedback() {
    return R.string.continueRequestPermissionDeniedFeedback;
  }

  @Override public int getPermissionRationaleTitle() {
    return R.string.readOnExternalPermissionRationaleTitle;
  }

  @Override public int getPermissionRationaleMessage() {
    return R.string.continueRequestPermissionRationaleMessage;
  }
}

package com.applivery.applvsdklib.domain.download.permissions;

import android.Manifest;
import com.applivery.applvsdklib.R;
import com.applivery.applvsdklib.tools.permissions.Permission;

/**
 * Created by Andres Hernandez on 12/11/16.
 */
public class ReadScreenshotFolderPermission implements Permission {
  @Override public String getAndroidPermissionStringType() {
    return Manifest.permission.READ_EXTERNAL_STORAGE;
  }

  @Override public int getPermissionSettingsDeniedFeedback() {
    return R.string.applivery_readOnScreenshotFolderPermissionSettingsDeniedFeedback;
  }

  @Override public int getPermissionDeniedFeedback() {
    return R.string.applivery_readOnScreenshotFolderPermissionDeniedFeedback;
  }

  @Override public int getPermissionRationaleTitle() {
    return R.string.applivery_readOnScreenshotFolderPermissionRationaleTitle;
  }

  @Override public int getPermissionRationaleMessage() {
    return R.string.applivery_readOnScreenshotFolderPermissionRationalMessage;
  }
}

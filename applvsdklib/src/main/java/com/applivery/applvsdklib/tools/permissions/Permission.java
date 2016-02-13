package com.applivery.applvsdklib.tools.permissions;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 16/1/16.
 */
public interface Permission {
  String getAndroidPermissionStringType();

  int getPermissionSettingsDeniedFeedback();

  int getPermissionDeniedFeedback();

  int getPermissionRationaleTitle();

  int getPermissionRationaleMessage();
}

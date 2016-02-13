package com.applivery.applvsdklib.tools.permissions;

import android.app.Activity;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 15/1/16.
 */
public interface PermissionChecker {
  void askForPermission(Permission permission, UserPermissionRequestResponseListener userResponse,
      Activity activity);
  void continuePendingPermissionsRequestsIfPossible();
}

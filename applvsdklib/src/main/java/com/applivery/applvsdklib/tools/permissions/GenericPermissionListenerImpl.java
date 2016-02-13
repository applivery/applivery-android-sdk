package com.applivery.applvsdklib.tools.permissions;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 17/1/16.
 */
public class GenericPermissionListenerImpl extends AbstractPermissionListener{

  private final Permission permission;

  public GenericPermissionListenerImpl(Permission permission,
      UserPermissionRequestResponseListener userPermissionRequestResponseListener,
      ContextProvider contextProvider) {
    super(userPermissionRequestResponseListener, contextProvider);
    this.permission = permission;
  }

  @Override public int getPermissionDeniedFeedback() {
    return permission.getPermissionDeniedFeedback();
  }

  @Override public int getPermissionRationaleMessage() {
    return permission.getPermissionRationaleMessage();
  }

  @Override public int getPermissionRationaleTitle() {
    return permission.getPermissionRationaleTitle();
  }
}

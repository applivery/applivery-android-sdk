package com.applivery.applvsdklib.tools.permissions.utils;

import com.applivery.applvsdklib.tools.permissions.AbstractPermissionListener;
import com.applivery.applvsdklib.tools.permissions.ContextProvider;
import com.applivery.applvsdklib.tools.permissions.UserPermissionRequestResponseListener;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 18/1/16.
 */
public class DummyPermissionListenerImplementation extends AbstractPermissionListener {

  private final StubString stubString;

  public DummyPermissionListenerImplementation(ContextProvider contextProvider,
      StubString stubString) {
    super(contextProvider);
    this.stubString = stubString;
  }

  public DummyPermissionListenerImplementation(UserPermissionRequestResponseListener
      userPermissionRequestResponseListener, ContextProvider contextProvider,
      StubString stubString) {
    super(userPermissionRequestResponseListener, contextProvider);
    this.stubString = stubString;
  }

  @Override public int getPermissionDeniedFeedback() {
    return stubString.getPermissionDeniedFeedback();
  }

  @Override public int getPermissionRationaleMessage() {
    return stubString.getPermissionRationaleMessage();
  }

  @Override public int getPermissionRationaleTitle() {
    return stubString.getPermissionRationaleTitle();
  }

}

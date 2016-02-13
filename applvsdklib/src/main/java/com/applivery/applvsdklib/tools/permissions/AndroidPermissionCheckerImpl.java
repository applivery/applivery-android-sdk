package com.applivery.applvsdklib.tools.permissions;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.single.CompositePermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.karumi.dexter.listener.single.SnackbarOnDeniedPermissionListener;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 15/1/16.
 */
public class AndroidPermissionCheckerImpl implements PermissionChecker {

  //TODO this class is impossible to test review With Beni

  private final ContextProvider contextProvider;

  public AndroidPermissionCheckerImpl(Context context, ContextProvider contextProvider) {
    Dexter.initialize(context);
    this.contextProvider = contextProvider;
  }

  @Override public void askForPermission(Permission permission,
      UserPermissionRequestResponseListener userResponse, Activity activity) {
    if (Dexter.isRequestOngoing()) {
      return;
    }

    PermissionListener[] listeners = createListeners(permission, userResponse, activity);

    Dexter.checkPermission(new CompositePermissionListener(listeners),
                            permission.getAndroidPermissionStringType());

  }

  private PermissionListener[] createListeners(Permission permission,
      UserPermissionRequestResponseListener userResponse, Activity activity) {

    PermissionListener basicListener = getPermissionListenerImpl(permission, userResponse);

    try {
      ViewGroup viewGroup = PermissionsUIViews.getAppContainer(activity);
      PermissionListener listener = getDefaultDeniedPermissionListener(viewGroup, permission);
      return new PermissionListener[]{basicListener, listener};
    }catch (NullContainerException n){
      return new PermissionListener[]{basicListener};
    }
  }

  private PermissionListener getDefaultDeniedPermissionListener(ViewGroup rootView,
      Permission permission) {

    return SnackbarOnDeniedPermissionListener.Builder.with(rootView,
        permission.getPermissionRationaleMessage())
        .withOpenSettingsButton(permission.getPermissionSettingsDeniedFeedback())
        .build();
  }

  private PermissionListener getPermissionListenerImpl(final Permission permission,
      final UserPermissionRequestResponseListener userPermissionRequestResponseListener) {
    return new GenericPermissionListenerImpl(permission, userPermissionRequestResponseListener, contextProvider);
  }

  @Override public void continuePendingPermissionsRequestsIfPossible() {
    Dexter.continuePendingRequestIfPossible(new ContinueRequestPermissionListenerImpl(contextProvider));
  }
}

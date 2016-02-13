package com.applivery.applvsdklib.domain.download.token;

import com.applivery.applvsdklib.AppliverySdk;
import com.applivery.applvsdklib.domain.InteractorCallback;
import com.applivery.applvsdklib.domain.download.app.DownloadBuildInteractor;
import com.applivery.applvsdklib.domain.download.app.DownloadBuildInteractorCallback;
import com.applivery.applvsdklib.network.api.AppliveryApiService;
import com.applivery.applvsdklib.domain.model.BuildTokenInfo;
import com.applivery.applvsdklib.domain.model.ErrorObject;
import com.applivery.applvsdklib.domain.download.permissions.WriteExternalPermission;
import com.applivery.applvsdklib.tools.permissions.UserPermissionRequestResponseListener;
import com.applivery.applvsdklib.ui.views.ShowErrorAlert;
import com.applivery.applvsdklib.ui.views.update.UpdateView;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 10/1/16.
 */
public class ObtainBuildTokenInteractorCallback implements InteractorCallback<BuildTokenInfo> {

  private final AppliveryApiService apiService;
  private final String appName;
  private final UpdateView updateView;

  public ObtainBuildTokenInteractorCallback(AppliveryApiService apiService, String appName,
      UpdateView updateView) {
    this.apiService = apiService;
    this.appName = appName;
    this.updateView = updateView;
  }

  @Override public void onSuccess(final BuildTokenInfo buildTokenInfo) {
    AppliverySdk.getPermissionRequestManager().askForPermission(new WriteExternalPermission(),
        new UserPermissionRequestResponseListener() {
          @Override public void onPermissionAllowed(boolean permissionAllowed) {
            if (permissionAllowed){

              updateView.showDownloadInProgress();

              InteractorCallback interactorCallback = new DownloadBuildInteractorCallback(updateView);
              Runnable r = DownloadBuildInteractor.getInstance(apiService, appName, buildTokenInfo,
                  interactorCallback);

              AppliverySdk.getExecutor().execute(r);
            }else{
              updateView.hideDownloadInProgress();
            }
          }
        }, AppliverySdk.getCurrentActivity());
  }

  @Override public void onError(ErrorObject error) {
    ShowErrorAlert showErrorAlert = new ShowErrorAlert();
    showErrorAlert.showError(error);
  }
}

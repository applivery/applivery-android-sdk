package com.applivery.applvsdklib.domain.download.app;

import com.applivery.applvsdklib.AppliverySdk;
import com.applivery.applvsdklib.domain.BaseInteractor;
import com.applivery.applvsdklib.domain.InteractorCallback;
import com.applivery.applvsdklib.network.api.AppliveryApiService;
import com.applivery.applvsdklib.domain.model.BuildTokenInfo;
import com.applivery.applvsdklib.domain.model.BusinessObject;
import com.applivery.applvsdklib.domain.model.DownloadResult;
import com.applivery.applvsdklib.domain.model.ErrorObject;
import com.applivery.applvsdklib.domain.download.permissions.ReadExternalPermission;
import com.applivery.applvsdklib.network.api.requests.DownloadBuildRequest;
import com.applivery.applvsdklib.network.api.requests.DownloadStatusListener;
import com.applivery.applvsdklib.network.api.requests.ExternalStorageWriter;
import com.applivery.applvsdklib.tools.androidimplementations.AndroidAppInstallerImpl;
import com.applivery.applvsdklib.tools.androidimplementations.AndroidExternalStorageWriterImpl;
import com.applivery.applvsdklib.tools.permissions.PermissionChecker;
import com.applivery.applvsdklib.tools.permissions.UserPermissionRequestResponseListener;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 9/1/16.
 */
public class DownloadBuildInteractor extends BaseInteractor<DownloadResult> {

  private final DownloadBuildRequest downloadBuildRequest;
  private final InteractorCallback<DownloadResult> interactorCallback;
  private final DownloadStatusListener downloadStatusListener;
  private final AppInstaller appInstaller;
  private final PermissionChecker permissionRequestExecutor;

  public DownloadBuildInteractor(AppliveryApiService appliveryApiService, String appName,
      BuildTokenInfo buildTokenInfo, final InteractorCallback interactorCallback,
      ExternalStorageWriter externalStorageWriter) {

    this.downloadStatusListener = new DownloadStatusListener() {
      @Override public void updateDownloadPercentStatus(double percent) {
        //TODO implement new type of callback if necessary to update progress;
      }
    };

    this.downloadBuildRequest =
        new DownloadBuildRequest(appliveryApiService, buildTokenInfo, appName,
            downloadStatusListener, externalStorageWriter);
    this.interactorCallback = interactorCallback;
    this.appInstaller = new AndroidAppInstallerImpl(AppliverySdk.getApplicationContext());
    this.permissionRequestExecutor = AppliverySdk.getPermissionRequestManager();
  }

  @Override protected void receivedResponse(BusinessObject downloadResult) {
    super.receivedResponse(downloadResult, DownloadResult.class);
  }

  @Override protected void error(ErrorObject serverResponse) {
    interactorCallback.onError(serverResponse);
  }

  @Override protected void success(final DownloadResult response) {
    interactorCallback.onSuccess(response);

    permissionRequestExecutor.askForPermission(new ReadExternalPermission(),
        new UserPermissionRequestResponseListener() {
          @Override public void onPermissionAllowed(boolean permissionAllowed) {
            if (permissionAllowed) {
              appInstaller.installApp(response.getPath());
            }
          }
        }, AppliverySdk.getCurrentActivity());
  }

  @Override protected BusinessObject performRequest() {
    return downloadBuildRequest.execute();
  }

  public static Runnable getInstance(AppliveryApiService appliveryApiService, String appName,
      BuildTokenInfo buildTokenInfo, InteractorCallback interactorCallback) {

    ExternalStorageWriter externalStorageWriter = new AndroidExternalStorageWriterImpl();

    DownloadBuildInteractor downloadBuildInteractor =
        new DownloadBuildInteractor(appliveryApiService, appName, buildTokenInfo,
            interactorCallback, externalStorageWriter);

    return downloadBuildInteractor;
  }
}

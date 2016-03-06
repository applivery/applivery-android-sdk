package com.applivery.applvsdklib.domain.download.app;

import com.applivery.applvsdklib.AppliverySdk;
import com.applivery.applvsdklib.domain.BaseInteractor;
import com.applivery.applvsdklib.domain.InteractorCallback;
import com.applivery.applvsdklib.domain.model.BuildTokenInfo;
import com.applivery.applvsdklib.domain.model.BusinessObject;
import com.applivery.applvsdklib.domain.model.DownloadResult;
import com.applivery.applvsdklib.domain.model.ErrorObject;
import com.applivery.applvsdklib.network.api.AppliveryApiService;
import com.applivery.applvsdklib.network.api.requests.DownloadBuildRequest;
import com.applivery.applvsdklib.network.api.requests.DownloadStatusListener;
import com.applivery.applvsdklib.network.api.requests.ExternalStorageWriter;
import com.applivery.applvsdklib.tools.androidimplementations.AndroidAppInstallerImpl;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 9/1/16.
 */
public class DownloadBuildInteractor extends BaseInteractor<DownloadResult> {

  private final DownloadBuildRequest downloadBuildRequest;
  private final InteractorCallback<DownloadResult> interactorCallback;
  private final DownloadStatusListener downloadStatusListener;
  private final AppInstaller appInstaller;

  public DownloadBuildInteractor(AppliveryApiService appliveryApiService, String appName,
      BuildTokenInfo buildTokenInfo, final DownloadBuildInteractorCallback interactorCallback,
      ExternalStorageWriter externalStorageWriter) {

    this.downloadStatusListener = new DownloadStatusListener() {
      @Override public void updateDownloadPercentStatus(double percent) {
        interactorCallback.updateDownloadProgress(percent);
      }

      @Override public void downloadCompleted(DownloadResult downloadResult) {
        sendDelayedResponse(downloadResult);
      }
    };

    this.downloadBuildRequest =
        new DownloadBuildRequest(appliveryApiService, buildTokenInfo, appName,
            downloadStatusListener, externalStorageWriter);
    this.interactorCallback = interactorCallback;
    this.appInstaller = new AndroidAppInstallerImpl(AppliverySdk.getApplicationContext());
  }

  @Override protected void receivedResponse(BusinessObject downloadResult) {
    super.receivedResponse(downloadResult, DownloadResult.class);
  }

  @Override protected void error(ErrorObject serverResponse) {
    interactorCallback.onError(serverResponse);
  }

  @Override protected void success(final DownloadResult response) {
    if (response.isSuccess()) {
      interactorCallback.onSuccess(response);
      appInstaller.installApp(response.getPath());
    }
  }

  @Override protected BusinessObject performRequest() {
    return downloadBuildRequest.execute();
  }

  public static Runnable getInstance(AppliveryApiService appliveryApiService, String appName,
      BuildTokenInfo buildTokenInfo, DownloadBuildInteractorCallback interactorCallback,
      ExternalStorageWriter externalStorageWriter) {

    DownloadBuildInteractor downloadBuildInteractor =
        new DownloadBuildInteractor(appliveryApiService, appName, buildTokenInfo,
            interactorCallback, externalStorageWriter);

    return downloadBuildInteractor;
  }
}

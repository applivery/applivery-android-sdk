package com.applivery.applvsdklib.domain.download.token;

import com.applivery.applvsdklib.AppliverySdk;
import com.applivery.applvsdklib.domain.InteractorCallback;
import com.applivery.applvsdklib.domain.download.app.DownloadBuildInteractor;
import com.applivery.applvsdklib.domain.download.app.DownloadBuildInteractorCallback;
import com.applivery.applvsdklib.domain.model.BuildTokenInfo;
import com.applivery.applvsdklib.domain.model.ErrorObject;
import com.applivery.applvsdklib.network.api.AppliveryApiService;
import com.applivery.applvsdklib.network.api.requests.ExternalStorageWriter;
import com.applivery.applvsdklib.tools.androidimplementations.AndroidExternalStorageWriterImpl;
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
    updateView.showDownloadInProgress();

    DownloadBuildInteractorCallback interactorCallback = new DownloadBuildInteractorCallback(updateView);
    ExternalStorageWriter externalStorageWriter = new AndroidExternalStorageWriterImpl();
    Runnable r =
        DownloadBuildInteractor.getInstance(apiService, appName, buildTokenInfo, interactorCallback,
            externalStorageWriter);

    AppliverySdk.getExecutor().execute(r);
  }

  @Override public void onError(ErrorObject error) {
    ShowErrorAlert showErrorAlert = new ShowErrorAlert();
    showErrorAlert.showError(error);
  }
}

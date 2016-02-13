package com.applivery.applvsdklib.domain.appconfig.update;

import com.applivery.applvsdklib.AppliverySdk;
import com.applivery.applvsdklib.domain.InteractorCallback;
import com.applivery.applvsdklib.domain.download.token.ObtainBuildTokenInteractorCallback;
import com.applivery.applvsdklib.domain.download.token.ObtainAppBuildDownloadTokenInteractor;
import com.applivery.applvsdklib.network.api.AppliveryApiService;
import com.applivery.applvsdklib.domain.model.AppConfig;
import com.applivery.applvsdklib.ui.views.update.UpdateListener;
import com.applivery.applvsdklib.ui.views.update.UpdateView;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 10/1/16.
 */
public class UpdateListenerImpl implements UpdateListener {

  private final AppConfig appConfig;
  private final AppliveryApiService apiService;
  private UpdateView updateView;

  public UpdateListenerImpl(AppConfig appConfig, AppliveryApiService apiService) {
    this.appConfig = appConfig;
    this.apiService = apiService;
  }

  @Override public void onUpdateButtonClick() {
    String buildId = appConfig.getSdk().getAndroid().getLastBuildId();

    InteractorCallback interactorCallback = new ObtainBuildTokenInteractorCallback(
        apiService, appConfig.getName(), updateView);

    Runnable r = ObtainAppBuildDownloadTokenInteractor.getInstance(apiService, buildId,
        interactorCallback);

    AppliverySdk.getExecutor().execute(r);
  }

  @Override public void setUpdateView(UpdateView updateView) {
    this.updateView = updateView;
  }
}

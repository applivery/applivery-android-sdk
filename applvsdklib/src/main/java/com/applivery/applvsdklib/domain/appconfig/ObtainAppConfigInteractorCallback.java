package com.applivery.applvsdklib.domain.appconfig;

import com.applivery.applvsdklib.AppliverySdk;
import com.applivery.applvsdklib.domain.InteractorCallback;
import com.applivery.applvsdklib.domain.appconfig.update.CurrentAppInfo;
import com.applivery.applvsdklib.domain.appconfig.update.UpdateListenerImpl;
import com.applivery.applvsdklib.domain.appconfig.update.UpdateType;
import com.applivery.applvsdklib.network.api.AppliveryApiService;
import com.applivery.applvsdklib.domain.model.Android;
import com.applivery.applvsdklib.domain.model.AppConfig;
import com.applivery.applvsdklib.domain.model.ErrorObject;
import com.applivery.applvsdklib.ui.views.ShowErrorAlert;
import com.applivery.applvsdklib.ui.views.update.UpdateListener;
import com.applivery.applvsdklib.ui.views.update.UpdateViewPresenter;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 10/1/16.
 */
public class ObtainAppConfigInteractorCallback implements InteractorCallback<AppConfig> {

  private final CurrentAppInfo currentAppInfo;
  private final AppliveryApiService appliveryApiService;

  public ObtainAppConfigInteractorCallback(AppliveryApiService appliveryApiService,
      CurrentAppInfo currentAppInfo) {
    this.currentAppInfo = currentAppInfo;
    this.appliveryApiService = appliveryApiService;
  }

  @Override public void onSuccess(AppConfig appConfig) {
    UpdateType updateType = checkForUpdates(appConfig);
    UpdateViewPresenter presenter = new UpdateViewPresenter(getUpdateListener(appConfig));
    showUpdate(presenter, updateType, appConfig);
  }

  @Override public void onError(ErrorObject error) {
    ShowErrorAlert showErrorAlert = new ShowErrorAlert();
    showErrorAlert.showError(error);
  }

  private UpdateType checkForUpdates(AppConfig appConfig) {
    Android android = appConfig.getSdk().getAndroid();

    long minVersion = -1;
    long lastVersion = -1;

    long currentVersion = currentAppInfo.getVersionCode();
    boolean forceUpdate = android.isForceUpdate();
    boolean ota = android.isOta();

    try {
      if (forceUpdate){
        minVersion = Integer.valueOf(android.getMinVersion());
      }
      lastVersion = Integer.valueOf(android.getLastBuildVersion());
    }catch (NumberFormatException n){

    }

    return obtainUpdateType(minVersion, lastVersion, currentVersion, ota, forceUpdate);
  }

  private UpdateType obtainUpdateType(long minVersion, long lastVersion, long currentVersion,
      boolean ota, boolean forceUpdate) {

    UpdateType updateType = UpdateType.NO_UPDATE;

    if (forceUpdate){
      if (minVersion > currentVersion){
        updateType = UpdateType.FORCED_UPDATE;
      }else{
        AppliverySdk.Logger.log("ForceUpdate is true but App version and last uploaded are both same");
      }
    }else if (ota){
      if (lastVersion > currentVersion){
        updateType = UpdateType.SUGGESTED_UPDATE;
      }else{
        AppliverySdk.Logger.log("Ota is true but App version and last uploaded are both same");
      }
    }else{
      AppliverySdk.Logger.log("Not forceUpdate Neither Ota are activated");
    }

    return updateType;
  }

  private void showUpdate(UpdateViewPresenter updateViewPresenter, UpdateType updateType,
      AppConfig appConfig) {
    switch (updateType){
      case FORCED_UPDATE:
        updateViewPresenter.showForcedUpdate(
            appConfig.getName(), appConfig.getSdk().getAndroid().getMustUpdateMsg());
        break;
      case SUGGESTED_UPDATE:
        updateViewPresenter.showSuggestedUpdate(
            appConfig.getName(), appConfig.getSdk().getAndroid().getUpdateMsg());
        break;
      case NO_UPDATE:
      default:
        break;
    }
  }

  private UpdateListener getUpdateListener(AppConfig appConfig) {
    return new UpdateListenerImpl(appConfig, appliveryApiService);
  }

}

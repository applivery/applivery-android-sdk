/*
 * Copyright (c) 2016 Applivery
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.applivery.applvsdklib.domain.appconfig;

import android.util.Log;
import com.applivery.applvsdklib.AppliverySdk;
import com.applivery.applvsdklib.domain.InteractorCallback;
import com.applivery.applvsdklib.domain.appconfig.update.LastConfigWriter;
import com.applivery.applvsdklib.domain.appconfig.update.UpdateListenerImpl;
import com.applivery.applvsdklib.domain.appconfig.update.UpdateType;
import com.applivery.applvsdklib.domain.model.Android;
import com.applivery.applvsdklib.domain.model.AppConfig;
import com.applivery.applvsdklib.domain.model.ErrorObject;
import com.applivery.applvsdklib.domain.model.PackageInfo;
import com.applivery.applvsdklib.network.api.AppliveryApiService;
import com.applivery.applvsdklib.tools.androidimplementations.AndroidLastConfigWriterImpl;
import com.applivery.applvsdklib.tools.injection.Injection;
import com.applivery.applvsdklib.tools.session.SessionManager;
import com.applivery.applvsdklib.ui.views.ShowErrorAlert;
import com.applivery.applvsdklib.ui.views.update.UpdateListener;
import com.applivery.applvsdklib.ui.views.update.UpdateViewPresenter;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 10/1/16.
 */
public class ObtainAppConfigInteractorCallback implements InteractorCallback<AppConfig> {

  private static final String TAG = "ObtainAppConfigICb";
  private final PackageInfo packageInfo;
  private final AppliveryApiService appliveryApiService;
  private final SessionManager sessionManager;
  private final LastConfigWriter lastConfigWriter;

  public ObtainAppConfigInteractorCallback(AppliveryApiService appliveryApiService,
      SessionManager sessionManager, PackageInfo packageInfo) {
    this.packageInfo = packageInfo;
    this.sessionManager = sessionManager;
    this.appliveryApiService = appliveryApiService;
    this.lastConfigWriter = new AndroidLastConfigWriterImpl();
  }

  @Override public void onSuccess(AppConfig appConfig) {
    Injection.INSTANCE.setAppConfig(appConfig);
    UpdateType updateType = checkForUpdates(appConfig);
    lastConfigWriter.writeLastConfigCheckTimeStamp(System.currentTimeMillis());
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

    long currentVersion = packageInfo.getVersion();
    boolean forceUpdate = android.getForceUpdate();
    boolean ota = android.getOta();

    try {
      if (forceUpdate) {
        minVersion = Integer.valueOf(android.getMinVersion());
      }
      lastVersion = Integer.valueOf(android.getLastBuildVersion());
    } catch (NumberFormatException n) {
      Log.e(TAG, "checkForUpdates()");
    }

    UpdateType updateType =
        obtainUpdateType(minVersion, lastVersion, currentVersion, ota, forceUpdate);

    if (updateType == UpdateType.FORCED_UPDATE) {
      AppliverySdk.lockApp();
    } else {
      AppliverySdk.unlockApp();
    }

    return updateType;
  }

  private UpdateType obtainUpdateType(long minVersion, long lastVersion, long currentVersion,
      boolean ota, boolean forceUpdate) {

    UpdateType updateType = UpdateType.NO_UPDATE;

    if (forceUpdate) {
      if (minVersion > currentVersion) {
        updateType = UpdateType.FORCED_UPDATE;
      } else {
        AppliverySdk.Logger.log(
            "ForceUpdate is true but App version and last uploaded are both same");
      }
    } else if (ota) {
      if (lastVersion > currentVersion) {
        updateType = UpdateType.SUGGESTED_UPDATE;
      } else {
        AppliverySdk.Logger.log("Ota is true but App version and last uploaded are both same");
      }
    } else {
      AppliverySdk.Logger.log("Not forceUpdate Neither Ota are activated");
    }

    return updateType;
  }

  private void showUpdate(UpdateViewPresenter updateViewPresenter, UpdateType updateType,
      AppConfig appConfig) {
    switch (updateType) {
      case FORCED_UPDATE:
        //TODO add update msg
        updateViewPresenter.showForcedUpdate(appConfig.getName(), "");
        break;
      case SUGGESTED_UPDATE:
        //TODO add update msg
        updateViewPresenter.showSuggestedUpdate(appConfig.getName(), "TODO");
        break;
      case NO_UPDATE:
      default:
        break;
    }
  }

  private UpdateListener getUpdateListener(AppConfig appConfig) {
    return new UpdateListenerImpl(appConfig, sessionManager, appliveryApiService);
  }
}

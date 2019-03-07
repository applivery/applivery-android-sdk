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

package com.applivery.applvsdklib.domain.appconfig.update;

import com.applivery.applvsdklib.AppliverySdk;
import com.applivery.applvsdklib.domain.InteractorCallback;
import com.applivery.applvsdklib.domain.download.app.AppInstaller;
import com.applivery.applvsdklib.domain.download.app.ExternalStorageReader;
import com.applivery.applvsdklib.domain.download.token.ObtainAppBuildDownloadTokenInteractor;
import com.applivery.applvsdklib.domain.download.token.ObtainBuildTokenInteractorCallback;
import com.applivery.applvsdklib.domain.model.AppConfig;
import com.applivery.applvsdklib.network.api.AppliveryApiService;
import com.applivery.applvsdklib.tools.androidimplementations.AndroidAppInstallerImpl;
import com.applivery.applvsdklib.tools.androidimplementations.AndroidExternalStorageReaderImpl;
import com.applivery.applvsdklib.tools.session.SessionManager;
import com.applivery.applvsdklib.ui.views.login.LoginView;
import com.applivery.applvsdklib.ui.views.update.UpdateListener;
import com.applivery.applvsdklib.ui.views.update.UpdateView;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 10/1/16.
 */
public class UpdateListenerImpl implements UpdateListener {

  private final AppConfig appConfig;
  private final SessionManager sessionManager;
  private final AppliveryApiService apiService;
  private final ExternalStorageReader externalStorageReader;
  private UpdateView updateView;

  public UpdateListenerImpl(AppConfig appConfig, SessionManager sessionManager,
      AppliveryApiService apiService) {
    this.appConfig = appConfig;
    this.sessionManager = sessionManager;
    this.apiService = apiService;
    this.externalStorageReader = new AndroidExternalStorageReaderImpl();
  }

  @Override public void onUpdateButtonClick() {
    if (needLogin(appConfig)) {
      showLogin();
    } else {
      updateApp();
    }
  }

  private void updateApp() {
    String buildId = appConfig.getSdk().getAndroid().getLastBuildId();
    if (appBuildNotDownloaded(appConfig.getName() + "_" + buildId)) {
      InteractorCallback interactorCallback =
          new ObtainBuildTokenInteractorCallback(apiService, appConfig.getName(), updateView);

      Runnable r = ObtainAppBuildDownloadTokenInteractor.getInstance(apiService, buildId,
          interactorCallback);

      AppliverySdk.getExecutor().execute(r);
    } else {
      installApp(appConfig.getName() + "_" + buildId);
    }
  }

  private boolean appBuildNotDownloaded(String apkFileName) {
    return !externalStorageReader.fileExists(apkFileName);
  }

  private Boolean needLogin(AppConfig appConfig) {
    Boolean isAuthUpdate = appConfig.getSdk().getAndroid().getForceAuth();
    return isAuthUpdate && !sessionManager.hasSession();
  }

  private void showLogin() {
    LoginView loginView = new LoginView(AppliverySdk.getCurrentActivity(), new Function0<Unit>() {
      @Override public Unit invoke() {
        updateApp();
        return null;
      }
    });
    loginView.getPresenter().requestLogin();
  }

  private void installApp(String apkFileName) {
    AppInstaller appInstaller = new AndroidAppInstallerImpl(AppliverySdk.getApplicationContext(),
        AppliverySdk.getFileProviderAuthority());
    appInstaller.installApp(apkFileName);
  }

  @Override public void setUpdateView(UpdateView updateView) {
    this.updateView = updateView;
  }
}

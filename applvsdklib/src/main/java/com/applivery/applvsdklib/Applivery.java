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

package com.applivery.applvsdklib;

import android.app.Application;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 17/1/16.
 */
public class Applivery {
  /**
   * Initializes Sdk for the current app and developer. Call this method from your application
   * instance when onCreate method is called. Pay attention to description of isPlayStoreRelease
   * param.
   *
   * @param app your app instance, it can't be null.
   * @param applicationId your app id. You can find this value at applivery dashboard in your
   * app section
   * @param appClientToken your developer secret key. You can find this value at
   * applivery dashboard in developer section
   * @param isStoreRelease this flag MUST be true when application is candidate to be store
   * release, because otherwise app can show dialogs from applivery about updates that
   * can redirect end users to applivery beta versions.
   */
  public static void init(Application app, String applicationId, String appClientToken,
      boolean isStoreRelease) {
    AppliverySdk.sdkInitialize(app, applicationId, appClientToken, isStoreRelease);
  }

  /**
   * Sets if the app should check for updates when comming from background or only should
   * check it when the app is init for first time.
   * default false
   *
   * @param checkForUpdatesBackground Boolean if true the app check for updates when
   * comming from background
   */
  public static void setCheckForUpdatesBackground(Boolean checkForUpdatesBackground) {
    AppliverySdk.setCheckForUpdatesBackground(checkForUpdatesBackground);
  }

  /**
   * @return Boolean Boolean true if the app check for updates when comming from background
   */
  public static Boolean getCheckForUpdatesBackground() {
    return AppliverySdk.getCheckForUpdatesBackground();
  }

  /**
   * Manually check for App updates on Applivery.
   */
  public static void checkForUpdates() {
    AppliverySdk.obtainAppConfigForCheckUpdates();
  }

  /**
   * Enables feedback on shake, call it having your app in foreground whenever you want.
   */
  public static void enableShakeFeedback() {
    AppliverySdk.enableShakeFeedback();
  }

  /**
   * Disables feedback on shake, call it having your app in foreground whenever you want.
   */
  public static void disableShakeFeedback() {
    AppliverySdk.disableShakeFeedback();
  }

  /**
   * Enables feedback on screenshot capture, call it having your app in foreground whenever you
   * want.
   */
  public static void enableScreenshotFeedback() {
    AppliverySdk.enableScreenshotFeedback();
  }

  /**
   * Disables feedback on screenshot capture, call it having your app in foreground whenever you
   * want.
   */
  public static void disableScreenshotFeedback() {
    AppliverySdk.disableScreenshotFeedback();
  }
}

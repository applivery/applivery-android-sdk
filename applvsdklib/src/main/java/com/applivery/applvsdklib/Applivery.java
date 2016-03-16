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
   * @param isPlayStoreRelease this flag MUST be null when application is candidate to be Google
   * Play Store release, because otherwise app can show dialogs from applivery about updates that
   * can redirect end users to applivery beta versions.
   */
  public static void init(Application app,
      String applicationId, String appClientToken, boolean isPlayStoreRelease) {
    AppliverySdk.sdkInitialize(app, applicationId, appClientToken, isPlayStoreRelease);
  }

  /**
   * Sets the time to wait between one check for update request and another. If this time has
   * been rebased since last checking, when app comes from background will request again to
   * applivery server for updates.
   *
   * @param seconds int with seconds behind checking for new versions will be executed
   * comming from background
   */
  public static void setUpdateCheckingInterval(int seconds){
    AppliverySdk.setUpdateCheckingTime(seconds);
  }
}

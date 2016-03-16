package com.applivery.applvsdklib.domain.appconfig.update;

import com.applivery.applvsdklib.AppliverySdk;
import java.util.Calendar;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 11/1/16.
 */
public class AppConfigChecker {

  private final LastConfigReader lastConfigReader;

  public AppConfigChecker(LastConfigReader lastConfigReader) {
    this.lastConfigReader = lastConfigReader;
  }

  public boolean shouldCheckAppConfigForUpdate() {

    if (AppliverySdk.isSdkRestarted()){
      AppliverySdk.setSdkRestartedFalse();
      return true;
    }

    if (lastConfigReader.notExistsLastConfig()){
      return true;
    }

    long timeStamp = lastConfigReader.readLastConfigCheckTimeStamp();
    long diff = System.currentTimeMillis() - timeStamp;

    if (diff >= AppliverySdk.getUpdateCheckingTime()) {
      return true;
    } else {
      return false;
    }
  }
}

package com.applivery.applvsdklib.domain.appconfig.update;

import com.applivery.applvsdklib.AppliverySdk;
import java.util.Calendar;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 11/1/16.
 */
public class AppConfigChecker {

  private static final long ONE_DAY = 86400000l;
  private static final long TEN_SECONDS = 10000l;

  private final LastConfigReader lastConfigReader;

  public AppConfigChecker(LastConfigReader lastConfigReader) {
    this.lastConfigReader = lastConfigReader;
  }

  public boolean shouldCheckAppConfigForUpdate() {

    //if not exists last config request TimeStamp, app config is requested automatically
    if (lastConfigReader.notExistsLastConfig() || AppliverySdk.isStoreRelease()) {
      return false;
    }

    long timeStamp = lastConfigReader.readLastConfigCheckTimeStamp();
    long diff = Calendar.getInstance().get(Calendar.MILLISECOND) - timeStamp;

    //if (diff > ONE_DAY) {
    if (diff > TEN_SECONDS) {
      return true;
    } else {
      return false;
    }
  }
}

package com.applivery.applvsdklib.domain.appconfig.update;

import com.applivery.applvsdklib.AppliverySdk;
import java.util.Calendar;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 11/1/16.
 */
public class AppConfigChecker {

  private static final long ONE_DAY = 86400000l;

  private final LastConfigReader lastConfigReader;

  public AppConfigChecker(LastConfigReader lastConfigReader) {
    this.lastConfigReader = lastConfigReader;
  }

  public boolean shouldCheckAppConfigForUpdate(){

    if (!lastConfigReader.existLastConfig() || AppliverySdk.isStoreRelease()){
      return false;
    }

    long timeStamp = lastConfigReader.readLastConfigCheckTimeStamp();
    long diff = Calendar.getInstance().get(Calendar.MILLISECOND) - timeStamp;

    if (diff>ONE_DAY){
      return true;
    }else{
      return false;
    }
  }
}

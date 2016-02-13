package com.applivery.applvsdklib.domain.appconfig.update;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 11/1/16.
 */
public interface LastConfigReader {
  long readLastConfigCheckTimeStamp();
  boolean existLastConfig();
}

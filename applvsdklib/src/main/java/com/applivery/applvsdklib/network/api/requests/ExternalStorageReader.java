package com.applivery.applvsdklib.network.api.requests;

import com.applivery.applvsdklib.tools.androidimplementations.AppPathReceiver;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 9/1/16.
 */
public interface ExternalStorageReader {
  boolean fileExists(String apkFileName);
  void getPath(String apkFileName, AppPathReceiver appPathReceiver);
}

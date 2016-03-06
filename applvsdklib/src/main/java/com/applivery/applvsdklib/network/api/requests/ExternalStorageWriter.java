package com.applivery.applvsdklib.network.api.requests;

import java.io.InputStream;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 9/1/16.
 */
public interface ExternalStorageWriter {

  String writeToFile(InputStream inputStream, int lenght,
      DownloadStatusListener downloadStatusListener, String apkFileName);
}

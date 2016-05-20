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

package com.applivery.applvsdklib.tools.androidimplementations;

import android.os.Environment;
import com.applivery.applvsdklib.AppliverySdk;
import com.applivery.applvsdklib.domain.download.permissions.WriteExternalPermission;
import com.applivery.applvsdklib.domain.model.DownloadResult;
import com.applivery.applvsdklib.domain.download.app.DownloadStatusListener;
import com.applivery.applvsdklib.domain.download.app.ExternalStorageWriter;
import com.applivery.applvsdklib.tools.permissions.PermissionChecker;
import com.applivery.applvsdklib.tools.permissions.UserPermissionRequestResponseListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 9/1/16.
 */
public class AndroidExternalStorageWriterImpl implements ExternalStorageWriter {

  private final static int BYTE_LENGHT = 1024;
  private final PermissionChecker permissionRequestExecutor;
  private final WriteExternalPermission writeExternalPermission;

  public AndroidExternalStorageWriterImpl() {
    this.permissionRequestExecutor = AppliverySdk.getPermissionRequestManager();
    writeExternalPermission = new WriteExternalPermission();
  }

  public String writeToFile(InputStream inputStream, int lenght,
      DownloadStatusListener downloadStatusListener, String apkFileName) {

    if (permissionRequestExecutor.isGranted(writeExternalPermission)) {
      return write(inputStream, lenght, downloadStatusListener, apkFileName, false);
    } else {
      askForPermission(inputStream, lenght, downloadStatusListener, apkFileName);
      return null;
    }
  }

  private String write(InputStream inputStream, int lenght,
      DownloadStatusListener downloadStatusListener, String apkFileName, boolean async) {

    boolean result = false;
    String apkPath = null;

    try {
      apkPath = getExternalStoragePath() + "/" + apkFileName + ".apk";
      File f = new File(apkPath);
      OutputStream apk = new FileOutputStream(f);
      byte[] buf = new byte[BYTE_LENGHT];
      int len;
      int read = 0;

      while ((len = inputStream.read(buf)) > 0) {
        read = read + len;
        apk.write(buf, 0, len);
        if (downloadStatusListener != null) {
          downloadStatusListener.updateDownloadPercentStatus(
              (Integer.valueOf(read).doubleValue() / Integer.valueOf(lenght).doubleValue())*100);
        }
      }

      inputStream.close();
      apk.close();

      result = true;
    } catch (Exception e) {
      AppliverySdk.Logger.log(e.getMessage());
    } finally {
      if (async) {
        DownloadResult downloadResult = new DownloadResult(result, apkFileName);
        downloadStatusListener.downloadCompleted(downloadResult);
      }
    }

    return apkFileName;
  }

  private void askForPermission(final InputStream inputStream, final int lenght,
      final DownloadStatusListener downloadStatusListener, final String apkFileName) {

    permissionRequestExecutor.askForPermission(writeExternalPermission,
        new UserPermissionRequestResponseListener() {
          @Override public void onPermissionAllowed(boolean permissionAllowed) {
            if (permissionAllowed) {
              new Thread(new Runnable() {
                @Override public void run() {
                  write(inputStream, lenght, downloadStatusListener, apkFileName, true);
                }
              }).start();
            }else{
              downloadStatusListener.downloadNotStartedPermissionDenied();
            }
          }
        }, AppliverySdk.getCurrentActivity());
  }

  private String getExternalStoragePath() {
    File f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    return f.getAbsolutePath();
  }
}

package com.applivery.applvsdklib.tools.androidimplementations;

import android.os.Environment;
import com.applivery.applvsdklib.network.api.requests.DownloadStatusListener;
import com.applivery.applvsdklib.network.api.requests.ExternalStorageWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 9/1/16.
 */
public class AndroidExternalStorageWriterImpl implements ExternalStorageWriter {

  private final static int BYTE_LENGHT = 1024;

  public String writeToFile(InputStream inputStream, int lenght,
      DownloadStatusListener downloadStatusListener, String appName) throws IOException {

    OutputStream apk = null;
    String apkPath;

    try{
      apkPath = getExternalStoragePath() + "/" + appName + ".apk";
      File f = new File(apkPath);
      apk = new FileOutputStream(f);
      byte[] buf = new byte[BYTE_LENGHT];//Actualizado me olvide del 1024
      int len;
      int read = 0;

      while(( len = inputStream.read(buf)) > 0){
        read = read + len;
        apk.write(buf, 0, len);
        if(downloadStatusListener!=null){
          downloadStatusListener.updateDownloadPercentStatus(read/lenght);
        }
      }

    }catch(IOException e){
      throw new IOException(e);
    }finally {
      inputStream.close();
      apk.close();
    }
    return apkPath;
  }

  public String getExternalStoragePath() {
    File f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    return f.getAbsolutePath();
  }
}

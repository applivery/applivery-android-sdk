package com.applivery.applvsdklib.tools.androidimplementations;

import android.content.Context;
import com.applivery.applvsdklib.AppliverySdk;
import com.applivery.applvsdklib.domain.appconfig.update.LastConfigReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 11/1/16.
 */
public class AndroidLastConfigReaderImpl implements LastConfigReader {

  @Override public long readLastConfigCheckTimeStamp() {
    Context context = AppliverySdk.getApplicationContext();
    FileInputStream fis = null;

    try {
      fis = context.openFileInput(AndroidLastConfigWriterImpl.TIME_STAMP_FILE_NAME);
      InputStreamReader isr = new InputStreamReader(fis);
      BufferedReader bufferedReader = new BufferedReader(isr);
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        sb.append(line);
      }
      fis.close();
      return Long.valueOf(sb.toString());
    } catch (FileNotFoundException e) {
      Date date = new Date();
      return date.getTime();
    }catch (IOException e) {
      Date date = new Date();
      return date.getTime();
    }
  }

  @Override public boolean existLastConfig() {
    File file = getFile();
    if (file != null){
      return file.exists();
    }else{
      return false;
    }
  }

  private File getFile(){
    Context context = AppliverySdk.getApplicationContext();
    return new File(context.getFilesDir() + "/" + AndroidLastConfigWriterImpl.TIME_STAMP_FILE_NAME);
  }
}

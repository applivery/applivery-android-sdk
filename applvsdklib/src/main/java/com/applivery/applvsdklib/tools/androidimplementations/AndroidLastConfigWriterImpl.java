package com.applivery.applvsdklib.tools.androidimplementations;

import android.content.Context;
import com.applivery.applvsdklib.AppliverySdk;
import com.applivery.applvsdklib.domain.appconfig.update.LastConfigWriter;
import java.io.FileOutputStream;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 11/1/16.
 */
public class AndroidLastConfigWriterImpl implements LastConfigWriter {

  public final static String TIME_STAMP_FILE_NAME = "lastConfigTimeStamp";

  @Override public boolean writeLastConfigCheckTimeStamp(long timeStamp) {
    Context context = AppliverySdk.getApplicationContext();
    FileOutputStream outputStream;
    try {
      outputStream = context.openFileOutput(TIME_STAMP_FILE_NAME, Context.MODE_PRIVATE);
      outputStream.write(String.valueOf(timeStamp).getBytes());
      outputStream.close();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

  }
}

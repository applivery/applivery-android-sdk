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

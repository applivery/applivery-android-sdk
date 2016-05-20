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
    } catch (IOException e) {
      Date date = new Date();
      return date.getTime();
    }
  }

  @Override public boolean notExistsLastConfig() {
    File file = getFile();

    if (file == null) {
      return true;
    } else {
      return !file.exists();
    }
  }

  private File getFile() {
    Context context = AppliverySdk.getApplicationContext();
    return new File(context.getFilesDir() + "/" + AndroidLastConfigWriterImpl.TIME_STAMP_FILE_NAME);
  }
}

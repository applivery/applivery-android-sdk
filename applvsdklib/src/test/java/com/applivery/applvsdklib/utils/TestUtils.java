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

package com.applivery.applvsdklib.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 25/12/15.
 */
public class TestUtils {

  private static File getFileFromPath(Object obj, String fileName) {
    ClassLoader classLoader = obj.getClass().getClassLoader();
    URL resource = classLoader.getResource(fileName);
    return new File(resource.getPath());
  }

  public static String getContentFromFile(String fileName, Object context) throws Exception {
    File file = getFileFromPath(context, fileName);
    FileInputStream fin = new FileInputStream(file);
    String response = convertStreamToString(fin);
    fin.close();
    return response;
  }

  public static String convertStreamToString(InputStream is) throws Exception {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    StringBuilder sb = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      sb.append(line).append("\n");
    }
    reader.close();
    return sb.toString();
  }

}

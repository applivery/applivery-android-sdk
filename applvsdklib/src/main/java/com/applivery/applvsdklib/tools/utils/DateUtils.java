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

package com.applivery.applvsdklib.tools.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

  public static Date stringToDateWithFormat(String stringToConvert, String dateFormat) {
    if (stringToConvert != null) {
      try {
        DateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Date date = simpleDateFormat.parse(stringToConvert);
        return date;
      } catch (ParseException e) {
      }
    }
    return new Date(0);
  }

  public static String dateToStringWithFormat(Date dateToString, String dateFormat) {
    if (dateToString != null) {
      DateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
      return simpleDateFormat.format(dateToString);
    } else {
      return null;
    }
  }
}

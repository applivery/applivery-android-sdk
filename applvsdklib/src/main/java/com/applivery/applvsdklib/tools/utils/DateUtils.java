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

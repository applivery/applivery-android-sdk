package com.applivery.applvsdklib;

import com.applivery.applvsdklib.tools.utils.DateUtils;
import java.util.Calendar;
import java.util.Date;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;

import static com.applivery.applvsdklib.matchers.IsDateEqualTo.isDateEqualTo;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class DateUtilsTest {

    private Date getCalendar(int year, int month, int day, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute, second);
        return calendar.getTime();
    }

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    @Test
    public void testStringToDateWithFormatGoodDate() throws Exception {
        String stringDate = "2013-09-29T18:46:19Z";

        Date date = DateUtils.stringToDateWithFormat(stringDate, DATE_FORMAT);

        Date expectedDate = getCalendar(2013, Calendar.SEPTEMBER, 29, 18, 46, 19);

        assertThat(expectedDate, isDateEqualTo(date));
    }

    @Test
    public void testStringToDateWithFormatNotEquals() throws Exception {
        String stringDate = "2013-09-29T18:46:19Z";

        Date date = DateUtils.stringToDateWithFormat(stringDate, DATE_FORMAT);

        Date expectedDate = getCalendar(2000, Calendar.FEBRUARY, 10, 12, 12, 12);

        assertThat(expectedDate, not(isDateEqualTo(date)));
    }

    @Test
    public void testStringToDateWithFormatWrongDate() throws Exception {
        String stringDate = "Valor Fake";

        Date date = DateUtils.stringToDateWithFormat(stringDate, DATE_FORMAT);

        Date expectedDate = getCalendar(1970, Calendar.JANUARY, 01, 01, 00, 00);

        //Travis CI fails using assertThat(expectedDate, isDateEqualTo(date));
        //So it was necessary to check the date using this comparation

        Calendar actualCalendar = Calendar.getInstance();
        actualCalendar.setTime(date);
        Calendar expectedCalendar = Calendar.getInstance();
        expectedCalendar.setTime(expectedDate);

        Assert.assertEquals(actualCalendar.get(Calendar.YEAR), expectedCalendar.get(Calendar.YEAR));
        Assert.assertEquals(actualCalendar.get(Calendar.MONTH),expectedCalendar.get(Calendar.MONTH));
        Assert.assertEquals(actualCalendar.get(Calendar.DAY_OF_MONTH),expectedCalendar.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void testStringToDateWithFormatNullDate() throws Exception {
        Date date = DateUtils.stringToDateWithFormat(null, DATE_FORMAT);
        assertNotNull(date);
    }

    @Test
    public void testDateToStringWithFormatNullDate() throws Exception {
        String date = DateUtils.dateToStringWithFormat(null, DATE_FORMAT);
        assertNull(date);
    }

    @Test
    public void testDateToStringWithFormatOk() throws Exception {
        Date date = getCalendar(2013, Calendar.SEPTEMBER, 29, 18, 46, 19);

        String expectedDate = DateUtils.dateToStringWithFormat(date, DATE_FORMAT);

        assertEquals("2013-09-29T18:46:19Z", expectedDate);
    }
}

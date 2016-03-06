package com.applivery.applvsdklib;

import com.applivery.applvsdklib.tools.utils.DateUtils;
import java.util.Calendar;
import java.util.Date;
import org.junit.Ignore;
import org.junit.Test;

import static com.applivery.applvsdklib.matchers.IsDateEqualTo.isDateEqualTo;
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

    @Test(expected = Exception.class)
    public void testStringToDateWithFormatWrongDate() throws Exception {
        String stringDate = "Valor Fake";

        Date date = DateUtils.stringToDateWithFormat(stringDate, DATE_FORMAT);

        Date expectedDate = getCalendar(1970, Calendar.JANUARY, 01, 01, 00, 00);

        assertThat(expectedDate, isDateEqualTo(date));
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

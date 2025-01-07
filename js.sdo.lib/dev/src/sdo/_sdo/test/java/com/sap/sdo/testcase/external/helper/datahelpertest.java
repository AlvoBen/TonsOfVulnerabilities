package com.sap.sdo.testcase.external.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.external.Conversions;
import com.sap.sdo.testcase.typefac.SimpleIntf1;

import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.DataHelper;

public class DataHelperTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public DataHelperTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }


    /**
     *
     */
    private static final String DATE_STR = "1999-05-31T13:20:00.000Z";
    private static final String DURATION_STR = "P10742DT13H20M0.000S";
    private static final String DURATION_STR2 = "P29Y4M30DT13H20M0.000S";
    private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.'SSS'Z'");
    private DataHelper _dataHelper;

    @Before
    public void setUp() throws Exception {
        _dataHelper = _helperContext.getDataHelper();
    }

    /*
     * Test method for 'com.sap.sdo.impl.data.DataHelperImpl.toDate(String)'
     */
    @Test
    public void testToDate() throws ParseException {
        Date date1 = new Date();

        DF.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateString = DF.format(date1);
        Date date2 = _dataHelper.toDate(dateString);
        assertEquals(dateString, date1, date2);
        assertEquals(null, _dataHelper.toDate(null));

        Date date3 = _dataHelper.toDate(DATE_STR);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTime(date3);
        assertEquals(1999, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.MAY, calendar.get(Calendar.MONTH));
        assertEquals(31, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(13, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(20, calendar.get(Calendar.MINUTE));
        assertEquals(0, calendar.get(Calendar.SECOND));
        assertEquals(0, calendar.get(Calendar.MILLISECOND));

        Date date4 = _dataHelper.toDate(DURATION_STR);
        Calendar calendar2 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Calendar currenCal = (Calendar)calendar2.clone();
        currenCal.add(Calendar.DAY_OF_MONTH, 10742);
        currenCal.add(Calendar.HOUR_OF_DAY, 13);
        currenCal.add(Calendar.MINUTE, 20);
        calendar2.setTime(date4);
        // test difference is lower than 1 second, milliseconds are taken from current time
        assertTrue(
            "expect" + currenCal.getTime() + " but is " + calendar2.getTime(),
            (calendar2.getTimeInMillis() - currenCal.getTimeInMillis()) < 1000);

        Date date5 = _dataHelper.toDate("--01");
        Calendar calendar3 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar3.setTime(date5);
        assertEquals(Calendar.JANUARY, calendar3.get(Calendar.MONTH));
    }

    /*
     * Test method for 'com.sap.sdo.impl.data.DataHelperImpl.toCalendar(String)'
     */
    @Test
    public void testToCalendarString() throws ParseException {
        Calendar calendar = _dataHelper.toCalendar(DATE_STR);
        assertEquals(0, calendar.get(Calendar.ZONE_OFFSET));
        assertEquals(1999, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.MAY, calendar.get(Calendar.MONTH));
        assertEquals(31, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(13, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(20, calendar.get(Calendar.MINUTE));
        assertEquals(0, calendar.get(Calendar.SECOND));
        assertEquals(0, calendar.get(Calendar.MILLISECOND));

        Calendar currenCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        currenCal.add(Calendar.DAY_OF_MONTH, 10742);
        currenCal.add(Calendar.HOUR_OF_DAY, 13);
        currenCal.add(Calendar.MINUTE, 20);
        Calendar calendar2 = _dataHelper.toCalendar(DURATION_STR);
        assertEquals(0, calendar2.get(Calendar.ZONE_OFFSET));
        // test difference is lower than 1 second, milliseconds are taken from current time
        assertTrue(
            "expect" + currenCal.getTime() + " but is " + calendar2.getTime(),
            (calendar2.getTimeInMillis() - currenCal.getTimeInMillis()) < 1000);
    }

    /*
     * Test method for 'com.sap.sdo.impl.data.DataHelperImpl.toCalendar(String, Locale)'
     */
    @Test
    public void testToCalendarStringLocale() {
        Calendar calendar = _dataHelper.toCalendar(DATE_STR, Locale.US);
        assertEquals(0, calendar.get(Calendar.ZONE_OFFSET));
        assertEquals(1999, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.MAY, calendar.get(Calendar.MONTH));
        assertEquals(31, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(13, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(20, calendar.get(Calendar.MINUTE));
        assertEquals(0, calendar.get(Calendar.SECOND));
        assertEquals(0, calendar.get(Calendar.MILLISECOND));
        assertEquals(1, calendar.getFirstDayOfWeek());

        Calendar currenCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        currenCal.add(Calendar.DAY_OF_MONTH, 10742);
        currenCal.add(Calendar.HOUR_OF_DAY, 13);
        currenCal.add(Calendar.MINUTE, 20);
        Calendar calendar2 = _dataHelper.toCalendar(DURATION_STR, Locale.US);
        assertEquals(0, calendar2.get(Calendar.ZONE_OFFSET));
        // test difference is lower than 1 second, milliseconds are taken from current time
        assertTrue(
            "expect" + currenCal.getTime() + " but is " + calendar2.getTime(),
            (calendar2.getTimeInMillis() - currenCal.getTimeInMillis()) < 1000);
    }

    /*
     * Test method for 'com.sap.sdo.impl.data.DataHelperImpl.toDateTime(Date)'
     */
    @Test
    public void testToDateTimeDate() throws Exception {
        String dateTime = _dataHelper.toDateTime(DF.parse(DATE_STR));
        assertEquals(DATE_STR, dateTime);
    }

    /*
     * Test method for 'com.sap.sdo.impl.data.DataHelperImpl.toDuration(Date)'
     */
    @Test
    public void testToDurationDate() throws Exception {
        String duration = _dataHelper.toDuration(DF.parse(DATE_STR));
        if (duration == null || duration.length()<2 || duration.charAt(1)=='1') {
            assertEquals(DURATION_STR, duration);
        } else {
            assertEquals(DURATION_STR2, duration);
        }
    }

    /*
     * Test method for 'com.sap.sdo.impl.data.DataHelperImpl.toTime(Date)'
     */
    @Test
    public void testToTimeDate() throws Exception {
        String time = _dataHelper.toTime(DF.parse(DATE_STR));
        assertEquals(DATE_STR.substring(11), time);
    }

    /*
     * Test method for 'com.sap.sdo.impl.data.DataHelperImpl.toDay(Date)'
     */
    @Test
    public void testToDayDate() throws Exception {
        String day = _dataHelper.toDay(DF.parse(DATE_STR));
        assertEquals("---" + DATE_STR.substring(8, 10), day);
    }

    /*
     * Test method for 'com.sap.sdo.impl.data.DataHelperImpl.toMonth(Date)'
     */
    @Test
    public void testToMonthDate() throws Exception {
        String month = _dataHelper.toMonth(DF.parse(DATE_STR));
        assertEquals("--" + DATE_STR.substring(5, 7), month);
    }

    /*
     * Test method for 'com.sap.sdo.impl.data.DataHelperImpl.toMonthDay(Date)'
     */
    @Test
    public void testToMonthDayDate() throws Exception {
        String monthDay = _dataHelper.toMonthDay(DF.parse(DATE_STR));
        assertEquals("--" + DATE_STR.substring(5, 10), monthDay);
    }

    /*
     * Test method for 'com.sap.sdo.impl.data.DataHelperImpl.toYear(Date)'
     */
    @Test
    public void testToYearDate() throws Exception {
        String year = _dataHelper.toYear(DF.parse(DATE_STR));
        assertEquals(DATE_STR.substring(0, 4), year);
    }

    /*
     * Test method for 'com.sap.sdo.impl.data.DataHelperImpl.toYearMonth(Date)'
     */
    @Test
    public void testToYearMonthDate() throws Exception {
        String yearMonth = _dataHelper.toYearMonth(DF.parse(DATE_STR));
        assertEquals(DATE_STR.substring(0, 7), yearMonth);
    }

    /*
     * Test method for 'com.sap.sdo.impl.data.DataHelperImpl.toYearMonthDay(Date)'
     */
    @Test
    public void testToYearMonthDayDate() throws Exception {
        String yearMonthDay = _dataHelper.toYearMonthDay(DF.parse(DATE_STR));
        assertEquals(DATE_STR.substring(0, 10), yearMonthDay);
    }

    /*
     * Test method for 'com.sap.sdo.impl.data.DataHelperImpl.toDateTime(Calendar)'
     */
    @Test
    public void testToDateTimeCalendar() throws Exception {
        String dateTime = _dataHelper.toDateTime(_dataHelper.toCalendar(DATE_STR));
        assertEquals(DATE_STR, dateTime);
    }

    /*
     * Test method for 'com.sap.sdo.impl.data.DataHelperImpl.toDuration(Calendar)'
     */
    @Test
    public void testToDurationCalendar() throws Exception {
        String duration = _dataHelper.toDuration(_dataHelper.toCalendar(DATE_STR));
        if (duration == null || duration.length()<2 || duration.charAt(1)=='1') {
            assertEquals(DURATION_STR, duration);
        } else {
            assertEquals(DURATION_STR2, duration);
        }
    }

    /*
     * Test method for 'com.sap.sdo.impl.data.DataHelperImpl.toTime(Calendar)'
     */
    @Test
    public void testToTimeCalendar() throws Exception {
        String time = _dataHelper.toTime(_dataHelper.toCalendar(DATE_STR));
        assertEquals(DATE_STR.substring(11), time);
    }

    /*
     * Test method for 'com.sap.sdo.impl.data.DataHelperImpl.toDay(Calendar)'
     */
    @Test
    public void testToDayCalendar() throws Exception {
        String day = _dataHelper.toDay(_dataHelper.toCalendar(DATE_STR));
        assertEquals("---" + DATE_STR.substring(8, 10) + 'Z', day);
    }

    /*
     * Test method for 'com.sap.sdo.impl.data.DataHelperImpl.toMonth(Calendar)'
     */
    @Test
    public void testToMonthCalendar() throws Exception {
        String month = _dataHelper.toMonth(_dataHelper.toCalendar(DATE_STR));
        assertEquals("--" + DATE_STR.substring(5, 7) + 'Z', month);
    }

    /*
     * Test method for 'com.sap.sdo.impl.data.DataHelperImpl.toMonthDay(Calendar)'
     */
    @Test
    public void testToMonthDayCalendar() throws Exception {
        String monthDay = _dataHelper.toMonthDay(_dataHelper.toCalendar(DATE_STR));
        assertEquals("--" + DATE_STR.substring(5, 10) + 'Z', monthDay);
    }

    /*
     * Test method for 'com.sap.sdo.impl.data.DataHelperImpl.toYear(Calendar)'
     */
    @Test
    public void testToYearCalendar() throws Exception {
        String year = _dataHelper.toYear(_dataHelper.toCalendar(DATE_STR));
        assertEquals(DATE_STR.substring(0, 4) + 'Z', year);
    }

    /*
     * Test method for 'com.sap.sdo.impl.data.DataHelperImpl.toYearMonth(Calendar)'
     */
    @Test
    public void testToYearMonthCalendar() throws Exception {
        String yearMonth = _dataHelper.toYearMonth(_dataHelper.toCalendar(DATE_STR));
        assertEquals(DATE_STR.substring(0, 7) + 'Z', yearMonth);
    }

    /*
     * Test method for 'com.sap.sdo.impl.data.DataHelperImpl.toYearMonthDay(Calendar)'
     */
    @Test
    public void testToYearMonthDayCalendar() throws Exception {
        String yearMonthDay = _dataHelper.toYearMonthDay(_dataHelper.toCalendar(DATE_STR));
        assertEquals(DATE_STR.substring(0, 10) + 'Z', yearMonthDay);
    }

    @Test
    public void testConvertByType() {
        Type intType = _helperContext.getTypeHelper().getType(Integer.class);
        Integer intValue = (Integer)_dataHelper.convert(intType, "18");

        assertEquals(18, intValue.intValue());
    }

    @Test
    public void testConvertByProperty() {
        Type type = _helperContext.getTypeHelper().getType(SimpleIntf1.class);
        Property property = type.getProperty("green");
        Boolean boolValue = (Boolean)_dataHelper.convert(property, "true");

        assertEquals(true, boolValue.booleanValue());
    }

    @Test
    public void testConvertBoolean() throws Exception {
        for (Conversions conversion : Conversions.values()) {
            convert(conversion, true, boolean.class, 0);
        }
    }

    @Test
    public void testConvertByte() throws Exception {
        for (Conversions conversion : Conversions.values()) {
            convert(conversion, (byte)42, byte.class, 1);
        }
    }

    @Test
    public void testConvertCharacter() throws Exception {
        for (Conversions conversion : Conversions.values()) {
            convert(conversion, 'a', char.class, 2);
        }
    }

    @Test
    public void testConvertDouble() throws Exception {
        for (Conversions conversion : Conversions.values()) {
            convert(conversion, 42.0, double.class, 3);
        }
    }

    @Test
    public void testConvertFloat() throws Exception {
        for (Conversions conversion : Conversions.values()) {
            convert(conversion, 42.0F, float.class, 4);
        }
    }

    @Test
    public void testConvertInt() throws Exception {
        for (Conversions conversion : Conversions.values()) {
            convert(conversion, 42, int.class, 5);
        }
    }

    @Test
    public void testConvertLong() throws Exception {
        for (Conversions conversion : Conversions.values()) {
            convert(conversion, 42L, long.class, 6);
        }
    }

    @Test
    public void testConvertShort() throws Exception {
        for (Conversions conversion : Conversions.values()) {
            convert(conversion, (short)42, short.class, 7);
        }
    }

    @Test
    public void testConvertString() throws Exception {
        for (Conversions conversion : Conversions.values()) {
            String value;
            switch (conversion) {
                case Date:
                case DateTime:
                case Day:
                case Month:
                case MonthDay:
                case Time:
                case Year:
                case YearMonth:
                case YearMonthDay:
                    value = "2006-11-13T15:17:32";
                    break;
                case Duration:
                    value = "PT15S";
                    break;
                default:
                    value = "1";
                    break;
            }
            convert(conversion, value, String.class, 8);
        }
    }

    @Test
    public void testConvertBytes() throws Exception {
        for (Conversions conversion : Conversions.values()) {
            convert(conversion, new byte[]{1,2,3}, byte[].class, 9);
        }
    }

    @Test
    public void testConvertDecimal() throws Exception {
        for (Conversions conversion : Conversions.values()) {
            convert(conversion, new BigDecimal("42"), BigDecimal.class, 10);
        }
    }

    @Test
    public void testConvertInteger() throws Exception {
        for (Conversions conversion : Conversions.values()) {
            convert(conversion, new BigInteger("42"), BigInteger.class, 11);
        }
    }

    @Test
    public void testConvertDate() throws Exception {
        for (Conversions conversion : Conversions.values()) {
            convert(conversion, new Date(), Date.class, 12);
        }
    }

    @Test
    public void testConvertStrings() throws Exception {
        for (Conversions conversion : Conversions.values()) {
            convert(conversion, Arrays.asList(new String[]{"a","b","c"}), List.class, 18);
        }
    }


    /**
     * @param conversion
     * @param input
     * @throws Exception
     */
    private <T> void convert(Conversions conversion, T input, Class<T> inputClass, int index) throws Exception {
        try {
            Type type = _helperContext.getTypeHelper().getType("commonj.sdo", conversion.name());
            Object value = _helperContext.getDataHelper().convert(type, input);
            assertNotNull("Conversion from " + inputClass.getName() + "to " + conversion.name() + " returned null");
            if (conversion.possibleConverstions()[index] == 2) {
                assertEquals("Identity conversion failed", input, value);
            }
        } catch (ClassCastException ex) {
            assertTrue(
                "Could not convert "  + inputClass.getName() + " to " + conversion.name(),
                conversion.possibleConverstions()[index] == 0);
        } catch (Exception ex) {
            throw new Exception("Exception during convert " + inputClass.getName() + " to " + conversion.name(), ex);
        }
    }
}

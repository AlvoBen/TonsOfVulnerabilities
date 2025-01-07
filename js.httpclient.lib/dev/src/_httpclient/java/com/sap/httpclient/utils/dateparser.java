/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.httpclient.utils;

import com.sap.httpclient.exception.DateParseException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Used for parsing and formatting HTTP dates. This class handles dates as defined by RFC 2616.
 *
 * @author Nikolai Neichev
 */
public class DateParser {

  /**
   * Date format pattern used to parse HTTP date headers in RFC 1123 format.
   */
  public static final String DATE_PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";

  /**
   * Date format pattern used to parse HTTP date headers in RFC 1036 format.
   */
  public static final String DATE_PATTERN_RFC1036 = "EEEE, dd-MMM-yy HH:mm:ss zzz";

  /**
   * Date format pattern used to parse HTTP date headers in ANSI C <code>asctime()</code> format.
   */
  public static final String DATE_PATTERN_ASCTIME = "EEE MMM d HH:mm:ss yyyy";

  private static final Collection<String> DEFAULT_DATE_PATTERNS =
          Arrays.asList(new String[]{DATE_PATTERN_ASCTIME, DATE_PATTERN_RFC1036, DATE_PATTERN_RFC1123});

  private static final Date DEFAULT_TWO_DIGIT_YEAR_START;

  static {
    Calendar calendar = Calendar.getInstance();
    calendar.set(2000, Calendar.JANUARY, 1, 0, 0);
    DEFAULT_TWO_DIGIT_YEAR_START = calendar.getTime();
  }

  /**
   * Parses a date value with the default date patterns.
   *
   * @param dateValue the date value to parse
   * @return the parsed date
   * @throws DateParseException if the value could not be parsed with the supported date formats
   */
  public static Date parse(String dateValue) throws DateParseException {
    return parse(dateValue, null, null);
  }

  /**
   * Parses the date value using the specified date formats.
   *
   * @param dateValue   the date value to parse
   * @param dateFormats the date formats to use
   * @return the parsed date
   * @throws DateParseException if none of the dataFormats could parse the dateValue
   */
  public static Date parse(String dateValue, Collection<String> dateFormats) throws DateParseException {
    return parse(dateValue, dateFormats, null);
  }

  /**
   * Parses the date value using the specified date formats.
   *
   * @param dateValue   the date value to parse
   * @param dateFormats the date formats to use
   * @param startDate   During parsing, two digit years will be placed in the range
   *  <code>startDate</code> to <code>startDate + 100 years</code>. This value may be <code>null</code>.
   *  When <code>null</code> is specified as a parameter, year <code>2000</code> will be used.
   * @return the parsed date
   * @throws DateParseException if none of the dataFormats could parse the dateValue
   */
  public static Date parse(String dateValue, Collection<String> dateFormats, Date startDate) throws DateParseException {
    if (dateValue == null) {
      throw new IllegalArgumentException("dateValue is null");
    }
    if (dateFormats == null) {
      dateFormats = DEFAULT_DATE_PATTERNS;
    }
    if (startDate == null) {
      startDate = DEFAULT_TWO_DIGIT_YEAR_START;
    }
    // trim single quotes around date if present
    if ( (dateValue.length() > 1)
          && dateValue.startsWith("'")
          && dateValue.endsWith("'") ) {
      dateValue = dateValue.substring(1, dateValue.length() - 1);
    }
    SimpleDateFormat dateParser = null;
    for (String format : dateFormats) {
      if (dateParser == null) {
        dateParser = new SimpleDateFormat(format, Locale.US);
        dateParser.setTimeZone(TimeZone.getTimeZone("GMT"));
        dateParser.set2DigitYearStart(startDate);
      } else {
        dateParser.applyPattern(format);
      }
      try {
        return dateParser.parse(dateValue);
      } catch (ParseException pe) {
        // $JL-EXC$
      }
    }
    //unable to parse the date
    throw new DateParseException("Unable to parse the date " + dateValue);
  }

  /**
   * This class should not be instantiated.
   */
  private DateParser() {
  }

}
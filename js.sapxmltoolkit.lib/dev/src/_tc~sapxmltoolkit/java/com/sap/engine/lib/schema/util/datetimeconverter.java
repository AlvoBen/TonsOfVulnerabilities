/*
 * Copyright:    Copyright (c) 2002
 * Company:      SAP AG
 * created:      Mar 8, 2002 , 6:08:11 PM
 */
package com.sap.engine.lib.schema.util;

import com.sap.engine.lib.schema.exception.SchemaException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Loads and saves time according to schema specification may need some adjusting.
 * @author       Chavdar Baykov , Chavdarb@yahoo.com
 * @version      1.0
 * @deprecated Please use javax.xml.datatype.DatatypeFactory provided by JDK 5.
 */
@Deprecated
public class DateTimeConverter {

  // Return canonical time representation according Schema specification
  public static final String getCanonical(java.util.Calendar calendar) {
    Calendar workCalendar = (Calendar) calendar.clone();
    workCalendar.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    df.setCalendar(workCalendar);
    return df.format(workCalendar.getTime());
  }

  // Return relative time according given calendar
  public static final String getRelative(java.util.Calendar calendar) {
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    df.setCalendar(calendar);
    StringBuffer result = new StringBuffer();
    result.append(df.format(calendar.getTime()));
    int offset = calendar.get(Calendar.ZONE_OFFSET) / (60 * 60 * 1000);

    if (offset >= 0) {
      result.append('+');
    } else {
      result.append('-');
    }

    offset = Math.abs(offset);

    if (offset < 10) {
      result.append('0');
    }

    result.append(Integer.toString(offset));
    result.append(":00");
    return result.toString();
  }

  // Parses relative time
  public static final Calendar parseRelative(String time) throws Exception {
    time = time.trim();
    int signPos = time.indexOf('+');

    if (signPos == -1) {
      signPos = time.lastIndexOf('-');

      if (signPos < time.indexOf('T')) {
        signPos = -1;
      }
    }

    if (signPos == -1) { // if there is no sign in time we suppose the time zone doesn't matter use current timezone
      Calendar calendar = new GregorianCalendar();
      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
      df.setCalendar(calendar);
      Date sentTime;
      try {
        sentTime = df.parse(time);
      } catch (Exception e) {
        throw new Exception("Time could not be parsed correctly [" + time + "]");
      }
      calendar.setTime(sentTime);
      return calendar;
    } else {
      char sign = time.charAt(signPos);
      Calendar calendar = new GregorianCalendar();
      SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
      timeFormat.setCalendar(calendar);
      df.setCalendar(calendar);
      String mainTime = time.substring(0, signPos);
      String offset = time.substring(signPos + 1);
      try {
        timeFormat.parse(offset);
      } catch (Exception e) {
        throw new Exception(" Zone offset not correct [" + offset + "]");
      }
      try {
        calendar.setTime(df.parse(mainTime));
        offset = time.substring(signPos);
        calendar.setTimeZone(TimeZone.getTimeZone("GMT" + offset));
      } catch (Exception e) {
        throw new Exception("Time could not be parsed Correctly [" + time + "]");
      }
      return calendar;
    }
  }

  // Parses canonical time
  public static final Calendar parseCanonical(String time) throws Exception {
    Calendar calendar = new GregorianCalendar();
    calendar.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    df.setCalendar(calendar);
    try {
      calendar.setTime(df.parse(time));
    } catch (Exception e) {
      throw new Exception("Canonical time could not be parsed correctly [" + time + "]");
    }
    return calendar;
  }

  // Parses xml time formated canonical or  relative
  public static final Calendar parseDateTime(String time) throws SchemaException {
    if (time.indexOf('Z') != -1) {
      try {
        Calendar result = parseCanonical(time);
        return result;
      } catch (Exception e) {
        throw new SchemaException(e);
      }
    } else {
      try {
        Calendar result = parseRelative(time);
        return result;
      } catch (Exception e) {
        throw new SchemaException(e);
      }
    }
  }

}

// Simple functionality tester
//GregorianCalendar calendar = new GregorianCalendar(); // Gets Current calendar
//LogWriter.getSystemLogWriter().println(DateTimeConverter.getCanonical(calendar));
//LogWriter.getSystemLogWriter().println(DateTimeConverter.getRelative(calendar));
//String toParse = DateTimeConverter.getRelative(calendar);
//String canonical = DateTimeConverter.getCanonical(calendar);
//LogWriter.getSystemLogWriter().println("--------------------");
//Calendar resCalendar = DateTimeConverter.parseRelative(toParse);
//Calendar resCalendar2 = DateTimeConverter.parseRelative("2000-01-15T00:00:00");
//Calendar resCalendar3 = DateTimeConverter.parseCanonical(canonical);
//LogWriter.getSystemLogWriter().println(DateTimeConverter.getCanonical(resCalendar));
//LogWriter.getSystemLogWriter().println(DateTimeConverter.getRelative(resCalendar));
//LogWriter.getSystemLogWriter().println("--------------------");
//LogWriter.getSystemLogWriter().println(DateTimeConverter.getCanonical(resCalendar2));
//LogWriter.getSystemLogWriter().println(DateTimeConverter.getRelative(resCalendar2));
//LogWriter.getSystemLogWriter().println("--------------------");
//LogWriter.getSystemLogWriter().println(DateTimeConverter.getCanonical(resCalendar3));
//LogWriter.getSystemLogWriter().println(DateTimeConverter.getRelative(resCalendar3));


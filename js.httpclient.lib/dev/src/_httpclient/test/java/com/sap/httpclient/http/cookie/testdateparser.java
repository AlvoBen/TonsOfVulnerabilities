package com.sap.httpclient.http.cookie;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.sap.httpclient.utils.DateParser;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test cases for expiry date parsing
 */
public class TestDateParser extends TestCase {

  public TestDateParser(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(TestDateParser.class);
  }

  private static final String PATTERN = "EEE, dd-MMM-yy HH:mm:ss zzz";
  private static final List<String> PATTERNS = new ArrayList<String>();

  static {
    PATTERNS.add(PATTERN);
  }

  public void testFourDigitYear() throws Exception {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(DateParser.parse("Thu, 15-Dec-2006 24:00:00 CET", PATTERNS));
    assertEquals(2006, calendar.get(Calendar.YEAR));
  }

  public void testThreeDigitYear() throws Exception {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(DateParser.parse("Thu, 16-Dec-999 24:00:00 CET", PATTERNS));
    assertEquals(999, calendar.get(Calendar.YEAR));
  }

  public void testTwoDigitYear() throws Exception {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(DateParser.parse("Thu, 17-Dec-06 24:00:00 CET", PATTERNS));
    assertEquals(2006, calendar.get(Calendar.YEAR));
    calendar.setTime(DateParser.parse("Thu, 18-Dec-92 24:00:00 CET", PATTERNS));
    assertEquals(2092, calendar.get(Calendar.YEAR));
  }

}
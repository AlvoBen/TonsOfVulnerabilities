package com.sap.httpclient;

import com.sap.httpclient.utils.ParameterFormatter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit tests for {@link ParameterFormatter}.
 */
public class TestParameterFormatter extends TestCase {

  public TestParameterFormatter(String testName) {
    super(testName);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestParameterFormatter.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public static Test suite() {
    return new TestSuite(TestParameterFormatter.class);
  }

  public void testBasicValueFormatting() throws Exception {
    ParameterFormatter formatter = new ParameterFormatter();
    NameValuePair param1 = new NameValuePair("param", "regular_stuff");
    NameValuePair param2 = new NameValuePair("param", "this\\that");
    NameValuePair param3 = new NameValuePair("param", "this,that");
    NameValuePair param4 = new NameValuePair("param", "quote marks (\") must be escaped");
    NameValuePair param5 = new NameValuePair("param", "back slash (\\) must be escaped too");
    NameValuePair param6 = new NameValuePair("param", "values with\tblanks must always be quoted");
    formatter.setUseQuotes(false);
    assertEquals("param=regular_stuff", formatter.format(param1));
    assertEquals("param=\"this\\\\that\"", formatter.format(param2));
    assertEquals("param=\"this,that\"", formatter.format(param3));
    assertEquals("param=\"quote marks (\\\") must be escaped\"", formatter.format(param4));
    assertEquals("param=\"back slash (\\\\) must be escaped too\"", formatter.format(param5));
    assertEquals("param=\"values with\tblanks must always be quoted\"", formatter.format(param6));
    formatter.setUseQuotes(true);
    assertEquals("param=\"regular_stuff\"", formatter.format(param1));
    assertEquals("param=\"this\\\\that\"", formatter.format(param2));
    assertEquals("param=\"this,that\"", formatter.format(param3));
    assertEquals("param=\"quote marks (\\\") must be escaped\"", formatter.format(param4));
    assertEquals("param=\"back slash (\\\\) must be escaped too\"", formatter.format(param5));
    assertEquals("param=\"values with\tblanks must always be quoted\"", formatter.format(param6));
  }

}
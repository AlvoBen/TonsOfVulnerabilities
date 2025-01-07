package com.sap.httpclient;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.List;

import com.sap.httpclient.utils.ParameterParser;

/**
 * Unit tests for {@link ParameterParser}.
 */
public class TestParameterParser extends TestCase {

  public TestParameterParser(String testName) {
    super(testName);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestParameterParser.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public static Test suite() {
    return new TestSuite(TestParameterParser.class);
  }

  public void testParsing() {
    String s = "test; test1 =  stuff   ; test2 =  \"stuff; stuff\"; test3=\"stuff";
    ParameterParser parser = new ParameterParser();
    List params = parser.parse(s, ';');
    assertEquals("test", ((NameValuePair) params.get(0)).getName());
    assertEquals(null, ((NameValuePair) params.get(0)).getValue());
    assertEquals("test1", ((NameValuePair) params.get(1)).getName());
    assertEquals("stuff", ((NameValuePair) params.get(1)).getValue());
    assertEquals("test2", ((NameValuePair) params.get(2)).getName());
    assertEquals("stuff; stuff", ((NameValuePair) params.get(2)).getValue());
    assertEquals("test3", ((NameValuePair) params.get(3)).getName());
    assertEquals("\"stuff", ((NameValuePair) params.get(3)).getValue());
    s = "  test  , test1=stuff   ,  , test2=, test3, ";
    params = parser.parse(s, ',');
    assertEquals("test", ((NameValuePair) params.get(0)).getName());
    assertEquals(null, ((NameValuePair) params.get(0)).getValue());
    assertEquals("test1", ((NameValuePair) params.get(1)).getName());
    assertEquals("stuff", ((NameValuePair) params.get(1)).getValue());
    assertEquals("test2", ((NameValuePair) params.get(2)).getName());
    assertEquals("", ((NameValuePair) params.get(2)).getValue());
    assertEquals("test3", ((NameValuePair) params.get(3)).getName());
    assertEquals(null, ((NameValuePair) params.get(3)).getValue());
    s = "  test";
    params = parser.parse(s, ';');
    assertEquals("test", ((NameValuePair) params.get(0)).getName());
    assertEquals(null, ((NameValuePair) params.get(0)).getValue());
    s = "  ";
    params = parser.parse(s, ';');
    assertEquals(0, params.size());
    s = " = stuff ";
    params = parser.parse(s, ';');
    assertEquals(1, params.size());
    assertEquals("", ((NameValuePair) params.get(0)).getName());
    assertEquals("stuff", ((NameValuePair) params.get(0)).getValue());
  }

  public void testParsingEscapedChars() {
    String s = "param = \"stuff\\\"; more stuff\"";
    ParameterParser parser = new ParameterParser();
    List params = parser.parse(s, ';');
    assertEquals(1, params.size());
    assertEquals("param", ((NameValuePair) params.get(0)).getName());
    assertEquals("stuff\\\"; more stuff", ((NameValuePair) params.get(0)).getValue());
    s = "param = \"stuff\\\\\"; anotherparam";
    params = parser.parse(s, ';');
    assertEquals(2, params.size());
    assertEquals("param", ((NameValuePair) params.get(0)).getName());
    assertEquals("stuff\\\\", ((NameValuePair) params.get(0)).getValue());
    assertEquals("anotherparam", ((NameValuePair) params.get(1)).getName());
    assertNull(((NameValuePair) params.get(1)).getValue());
  }

  public void testParsingBlankParams() {
    String s = "test; test1 =  ; test2 = \"\"";
    ParameterParser parser = new ParameterParser();
    List params = parser.parse(s, ';');
    assertEquals("test", ((NameValuePair) params.get(0)).getName());
    assertEquals(null, ((NameValuePair) params.get(0)).getValue());
    assertEquals("test1", ((NameValuePair) params.get(1)).getName());
    assertEquals("", ((NameValuePair) params.get(1)).getValue());
    assertEquals("test2", ((NameValuePair) params.get(2)).getName());
    assertEquals("", ((NameValuePair) params.get(2)).getValue());
  }
}
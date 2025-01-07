package com.sap.httpclient;

import junit.framework.*;

/**
 * Simple tests for {@link NameValuePair}.
 */
public class TestNVP extends TestCase {

  public TestNVP(String testName) {
    super(testName);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestNVP.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public static Test suite() {
    return new TestSuite(TestNVP.class);
  }

  protected NameValuePair makePair() {
    return new NameValuePair();
  }

  protected NameValuePair makePair(String name, String value) {
    return new NameValuePair(name, value);
  }

  public void testGet() {
    NameValuePair pair = makePair("name 1", "value 1");
    assertEquals("name 1", pair.getName());
    assertEquals("value 1", pair.getValue());
  }

  public void testSet() {
    NameValuePair pair = makePair();
    assertTrue(null == pair.getName());
    assertTrue(null == pair.getValue());
    pair.setName("name");
    assertEquals("name", pair.getName());
    pair.setValue("value");
    assertEquals("value", pair.getValue());
  }

  public void testHashCode() {
    NameValuePair param1 = new NameValuePair("name1", "value1");
    NameValuePair param2 = new NameValuePair("name2", "value2");
    NameValuePair param3 = new NameValuePair("name1", "value1");
    assertTrue(param1.hashCode() != param2.hashCode());
    assertTrue(param1.hashCode() == param3.hashCode());
  }

  public void testEquals() {
    NameValuePair param1 = new NameValuePair("name1", "value1");
    NameValuePair param2 = new NameValuePair("name2", "value2");
    NameValuePair param3 = new NameValuePair("name1", "value1");
    assertFalse(param1.equals(param2));
    assertFalse(param1.equals(null));
    assertFalse(param1.equals("name1 = value1"));
    assertTrue(param1.equals(param1));
    assertTrue(param2.equals(param2));
    assertTrue(param1.equals(param3));
  }

}
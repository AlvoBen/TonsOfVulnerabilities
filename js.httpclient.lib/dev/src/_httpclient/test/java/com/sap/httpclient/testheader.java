package com.sap.httpclient;

import junit.framework.*;
import com.sap.httpclient.http.Header;

/**
 * Simple tests for {@link NameValuePair}.
 */
public class TestHeader extends TestNVP {

  public TestHeader(String testName) {
    super(testName);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestHeader.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public static Test suite() {
    return new TestSuite(TestHeader.class);
  }

  protected NameValuePair makePair() {
    return new Header();
  }

  protected NameValuePair makePair(String name, String value) {
    return new Header(name, value);
  }

  public void testToExternalFormNull() {
    Header header = (Header) makePair();
    assertEquals(": \r\n", header.toText());
  }

  public void testToExternalFormNullName() {
    Header header = (Header) makePair(null, "value");
    assertEquals(": value\r\n", header.toText());
  }

  public void testToExternalFormNullValue() {
    Header header = (Header) makePair("name", null);
    assertEquals("name: \r\n", header.toText());
  }

  public void testToExternalForm() {
    Header header = (Header) makePair("a", "b");
    assertEquals("a: b\r\n", header.toText());
  }

  public void testEqualToNVP() {
    NameValuePair header = makePair("a", "b");
    NameValuePair pair = new NameValuePair("a", "b");
    assertTrue(header.equals(pair));
    assertTrue(pair.equals(header));
  }
}
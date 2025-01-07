package com.sap.httpclient;

import junit.framework.*;
import com.sap.httpclient.http.HeaderElement;

/**
 * Simple tests for {@link HeaderElement}.
 */
public class TestHeaderElement extends TestNVP {

  public TestHeaderElement(String testName) {
    super(testName);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestHeaderElement.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public static Test suite() {
    return new TestSuite(TestHeaderElement.class);
  }

  protected NameValuePair makePair() {
    return new HeaderElement();
  }

  protected NameValuePair makePair(String name, String value) {
    return new HeaderElement(name, value);
  }

  public void testOldMain() throws Exception {
    // this is derived from the old main method in HeaderElement
    String headerValue = "name1 = value1; name2; name3=\"value3\" , name4=value4; " +
            "name5=value5, name6= ; name7 = value7; name8 = \" value8\"";
    HeaderElement[] elements = HeaderElement.parseElements(headerValue);
    // there are 3 elements
    assertEquals(3, elements.length);
    // 1st element
    assertEquals("name1", elements[0].getName());
    assertEquals("value1", elements[0].getValue());
    // 1st element has 2 getParameters()
    assertEquals(2, elements[0].getParameters().length);
    assertEquals("name2", elements[0].getParameters()[0].getName());
    assertEquals(null, elements[0].getParameters()[0].getValue());
    assertEquals("name3", elements[0].getParameters()[1].getName());
    assertEquals("value3", elements[0].getParameters()[1].getValue());
    // 2nd element
    assertEquals("name4", elements[1].getName());
    assertEquals("value4", elements[1].getValue());
    // 2nd element has 1 parameter
    assertEquals(1, elements[1].getParameters().length);
    assertEquals("name5", elements[1].getParameters()[0].getName());
    assertEquals("value5", elements[1].getParameters()[0].getValue());
    // 3rd element
    assertEquals("name6", elements[2].getName());
    assertEquals("", elements[2].getValue());
    // 3rd element has 2 getParameters()
    assertEquals(2, elements[2].getParameters().length);
    assertEquals("name7", elements[2].getParameters()[0].getName());
    assertEquals("value7", elements[2].getParameters()[0].getValue());
    assertEquals("name8", elements[2].getParameters()[1].getName());
    assertEquals(" value8", elements[2].getParameters()[1].getValue());
  }

  public void testFringeCase1() throws Exception {
    String headerValue = "name1 = value1,";
    HeaderElement[] elements = HeaderElement.parseElements(headerValue);
    assertEquals("Number of elements", 1, elements.length);
  }

  public void testFringeCase2() throws Exception {
    String headerValue = "name1 = value1, ";
    HeaderElement[] elements = HeaderElement.parseElements(headerValue);
    assertEquals("Number of elements", 1, elements.length);
  }

  public void testFringeCase3() throws Exception {
    String headerValue = ",, ,, ,";
    HeaderElement[] elements = HeaderElement.parseElements(headerValue);
    assertEquals("Number of elements", 0, elements.length);
  }
}
package com.sap.httpclient;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import junit.framework.*;
import com.sap.httpclient.exception.HttpException;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.http.HttpParser;

/**
 * Simple tests for {@link HttpParser}.
 */
public class TestHttpParser extends TestCase {

  private static final String HTTP_ELEMENT_CHARSET = "US-ASCII";

  public TestHttpParser(String testName) {
    super(testName);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestHttpParser.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public static Test suite() {
    return new TestSuite(TestHttpParser.class);
  }

  public void testReadHttpLine() throws Exception {
    InputStream instream = new ByteArrayInputStream("\r\r\nstuff\r\n".getBytes(HTTP_ELEMENT_CHARSET));
    assertEquals("\r", HttpParser.readLine(instream, HTTP_ELEMENT_CHARSET));
    assertEquals("stuff", HttpParser.readLine(instream, HTTP_ELEMENT_CHARSET));
    assertEquals(null, HttpParser.readLine(instream, HTTP_ELEMENT_CHARSET));
    instream = new ByteArrayInputStream("\n\r\nstuff\r\n".getBytes("US-ASCII"));
    assertEquals("", HttpParser.readLine(instream, HTTP_ELEMENT_CHARSET));
    assertEquals("", HttpParser.readLine(instream, HTTP_ELEMENT_CHARSET));
    assertEquals("stuff", HttpParser.readLine(instream, HTTP_ELEMENT_CHARSET));
    assertEquals(null, HttpParser.readLine(instream, HTTP_ELEMENT_CHARSET));
  }

  public void testReadWellFormedHttpHeaders() throws Exception {
    InputStream instream = new ByteArrayInputStream("a: a\r\nb: b\r\n\r\nwhatever".getBytes(HTTP_ELEMENT_CHARSET));
    ArrayList<Header> headers = (HttpParser.parseHeaders(instream, HTTP_ELEMENT_CHARSET));
    assertNotNull(headers);
    assertEquals(2, headers.size());
    assertEquals("a", headers.get(0).getName());
    assertEquals("a", headers.get(0).getValue());
    assertEquals("b", headers.get(1).getName());
    assertEquals("b", headers.get(1).getValue());
  }

  public void testReadMalformedHttpHeaders() throws Exception {
    InputStream instream = new ByteArrayInputStream("a: a\r\nb b\r\n\r\nwhatever".getBytes(HTTP_ELEMENT_CHARSET));
    try {
      HttpParser.parseHeaders(instream, HTTP_ELEMENT_CHARSET);
      fail("HttpException should have been thrown");
    } catch (HttpException is_OK) {
      // $JL-EXC$
    }
  }

  public void testHeadersTerminatorLeniency1() throws Exception {
    InputStream instream = new ByteArrayInputStream("a: a\r\nb: b\r\n\r\r\nwhatever".getBytes(HTTP_ELEMENT_CHARSET));
    ArrayList<Header> headers = HttpParser.parseHeaders(instream, HTTP_ELEMENT_CHARSET);
    assertNotNull(headers);
    assertEquals(2, headers.size());
    assertEquals("a", headers.get(0).getName());
    assertEquals("a", headers.get(0).getValue());
    assertEquals("b", headers.get(1).getName());
    assertEquals("b", headers.get(1).getValue());
  }

  public void testHeadersTerminatorLeniency2() throws Exception {
    InputStream instream = new ByteArrayInputStream("a: a\r\nb: b\r\n    \r\nwhatever".getBytes(HTTP_ELEMENT_CHARSET));
    ArrayList<Header> headers = HttpParser.parseHeaders(instream, HTTP_ELEMENT_CHARSET);
    assertNotNull(headers);
    assertEquals(2, headers.size());
    assertEquals("a", headers.get(0).getName());
    assertEquals("a", headers.get(0).getValue());
    assertEquals("b", headers.get(1).getName());
    assertEquals("b", headers.get(1).getValue());
  }
}
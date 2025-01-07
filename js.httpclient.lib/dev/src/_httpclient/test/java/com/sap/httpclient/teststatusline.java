package com.sap.httpclient;

import junit.framework.*;
import com.sap.httpclient.exception.HttpException;
import com.sap.httpclient.http.StatusLine;

/**
 * Simple tests for {@link StatusLine}.
 */
public class TestStatusLine extends TestCase {

  private StatusLine statusLine = null;

  public TestStatusLine(String testName) {
    super(testName);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestStatusLine.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public static Test suite() {
    return new TestSuite(TestStatusLine.class);
  }

  public void testIfStatusLine() throws Exception {
    assertTrue(StatusLine.startsWithHTTP("HTTP"));
    assertTrue(StatusLine.startsWithHTTP("         HTTP"));
    assertTrue(StatusLine.startsWithHTTP("\rHTTP"));
    assertTrue(StatusLine.startsWithHTTP("\tHTTP"));
    assertFalse(StatusLine.startsWithHTTP("crap"));
    assertFalse(StatusLine.startsWithHTTP("HTT"));
    assertFalse(StatusLine.startsWithHTTP("http"));
  }

  public void testSuccess() throws Exception {
    //typical status line
    statusLine = new StatusLine("HTTP/1.1 200 OK");
    assertEquals("HTTP/1.1 200 OK", statusLine.toString());
    assertEquals("HTTP/1.1", statusLine.getHttpVersion());
    assertEquals(200, statusLine.getStatusCode());
    assertEquals("OK", statusLine.getReasonPhrase());
    //status line with multi word reason phrase
    statusLine = new StatusLine("HTTP/1.1 404 Not Found");
    assertEquals(404, statusLine.getStatusCode());
    assertEquals("Not Found", statusLine.getReasonPhrase());
    //reason phrase can be anyting
    statusLine = new StatusLine("HTTP/1.1 404 Non Trouve");
    assertEquals("Non Trouve", statusLine.getReasonPhrase());
    //its ok to end with a \n\r
    statusLine = new StatusLine("HTTP/1.1 404 Not Found\r\n");
    assertEquals("Not Found", statusLine.getReasonPhrase());
    //this is valid according to the Status-Line BNF
    statusLine = new StatusLine("HTTP/1.1 200 ");
    assertEquals(200, statusLine.getStatusCode());
    assertEquals("", statusLine.getReasonPhrase());
    //this is not strictly valid, but is lienent
    statusLine = new StatusLine("HTTP/1.1 200");
    assertEquals(200, statusLine.getStatusCode());
    assertEquals("", statusLine.getReasonPhrase());
    //this is not strictly valid, but is lienent
    statusLine = new StatusLine("HTTP/1.1     200 OK");
    assertEquals(200, statusLine.getStatusCode());
    assertEquals("OK", statusLine.getReasonPhrase());
    //this is not strictly valid, but is lienent
    statusLine = new StatusLine("\rHTTP/1.1 200 OK");
    assertEquals(200, statusLine.getStatusCode());
    assertEquals("OK", statusLine.getReasonPhrase());
    assertEquals("HTTP/1.1", statusLine.getHttpVersion());
    //this is not strictly valid, but is lienent
    statusLine = new StatusLine("  HTTP/1.1 200 OK");
    assertEquals(200, statusLine.getStatusCode());
    assertEquals("OK", statusLine.getReasonPhrase());
    assertEquals("HTTP/1.1", statusLine.getHttpVersion());
  }

  public void testFailure() throws Exception {
    try {
      statusLine = new StatusLine(null);
      fail();
    } catch (NullPointerException is_OK) {
      // $JL-EXC$
    }

    try {
      statusLine = new StatusLine("xxx 200 OK");
      fail();
    } catch (HttpException is_OK) {
      // $JL-EXC$
    }

    try {
      statusLine = new StatusLine("HTTP/1.1 xxx OK");
      fail();
    } catch (HttpException is_OK) {
      // $JL-EXC$
    }

    try {
      statusLine = new StatusLine("HTTP/1.1    ");
      fail();
    } catch (HttpException is_OK) {
      // $JL-EXC$
    }
  }

}
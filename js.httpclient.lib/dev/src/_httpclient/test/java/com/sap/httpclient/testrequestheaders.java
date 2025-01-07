package com.sap.httpclient;

import com.sap.httpclient.net.Protocol;
import com.sap.httpclient.net.connection.HttpConnection;
import com.sap.httpclient.http.Header;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests for reading response headers.
 */
public class TestRequestHeaders extends TestCase {

  public TestRequestHeaders(String testName) {
    super(testName);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestRequestHeaders.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public static Test suite() {
    return new TestSuite(TestRequestHeaders.class);
  }

  public void testNullHeader() throws Exception {
    FakeHttpMethod method = new FakeHttpMethod();
    assertEquals(null, method.getRequestHeader(null));
    assertEquals(null, method.getRequestHeader("bogus"));
  }

  public void testHostHeaderPortHTTP80() throws Exception {
    HttpConnection conn = new HttpConnection("some.host.name", 80);
    HttpState state = new HttpState();
    FakeHttpMethod method = new FakeHttpMethod();
    method.addRequestHeaders(state, conn);
    assertEquals("Host: some.host.name", method.getRequestHeader("Host").toString().trim());
  }

  public void testHostHeaderPortHTTP81() throws Exception {
    HttpConnection conn = new HttpConnection("some.host.name", 81);
    HttpState state = new HttpState();
    FakeHttpMethod method = new FakeHttpMethod();
    method.addRequestHeaders(state, conn);
    assertEquals("Host: some.host.name:81", method.getRequestHeader("Host").toString().trim());
  }

  public void testHostHeaderPortHTTPS443() throws Exception {
    HttpConnection conn = new HttpConnection("some.host.name", 443, Protocol.getProtocol("https"));
    HttpState state = new HttpState();
    FakeHttpMethod method = new FakeHttpMethod();
    method.addRequestHeaders(state, conn);
    assertEquals("Host: some.host.name", method.getRequestHeader("Host").toString().trim());
  }

  public void testHostHeaderPortHTTPS444() throws Exception {
    HttpConnection conn = new HttpConnection("some.host.name", 444, Protocol.getProtocol("https"));
    HttpState state = new HttpState();
    FakeHttpMethod method = new FakeHttpMethod();
    method.addRequestHeaders(state, conn);
    assertEquals("Host: some.host.name:444", method.getRequestHeader("Host").toString().trim());
  }

  public void testHeadersPreserveCaseKeyIgnoresCase() throws Exception {
    FakeHttpMethod method = new FakeHttpMethod();
    method.addRequestHeader(new Header("NAME", "VALUE"));
    Header upHeader = method.getRequestHeader("NAME");
    Header loHeader = method.getRequestHeader("name");
    Header mixHeader = method.getRequestHeader("nAmE");
    assertEquals("NAME", upHeader.getName());
    assertEquals("VALUE", upHeader.getValue());
    assertEquals("NAME", loHeader.getName());
    assertEquals("VALUE", loHeader.getValue());
    assertEquals("NAME", mixHeader.getName());
    assertEquals("VALUE", mixHeader.getValue());
  }
}
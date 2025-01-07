package com.sap.httpclient;

import com.sap.httpclient.net.Protocol;
import com.sap.httpclient.net.connection.HttpConnection;
import com.sap.httpclient.http.HttpVersion;
import junit.framework.*;

/**
 * Simple tests for {@link com.sap.httpclient.http.StatusLine}.
 */
public class TestRequestLine extends TestCase {

  public TestRequestLine(String testName) {
    super(testName);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestRequestLine.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public static Test suite() {
    return new TestSuite(TestRequestLine.class);
  }

  public void testRequestLineGeneral() throws Exception {
    HttpConnection conn = new HttpConnection("localhost", 80);
    FakeHttpMethod method = new FakeHttpMethod();
    assertEquals("Simple / HTTP/1.1\r\n", method.generateRequestLine(conn, HttpVersion.HTTP_1_1));
    method = new FakeHttpMethod("stuff");
    assertEquals("Simple stuff HTTP/1.1\r\n", method.generateRequestLine(conn, HttpVersion.HTTP_1_1));
    conn = new HttpConnection("proxy", 8080, "localhost", 80, Protocol.getProtocol("http"));
    method = new FakeHttpMethod();
    assertEquals("Simple http://localhost/ HTTP/1.1\r\n", method.generateRequestLine(conn, HttpVersion.HTTP_1_1));
    method = new FakeHttpMethod("stuff");
    assertEquals("Simple http://localhost/stuff HTTP/1.1\r\n", method.generateRequestLine(conn, HttpVersion.HTTP_1_1));
    conn = new HttpConnection("proxy", 8080, "localhost", -1, Protocol.getProtocol("http"));
    method = new FakeHttpMethod();
    assertEquals("Simple http://localhost/ HTTP/1.1\r\n", method.generateRequestLine(conn, HttpVersion.HTTP_1_1));
    method = new FakeHttpMethod("stuff");
    assertEquals("Simple http://localhost/stuff HTTP/1.1\r\n", method.generateRequestLine(conn, HttpVersion.HTTP_1_1));
    conn = new HttpConnection("proxy", 8080, "localhost", 666, Protocol.getProtocol("http"));
    method = new FakeHttpMethod();
    assertEquals("Simple http://localhost:666/ HTTP/1.1\r\n", method.generateRequestLine(conn, HttpVersion.HTTP_1_1));
    method = new FakeHttpMethod("stuff");
    assertEquals("Simple http://localhost:666/stuff HTTP/1.1\r\n", method.generateRequestLine(conn, HttpVersion.HTTP_1_1));
  }

  public void testRequestLineQuery() throws Exception {
    HttpConnection conn = new HttpConnection("localhost", 80);

    FakeHttpMethod method = new FakeHttpMethod();
    method.setQueryString(new NameValuePair[]{
      new NameValuePair("param1", " !#$%&\'()*+,-./:;<=>?@[\\]^_`{|}~"),
      new NameValuePair("param2", "some stuff")
    });
    assertEquals("Simple /?param1=+%21%23%24%25%26%27%28%29*%2B%2C-.%2F%3A%3B%3C%3D%3E%3F%40%5B%5C%5D%5E_%60%7B%7C%7D%7E&param2=some+stuff HTTP/1.1\r\n",
            method.generateRequestLine(conn, HttpVersion.HTTP_1_1));
  }

  public void testRequestLinePath() throws Exception {
    HttpConnection conn = new HttpConnection("localhost", 80);
    FakeHttpMethod method = new FakeHttpMethod();
    method.setPath("/some%20stuff/");
    assertEquals("Simple /some%20stuff/ HTTP/1.1\r\n", method.generateRequestLine(conn, HttpVersion.HTTP_1_1));
    method = new FakeHttpMethod("/some%20stuff/");
    assertEquals("Simple /some%20stuff/ HTTP/1.1\r\n", method.generateRequestLine(conn, HttpVersion.HTTP_1_1));
  }
}
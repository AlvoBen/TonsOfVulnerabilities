package com.sap.httpclient;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import com.sap.httpclient.http.methods.GET;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.http.HttpVersion;
import com.sap.httpclient.http.HttpStatus;
import com.sap.httpclient.server.HttpService;
import com.sap.httpclient.server.SimpleRequest;
import com.sap.httpclient.server.SimpleResponse;
import com.sap.httpclient.exception.URIException;
import com.sap.httpclient.net.connection.HttpConnection;
import com.sap.httpclient.net.connection.HttpConnectionManagerImpl;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests basic method functionality.
 */
public class TestHttpMethodFundamentals extends HttpClientTestBase {

  public TestHttpMethodFundamentals(final String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(TestHttpMethodFundamentals.class);
    ProxyTestDecorator.addTests(suite);
    return suite;
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestHttpMethodFundamentals.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  class ManyAService implements HttpService {

    public ManyAService() {
      super();
    }

    public boolean process(final SimpleRequest request, final SimpleResponse response) throws IOException {
      HttpVersion httpversion = request.getRequestLine().getHttpVersion();
      response.setStatusLine(httpversion, HttpStatus.SC_OK);
      response.addHeader(new Header("Content-Type", "text/plain"));
      response.addHeader(new Header("Connection", "close"));
      StringBuilder buffer = new StringBuilder(1024);
      for (int i = 0; i < 1024; i++) {
        buffer.append('A');
      }
      response.setBodyString(buffer.toString());
      return true;
    }
  }

  class SimpleChunkedService implements HttpService {

    public SimpleChunkedService() {
      super();
    }

    public boolean process(final SimpleRequest request, final SimpleResponse response) throws IOException {
      HttpVersion httpversion = request.getRequestLine().getHttpVersion();
      response.setStatusLine(httpversion, HttpStatus.SC_OK);
      response.addHeader(new Header("Content-Type", "text/plain"));
      response.addHeader(new Header("Content-Length", "garbage"));
      response.addHeader(new Header("Transfer-Encoding", "chunked"));
      response.addHeader(new Header("Connection", "close"));
      response.setBodyString("1234567890123");
      return true;
    }
  }

  class EmptyResponseService implements HttpService {

    public EmptyResponseService() {
      super();
    }

    public boolean process(final SimpleRequest request, final SimpleResponse response) throws IOException {
      HttpVersion httpversion = request.getRequestLine().getHttpVersion();
      response.setStatusLine(httpversion, HttpStatus.SC_OK);
      response.addHeader(new Header("Content-Type", "text/plain"));
      response.addHeader(new Header("Transfer-Encoding", "chunked"));
      response.addHeader(new Header("Connection", "close"));
      return true;
    }
  }

  public void testHttpMethodBasePaths() throws Exception {
    HttpMethod simple = new FakeHttpMethod();
    String[] paths = {
      "/some/absolute/path",
      "../some/relative/path",
      "/",
      "/some/path/with?query=string"
    };
		for (String path : paths) {
			simple.setPath(path);
			assertEquals(path, simple.getPath());
		}
	}

  public void testHttpMethodBaseDefaultPath() throws Exception {
    HttpMethod simple = new FakeHttpMethod();
    assertEquals("/", simple.getPath());
    simple.setPath("");
    assertEquals("/", simple.getPath());
    simple.setPath(null);
    assertEquals("/", simple.getPath());
  }

  public void testHttpMethodBasePathConstructor() throws Exception {
    HttpMethod simple = new FakeHttpMethod();
    assertEquals("/", simple.getPath());
    simple = new FakeHttpMethod("");
    assertEquals("/", simple.getPath());
    simple = new FakeHttpMethod("/some/path/");
    assertEquals("/some/path/", simple.getPath());
  }

  /**
   * Tests response with a Trasfer-Encoding and Content-Length
	 *
	 * @throws Exception if an exception occures
   */
  public void testHttpMethodBaseTEandCL() throws Exception {
    this.server.setHttpService(new SimpleChunkedService());
    GET httpget = new GET("/test/");
    try {
      this.client.executeMethod(httpget);
      assertEquals(HttpStatus.SC_OK, httpget.getStatusCode());
      assertEquals("1234567890123", httpget.getResponseBodyAsString());
      assertTrue(this.client.getHttpConnectionManager() instanceof HttpConnectionManagerImpl);
      HttpConnection conn = this.client.getHttpConnectionManager().
              getConnection(this.client.getHostConfiguration());
      assertNotNull(conn);
      conn.assertNotOpen();
    } finally {
      httpget.releaseConnection();
    }
  }

  public void testConnectionAutoClose() throws Exception {
    this.server.setHttpService(new ManyAService());
    GET httpget = new GET("/test/");
    try {
      this.client.executeMethod(httpget);
      assertEquals(HttpStatus.SC_OK, httpget.getStatusCode());
      Reader response = new InputStreamReader(httpget.getResponseBodyAsStream());
      int c;
      while ((c = response.read()) != -1) {
        assertEquals((int) 'A', c);
      }
      assertTrue(this.client.getHttpConnectionManager() instanceof HttpConnectionManagerImpl);
      HttpConnection conn = this.client.getHttpConnectionManager().
              getConnection(this.client.getHostConfiguration());
      assertNotNull(conn);
      conn.assertNotOpen();
    } finally {
      httpget.releaseConnection();
    }
  }

  public void testSetGetQueryString1() {
    HttpMethod method = new GET();
    String qs1 = "name1=value1&name2=value2";
    method.setQuery(qs1);
    assertEquals(qs1, method.getQuery());
  }

  public void testQueryURIEncoding() {
    HttpMethod method = new GET("http://server/servlet?foo=bar&baz=schmoo");
    assertEquals("foo=bar&baz=schmoo", method.getQuery());
  }

  public void testSetGetQueryString2() {
    HttpMethod method = new GET();
    NameValuePair[] q1 = new NameValuePair[]{
      new NameValuePair("name1", "value1"),
      new NameValuePair("name2", "value2")
    };
    method.setQueryString(q1);
    String qs1 = "name1=value1&name2=value2";
    assertEquals(qs1, method.getQuery());
  }

  /**
   * Make sure that its OK to call releaseConnection if the connection has not been.
   */
  public void testReleaseConnection() {
    HttpMethod method = new GET("http://bogus.url/path/");
    method.releaseConnection();
  }

  /**
   * Tests empty body response
	 *
	 * @throws Exception if an exception occures
   */
  public void testEmptyBodyAsString() throws Exception {
    this.server.setHttpService(new EmptyResponseService());
    GET httpget = new GET("/test/");
    try {
      this.client.executeMethod(httpget);
      assertEquals(HttpStatus.SC_OK, httpget.getStatusCode());
      String response = httpget.getResponseBodyAsString();
      assertNull(response);
    } finally {
      httpget.releaseConnection();
    }
  }


  public void testEmptyBodyAsByteArray() throws Exception {
    this.server.setHttpService(new EmptyResponseService());
    GET httpget = new GET("/test/");
    try {
      this.client.executeMethod(httpget);
      assertEquals(HttpStatus.SC_OK, httpget.getStatusCode());
      byte[] response = httpget.getResponseBody();
      assertNull(response);
    } finally {
      httpget.releaseConnection();
    }
  }

  public void testUrlGetMethodWithPathQuery() {
    GET method = new GET("http://www.fubar.com/path1/path2?query=string");
    try {
      assertEquals("Get URL",
              "http://www.fubar.com/path1/path2?query=string",
              method.getURI().toString());
    } catch (URIException e) {
      fail("trouble getting URI: " + e);
    }
    assertEquals("Get Path", "/path1/path2", method.getPath());
    assertEquals("Get query string", "query=string", method.getQuery());

  }

  public void testUrlGetMethodWithPath() {
    GET method = new GET("http://www.fubar.com/path1/path2");
    try {
      assertEquals("Get URL",
              "http://www.fubar.com/path1/path2",
              method.getURI().toString());
    } catch (URIException e) {
      fail("trouble getting URI: " + e);
    }
    assertEquals("Get Path", "/path1/path2", method.getPath());
    assertEquals("Get query string", null, method.getQuery());
  }

  public void testUrlGetMethod() {
    GET method = new GET("http://www.fubar.com/");
    try {
      assertEquals("Get URL",
              "http://www.fubar.com/",
              method.getURI().toString());
    } catch (URIException e) {
      fail("trouble getting URI: " + e);
    }
    assertEquals("Get Path", "/", method.getPath());
    assertEquals("Get query string", null, method.getQuery());

  }


  public void testUrlGetMethodWithInvalidProtocol() {
    try {
      new GET("crap://www.fubar.com/");
      fail("The use of invalid net must have resulted in an IllegalStateException");
    } catch (IllegalStateException is_OK) {
      // $JL-EXC$
    }
  }
}
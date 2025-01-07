package com.sap.httpclient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.sap.httpclient.auth.AuthScope;
import com.sap.httpclient.auth.UserPassCredentials;
import com.sap.httpclient.http.methods.InputStreamRequestData;
import com.sap.httpclient.http.methods.POST;
import com.sap.httpclient.http.methods.RequestData;
import com.sap.httpclient.http.methods.StringRequestData;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.http.HttpVersion;
import com.sap.httpclient.http.HttpStatus;
import com.sap.httpclient.server.AuthRequestHandler;
import com.sap.httpclient.server.HttpRequestHandlerChain;
import com.sap.httpclient.server.HttpService;
import com.sap.httpclient.server.HttpServiceHandler;
import com.sap.httpclient.server.SimpleRequest;
import com.sap.httpclient.server.SimpleResponse;
import com.sap.httpclient.exception.ProtocolException;

/**
 * Tests specific to entity enclosing methods.
 */
public class TestEntityEnclosingMethod extends HttpClientTestBase {

  public TestEntityEnclosingMethod(String testName) {
    super(testName);
  }

  public static Test suite() {
		return new TestSuite(TestEntityEnclosingMethod.class);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestEntityEnclosingMethod.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public void testEnclosedEntityAutoLength() throws Exception {
    String inputstr = "This is a test message";
    byte[] input = inputstr.getBytes("US-ASCII");
    InputStream instream = new ByteArrayInputStream(input);
    RequestData requestentity = new InputStreamRequestData(instream, InputStreamRequestData.CONTENT_LENGTH_AUTO);
    POST method = new POST("/");
    method.setRequestData(requestentity);
    this.server.setHttpService(new EchoService());
    try {
      this.client.executeMethod(method);
      assertEquals(200, method.getStatusCode());
      String body = method.getResponseBodyAsString();
      assertEquals(inputstr, body);
      assertNull(method.getRequestHeader("Transfer-Encoding"));
      assertNotNull(method.getRequestHeader("Content-Length"));
      assertEquals(input.length, Integer.parseInt(method.getRequestHeader("Content-Length").getValue()));
    } finally {
      method.releaseConnection();
    }
  }

  public void testEnclosedEntityExplicitLength() throws Exception {
    String inputstr = "This is a test message";
    byte[] input = inputstr.getBytes("US-ASCII");
    InputStream instream = new ByteArrayInputStream(input);
    RequestData requestentity = new InputStreamRequestData(instream, 14);
    POST method = new POST("/");
    method.setRequestData(requestentity);
    this.server.setHttpService(new EchoService());
    try {
      this.client.executeMethod(method);
      assertEquals(200, method.getStatusCode());
      String body = method.getResponseBodyAsString();
      assertEquals("This is a test", body);
      assertNull(method.getRequestHeader("Transfer-Encoding"));
      assertNotNull(method.getRequestHeader("Content-Length"));
      assertEquals(14, Integer.parseInt(method.getRequestHeader("Content-Length").getValue()));
    } finally {
      method.releaseConnection();
    }
  }

  public void testEnclosedEntityChunked() throws Exception {
    String inputstr = "This is a test message";
    byte[] input = inputstr.getBytes("US-ASCII");
    InputStream instream = new ByteArrayInputStream(input);
    RequestData requestentity = new InputStreamRequestData(instream, InputStreamRequestData.CONTENT_LENGTH_AUTO);
    POST method = new POST("/");
    method.setRequestData(requestentity);
    method.setContentChunked(true);
    this.server.setHttpService(new EchoService());
    try {
      this.client.executeMethod(method);
      assertEquals(200, method.getStatusCode());
      String body = method.getResponseBodyAsString();
      assertEquals(inputstr, body);
      assertNotNull(method.getRequestHeader("Transfer-Encoding"));
      assertNull(method.getRequestHeader("Content-Length"));
    } finally {
      method.releaseConnection();
    }
  }

  public void testEnclosedEntityChunkedHTTP1_0() throws Exception {
    String inputstr = "This is a test message";
    byte[] input = inputstr.getBytes("US-ASCII");
    InputStream instream = new ByteArrayInputStream(input);
    RequestData requestentity = new InputStreamRequestData(instream, InputStreamRequestData.CONTENT_LENGTH_AUTO);
    POST method = new POST("/");
    method.setRequestData(requestentity);
    method.setContentChunked(true);
    method.getParams().setVersion(HttpVersion.HTTP_1_0);
    this.server.setHttpService(new EchoService());
    try {
      this.client.executeMethod(method);
      fail("ProtocolException should have been thrown");
    } catch (ProtocolException is_OK) {
      // $JL-EXC$
    } finally {
      method.releaseConnection();
    }
  }

  public void testEnclosedEntityRepeatable() throws Exception {
    String inputstr = "This is a test message";
    byte[] input = inputstr.getBytes("US-ASCII");
    InputStream instream = new ByteArrayInputStream(input);
    RequestData requestentity = new InputStreamRequestData(instream, InputStreamRequestData.CONTENT_LENGTH_AUTO);
    POST method = new POST("/");
    method.setRequestData(requestentity);
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds));
    handlerchain.appendHandler(new HttpServiceHandler(new EchoService()));
    this.server.setRequestHandler(handlerchain);
    this.client.getState().setCredentials(AuthScope.ANY, creds);
    try {
      this.client.executeMethod(method);
      assertEquals(200, method.getStatusCode());
      String body = method.getResponseBodyAsString();
      assertEquals(inputstr, body);
      assertNull(method.getRequestHeader("Transfer-Encoding"));
      assertNotNull(method.getRequestHeader("Content-Length"));
      assertEquals(input.length, Integer.parseInt(method.getRequestHeader("Content-Length").getValue()));
    } finally {
      method.releaseConnection();
    }
  }

  public void testEnclosedEntityNonRepeatable() throws Exception {
    String inputstr = "This is a test message";
    byte[] input = inputstr.getBytes("US-ASCII");
    InputStream instream = new ByteArrayInputStream(input);
    RequestData requestentity = new InputStreamRequestData(instream, InputStreamRequestData.CONTENT_LENGTH_AUTO);
    POST method = new POST("/");
    method.setRequestData(requestentity);
    method.setContentChunked(true);
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds));
    handlerchain.appendHandler(new HttpServiceHandler(new EchoService()));
    this.server.setRequestHandler(handlerchain);
    this.client.getState().setCredentials(AuthScope.ANY, creds);
    try {
      this.client.executeMethod(method);
      fail("ProtocolException should have been thrown");
    } catch (ProtocolException is_OK) {
      // $JL-EXC$
    } finally {
      method.releaseConnection();
    }
  }

  public void testEnclosedEntityNegativeLength() throws Exception {
    String inputstr = "This is a test message";
    byte[] input = inputstr.getBytes("US-ASCII");
    InputStream instream = new ByteArrayInputStream(input);
    RequestData requestentity = new InputStreamRequestData(instream, -14);
    POST method = new POST("/");
    method.setRequestData(requestentity);
    method.setContentChunked(false);
    this.server.setHttpService(new EchoService());
    try {
      this.client.executeMethod(method);
      assertEquals(200, method.getStatusCode());
      String body = method.getResponseBodyAsString();
      assertEquals(inputstr, body);
      assertNotNull(method.getRequestHeader("Transfer-Encoding"));
      assertNull(method.getRequestHeader("Content-Length"));
    } finally {
      method.releaseConnection();
    }
  }

  public void testEnclosedEntityNegativeLengthHTTP1_0() throws Exception {
    String inputstr = "This is a test message";
    byte[] input = inputstr.getBytes("US-ASCII");
    InputStream instream = new ByteArrayInputStream(input);
    RequestData requestentity = new InputStreamRequestData(instream, -14);
    POST method = new POST("/");
    method.setRequestData(requestentity);
    method.setContentChunked(false);
    method.getParams().setVersion(HttpVersion.HTTP_1_0);
    this.server.setHttpService(new EchoService());
    try {
      this.client.executeMethod(method);
      fail("ProtocolException should have been thrown");
    } catch (ProtocolException is_OK) {
      // $JL-EXC$
    } finally {
      method.releaseConnection();
    }
  }

  class RequestBodyStatsService implements HttpService {

    public RequestBodyStatsService() {
      super();
    }

    public boolean process(final SimpleRequest request, final SimpleResponse response) throws IOException {
      HttpVersion httpversion = request.getRequestLine().getHttpVersion();
      response.setStatusLine(httpversion, HttpStatus.SC_OK);
      response.addHeader(new Header("Content-Type", "text/plain"));
      StringBuilder buffer = new StringBuilder();
      buffer.append("Request bosy stats:\r\n");
      buffer.append("===================\r\n");
      long l = request.getContentLength();
      if (l >= 0) {
        buffer.append("Content-Length: ");
        buffer.append(l);
        buffer.append("\r\n");
      }
      Header te = request.getFirstHeader("Transfer-Encoding");
      if (te != null) {
        buffer.append("Content-Length: ");
        buffer.append(te.getValue());
        buffer.append("\r\n");
      }
      byte[] b = request.getBodyBytes();
      if (b.length <= 0) {
        buffer.append("No body submitted\r\n");
      }
      response.setBodyString(buffer.toString());
      return true;
    }
  }

  public void testEmptyPostMethod() throws Exception {
    this.server.setHttpService(new RequestBodyStatsService());
    POST method = new POST("/");
    method.setRequestHeader("Content-Type", "text/plain");
    this.client.executeMethod(method);
    assertEquals(200, method.getStatusLine().getStatusCode());
    String response = method.getResponseBodyAsString();
    assertNotNull(method.getRequestHeader("Content-Length"));
    assertTrue(response.indexOf("No body submitted") >= 0);
    method = new POST("/");
    method.setRequestHeader("Content-Type", "text/plain");
    method.setRequestData(new StringRequestData(""));
    this.client.executeMethod(method);
    assertEquals(200, method.getStatusLine().getStatusCode());
    assertNotNull(method.getRequestHeader("Content-Length"));
    response = method.getResponseBodyAsString();
    assertTrue(response.indexOf("No body submitted") >= 0);
    method = new POST("/");
    method.setRequestHeader("Content-Type", "text/plain");
    method.setContentChunked(true);
    this.client.executeMethod(method);
    assertEquals(200, method.getStatusLine().getStatusCode());
    assertNotNull(method.getRequestHeader("Content-Length"));
    response = method.getResponseBodyAsString();
    assertTrue(response.indexOf("No body submitted") >= 0);
    method = new POST("/");
    method.setRequestHeader("Content-Type", "text/plain");
    method.setRequestData(new StringRequestData(""));
    method.setContentChunked(true);
    this.client.executeMethod(method);
    assertNull(method.getRequestHeader("Content-Length"));
    assertNotNull(method.getRequestHeader("Transfer-Encoding"));
    assertEquals(200, method.getStatusLine().getStatusCode());
    response = method.getResponseBodyAsString();
    assertTrue(response.indexOf("No body submitted") >= 0);
  }

}
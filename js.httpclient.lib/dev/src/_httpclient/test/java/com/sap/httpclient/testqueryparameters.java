package com.sap.httpclient;

import java.io.IOException;

import junit.framework.*;
import com.sap.httpclient.http.methods.*;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.http.HttpVersion;
import com.sap.httpclient.http.HttpStatus;
import com.sap.httpclient.server.HttpService;
import com.sap.httpclient.server.SimpleRequest;
import com.sap.httpclient.server.SimpleResponse;
import com.sap.httpclient.uri.URI;

public class TestQueryParameters extends HttpClientTestBase {

  public TestQueryParameters(String testName) throws Exception {
    super(testName);
  }

  public static Test suite() {
		return new TestSuite(TestQueryParameters.class);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestQueryParameters.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  class QueryInfoService implements HttpService {

    public QueryInfoService() {
      super();
    }

    public boolean process(final SimpleRequest request, final SimpleResponse response) throws IOException {
      HttpVersion httpversion = request.getRequestLine().getHttpVersion();
      response.setStatusLine(httpversion, HttpStatus.SC_OK);
      response.addHeader(new Header("Content-Type", "text/plain"));
      URI uri = new URI(request.getRequestLine().getUri(), true);
      StringBuilder buffer = new StringBuilder();
      buffer.append("QueryString=\"");
      buffer.append(uri.getQuery());
      buffer.append("\"\r\n");
      response.setBodyString(buffer.toString());
      return true;
    }
  }

  /**
   * Test that {@link GET#setQuery(java.lang.String)} can include a leading question mark.
	 *
	 * @throws Exception if an exception occures
   */
  public void testGetMethodQueryString() throws Exception {
    this.server.setHttpService(new QueryInfoService());
    GET method = new GET("/");
    method.setQuery("?hadQuestionMark=true");
    try {
      this.client.executeMethod(method);
      assertEquals(200, method.getStatusCode());
      String response = method.getResponseBodyAsString();
      assertTrue(response.indexOf("QueryString=\"hadQuestionMark=true\"") >= 0);
    } finally {
      method.releaseConnection();
    }
  }

  /**
   * Test that {@link GET#setQuery(java.lang.String)} doesn't have to include a leading question mark.
	 *
	 * @throws Exception if an exception occures
   */
  public void testGetMethodQueryString2() throws Exception {
    this.server.setHttpService(new QueryInfoService());
    GET method = new GET("/");
    method.setQuery("hadQuestionMark=false");
    try {
      this.client.executeMethod(method);
      assertEquals(200, method.getStatusCode());
      String response = method.getResponseBodyAsString();
      assertTrue(response.indexOf("QueryString=\"hadQuestionMark=false\"") >= 0);
    } finally {
      method.releaseConnection();
    }
  }

  /**
   * Test that {@link GET#setQueryString(NameValuePair[])} values get added to the query string.
	 *
	 * @throws Exception if an exception occures
   */
  public void testGetMethodParameters() throws Exception {
    this.server.setHttpService(new QueryInfoService());
    GET method = new GET("/");
    method.setQueryString(new NameValuePair[]{new NameValuePair("param-one", "param-value")});
    try {
      this.client.executeMethod(method);
      assertEquals(200, method.getStatusCode());
      String response = method.getResponseBodyAsString();
      assertTrue(response.indexOf("QueryString=\"param-one=param-value\"") >= 0);
    } finally {
      method.releaseConnection();
    }
  }

  /**
   * Test that {@link GET#setQueryString(NameValuePair[])} works with multiple parameters.
	 *
	 * @throws Exception if an exception occures
   */
  public void testGetMethodMultiParameters() throws Exception {
    this.server.setHttpService(new QueryInfoService());
    GET method = new GET("/");
    method.setQueryString(new NameValuePair[]{
      new NameValuePair("param-one", "param-value"),
      new NameValuePair("param-two", "param-value2"),
      new NameValuePair("special-chars", ":/?~.")
    });
    try {
      this.client.executeMethod(method);
      assertEquals(200, method.getStatusCode());
      String response = method.getResponseBodyAsString();
      assertTrue(response.indexOf("QueryString=\"param-one=param-value&param-two=param-value2&special-chars=:/?~.\"") >= 0);
    } finally {
      method.releaseConnection();
    }
  }

  /**
   * Test that {@link GET#setQueryString(NameValuePair[])} works with a parameter name but no value.
	 *
	 * @throws Exception if an exception occures
   */
  public void testGetMethodParameterWithoutValue() throws Exception {
    this.server.setHttpService(new QueryInfoService());
    GET method = new GET("/");
    method.setQueryString(new NameValuePair[]{new NameValuePair("param-without-value", null)});
    try {
      this.client.executeMethod(method);
      assertEquals(200, method.getStatusCode());
      String response = method.getResponseBodyAsString();
      assertTrue(response.indexOf("QueryString=\"param-without-value=\"") >= 0);
    } finally {
      method.releaseConnection();
    }
  }

  /**
   * Test that {@link GET#setQueryString(NameValuePair[])} works with a parameter name that occurs more than once.
	 *
	 * @throws Exception if an exception occures
   */
  public void testGetMethodParameterAppearsTwice() throws Exception {
    this.server.setHttpService(new QueryInfoService());
    GET method = new GET("/");
    method.setQueryString(new NameValuePair[]{
      new NameValuePair("foo", "one"),
      new NameValuePair("foo", "two")
    });
    try {
      this.client.executeMethod(method);
      assertEquals(200, method.getStatusCode());
      String response = method.getResponseBodyAsString();
      assertTrue(response.indexOf("QueryString=\"foo=one&foo=two\"") >= 0);
    } finally {
      method.releaseConnection();
    }
  }

  public void testGetMethodOverwriteQueryString() throws Exception {
    this.server.setHttpService(new QueryInfoService());
    GET method = new GET("/");
    method.setQuery("query=string");
    method.setQueryString(new NameValuePair[]{
      new NameValuePair("param", "eter"),
      new NameValuePair("para", "meter")
    });
    try {
      this.client.executeMethod(method);
      assertEquals(200, method.getStatusCode());
      String response = method.getResponseBodyAsString();
      assertFalse(response.indexOf("QueryString=\"query=string\"") >= 0);
      assertTrue(response.indexOf("QueryString=\"param=eter&para=meter\"") >= 0);
    } finally {
      method.releaseConnection();
    }
  }

  /**
   * Test that {@link POST#addParameter(java.lang.String,java.lang.String)}
   * and {@link POST#setQuery(java.lang.String)} combine properly.
	 *
	 * @throws Exception if an exception occures
   */
  public void testPostMethodParameterAndQueryString() throws Exception {
    this.server.setHttpService(new QueryInfoService());
    POST method = new POST("/");
    method.setQuery("query=string");
    method.setRequestBody(new NameValuePair[]{
      new NameValuePair("param", "eter"),
      new NameValuePair("para", "meter")});
    try {
      this.client.executeMethod(method);
      assertEquals(200, method.getStatusCode());
      String response = method.getResponseBodyAsString();
      assertTrue(response.indexOf("QueryString=\"query=string\"") >= 0);
      assertFalse(response.indexOf("QueryString=\"param=eter&para=meter\"") >= 0);
    } finally {
      method.releaseConnection();
    }
  }
}
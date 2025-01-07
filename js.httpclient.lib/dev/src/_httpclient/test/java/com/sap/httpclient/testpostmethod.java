package com.sap.httpclient;

import junit.framework.*;
import com.sap.httpclient.http.methods.*;

import java.io.*;

/**
 * Webapp tests specific to the POST.
 */
public class TestPostMethod extends HttpClientTestBase {

  public TestPostMethod(String testName) {
    super(testName);
  }

  public static Test suite() {
		return new TestSuite(TestPostMethod.class);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestPostMethod.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  /**
   * Test that the body can be set as a array of parameters
	 *
	 * @throws Exception if an exception occures
   */
  public void testParametersBodyToParamServlet() throws Exception {
    POST method = new POST("/");
    NameValuePair[] parametersBody = new NameValuePair[]{
      new NameValuePair("pname1", "pvalue1"),
      new NameValuePair("pname2", "pvalue2")
    };
    method.setRequestBody(parametersBody);
    this.server.setHttpService(new EchoService());
    try {
      this.client.executeMethod(method);
      assertEquals(200, method.getStatusCode());
      String body = method.getResponseBodyAsString();
      assertEquals("pname1=pvalue1&pname2=pvalue2", body);
    } finally {
      method.releaseConnection();
    }
  }

  /**
   * Test that the body can be set as a String
	 *
	 * @throws Exception if an exception occures
   */
  public void testStringBodyToParamServlet() throws Exception {
    POST method = new POST("/");
    String stringBody = "pname1=pvalue1&pname2=pvalue2";
    method.setRequestData(new StringRequestData(stringBody, POST.FORM_URL_ENCODED_CONTENT_TYPE, null));
    this.server.setHttpService(new EchoService());
    try {
      this.client.executeMethod(method);
      assertEquals(200, method.getStatusCode());
      String body = method.getResponseBodyAsString();
      assertEquals("pname1=pvalue1&pname2=pvalue2", body);
    } finally {
      method.releaseConnection();
    }
  }

  /**
   * Test that the body can be set as a String without an explict content type
	 *
	 * @throws Exception if an exception occures
   */
  public void testStringBodyToBodyServlet() throws Exception {
    POST method = new POST("/");
    String stringBody = "pname1=pvalue1&pname2=pvalue2";
    method.setRequestData(new StringRequestData(stringBody));
    this.server.setHttpService(new EchoService());
    try {
      this.client.executeMethod(method);
      assertEquals(200, method.getStatusCode());
      String body = method.getResponseBodyAsString();
      assertEquals("pname1=pvalue1&pname2=pvalue2", body);
    } finally {
      method.releaseConnection();
    }
  }

  /**
   * Test that parameters can be added.
	 *
	 * @throws Exception if an exception occures
   */
  public void testAddParametersToParamServlet() throws Exception {
    POST method = new POST("/");
    method.addParameter(new NameValuePair("pname1", "pvalue1"));
    method.addParameter(new NameValuePair("pname2", "pvalue2"));
    this.server.setHttpService(new EchoService());
    try {
      this.client.executeMethod(method);
      assertEquals(200, method.getStatusCode());
      String body = method.getResponseBodyAsString();
      assertEquals("pname1=pvalue1&pname2=pvalue2", body);
    } finally {
      method.releaseConnection();
    }
  }

  /**
   * Test that parameters can be added and removed.
	 *
	 * @throws Exception if an exception occures
   */
  public void testAddRemoveParametersToParamServlet() throws Exception {
    POST method = new POST("/");
    method.addParameter(new NameValuePair("pname0", "pvalue0"));
    method.addParameter(new NameValuePair("pname1", "pvalue1"));
    method.addParameter(new NameValuePair("pname2", "pvalue2"));
    method.addParameter(new NameValuePair("pname3", "pvalue3"));
    method.removeParameter("pname0");
    method.removeParameter("pname3");
    this.server.setHttpService(new EchoService());
    try {
      this.client.executeMethod(method);
      assertEquals(200, method.getStatusCode());
      String body = method.getResponseBodyAsString();
      assertEquals("pname1=pvalue1&pname2=pvalue2", body);
    } finally {
      method.releaseConnection();
    }
  }

  /**
   * Test the return value of the POST#removeParameter.
	 *
	 * @throws Exception if an exception occures
   */
  public void testRemoveParameterReturnValue() throws Exception {
    POST method = new POST("/");
    method.addParameter("param", "whatever");
    assertTrue("Return value of the method is expected to be true", method.removeParameter("param"));
    assertFalse("Return value of the method is expected to be false", method.removeParameter("param"));
  }

  private String getRequestAsString(RequestData data) throws Exception {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    data.writeRequest(bos);
    return new String(bos.toByteArray(), "UTF-8");
  }

  /**
   * Test if setParameter overwrites existing parameter values.
	 *
	 * @throws Exception if an exception occures
   */
  public void testAddParameterFollowedBySetParameter() throws Exception {
    POST method = new POST("/");
    method.addParameter("param", "a");
    method.addParameter("param", "b");
    method.addParameter("param", "c");
    assertEquals("param=a&param=b&param=c", getRequestAsString(method.getRequesData()));
    method.setParameter("param", "a");
    assertEquals("param=a", getRequestAsString(method.getRequesData()));
  }

}
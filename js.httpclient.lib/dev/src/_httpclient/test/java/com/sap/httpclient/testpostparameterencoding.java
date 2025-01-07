package com.sap.httpclient;

import java.io.ByteArrayOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.sap.httpclient.http.methods.POST;
import com.sap.httpclient.http.methods.RequestData;
import com.sap.httpclient.http.methods.StringRequestData;

/**
 * Tests basic method functionality.
 */
public class TestPostParameterEncoding extends TestCase {

  static final String NAME = "name", VALUE = "value";
  static final String NAME0 = "name0", VALUE0 = "value0";
  static final String NAME1 = "name1", VALUE1 = "value1";
  static final String NAME2 = "name2", VALUE2 = "value2";

  static final NameValuePair PAIR = new NameValuePair(NAME, VALUE);
  static final NameValuePair PAIR0 = new NameValuePair(NAME0, VALUE0);
  static final NameValuePair PAIR1 = new NameValuePair(NAME1, VALUE1);
  static final NameValuePair PAIR2 = new NameValuePair(NAME2, VALUE2);

  public TestPostParameterEncoding(final String testName) {
    super(testName);
  }

  public static Test suite() {
    return new TestSuite(TestPostParameterEncoding.class);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestPostParameterEncoding.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  private String getRequestAsString(RequestData data) throws Exception {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    data.writeRequest(bos);
    return new String(bos.toByteArray(), "UTF-8");
  }

  public void testPostParametersEncoding() throws Exception {
    POST post = new POST();
    post.setRequestBody(new NameValuePair[]{PAIR});
    assertEquals("name=value", getRequestAsString(post.getRequesData()));

    post.setRequestBody(new NameValuePair[]{PAIR, PAIR1, PAIR2});
    assertEquals("name=value&name1=value1&name2=value2", getRequestAsString(post.getRequesData()));

    post.setRequestBody(new NameValuePair[]{PAIR, PAIR1, PAIR2, new NameValuePair("hasSpace", "a b c d")});
    assertEquals("name=value&name1=value1&name2=value2&hasSpace=a+b+c+d", getRequestAsString(post.getRequesData()));

    post.setRequestBody(new NameValuePair[]{new NameValuePair("escaping", ",.-\u00f6\u00e4\u00fc!+@#*&()=?:;}{[]$")});
    assertEquals("escaping=%2C.-%F6%E4%FC%21%2B%40%23*%26%28%29%3D%3F%3A%3B%7D%7B%5B%5D%24",
            getRequestAsString(post.getRequesData()));

  }

  public void testPostSetRequestBody() throws Exception {
    POST post = new POST("/foo");
    String body = "this+is+the+body";
    post.setRequestData(new StringRequestData(body));
    assertEquals(body, getRequestAsString(post.getRequesData()));
  }

}
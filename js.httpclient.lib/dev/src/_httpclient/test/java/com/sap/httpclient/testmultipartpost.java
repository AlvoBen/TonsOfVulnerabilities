package com.sap.httpclient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.sap.httpclient.http.methods.POST;
import com.sap.httpclient.http.methods.multipart.ByteArrayPartSource;
import com.sap.httpclient.http.methods.multipart.FilePart;
import com.sap.httpclient.http.methods.multipart.MultipartRequestData;
import com.sap.httpclient.http.methods.multipart.Part;
import com.sap.httpclient.http.methods.multipart.PartSource;
import com.sap.httpclient.http.methods.multipart.StringPart;

/**
 * Webapp tests specific to the MultiPostMethod.
 */
public class TestMultipartPost extends HttpClientTestBase {

  public TestMultipartPost(final String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(TestMultipartPost.class);
    ProxyTestDecorator.addTests(suite);
    return suite;
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestMultipartPost.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  /**
   * Test that the body consisting of a string part can be posted.
	 *
	 * @throws Exception if an exception occures
   */
  public void testPostStringPart() throws Exception {
    this.server.setHttpService(new EchoService());
    POST method = new POST();
    MultipartRequestData entity = new MultipartRequestData(new Part[]{new StringPart("param", "Hello", "ISO-8859-1")},
            method.getParams());
    method.setRequestData(entity);
    client.executeMethod(method);
    assertEquals(200, method.getStatusCode());
    String body = method.getResponseBodyAsString();
    assertTrue(body.indexOf("Content-Disposition: form-data; name=\"param\"") >= 0);
    assertTrue(body.indexOf("Content-Type: text/plain; charset=ISO-8859-1") >= 0);
    assertTrue(body.indexOf("Content-Transfer-Encoding: 8bit") >= 0);
    assertTrue(body.indexOf("Hello") >= 0);
  }


  /**
   * Test that the body consisting of a file part can be posted.
	 *
	 * @throws Exception if an exception occures
   */
  public void testPostFilePart() throws Exception {
    this.server.setHttpService(new EchoService());
    POST method = new POST();
    byte[] content = "Hello".getBytes();
    MultipartRequestData entity = new MultipartRequestData(new Part[]{
      new FilePart("param1",
              new ByteArrayPartSource("filename.txt", content),
              "text/plain",
              "ISO-8859-1")},
            method.getParams());
    method.setRequestData(entity);
    client.executeMethod(method);
    assertEquals(200, method.getStatusCode());
    String body = method.getResponseBodyAsString();
    assertTrue(body.indexOf("Content-Disposition: form-data; name=\"param1\"; filename=\"filename.txt\"") >= 0);
    assertTrue(body.indexOf("Content-Type: text/plain; charset=ISO-8859-1") >= 0);
    assertTrue(body.indexOf("Content-Transfer-Encoding: binary") >= 0);
    assertTrue(body.indexOf("Hello") >= 0);
  }

  /**
   * Test that the body consisting of a file part of unknown length can be posted.
   */

  public class TestPartSource implements PartSource {
    private String fileName;
    private byte[] data;

    public TestPartSource(String fileName, byte[] data) {
      this.fileName = fileName;
      this.data = data;
    }

    public long getLength() {
      return -1;
    }

    public String getFileName() {
      return fileName;
    }

    public InputStream createInputStream() throws IOException {
      return new ByteArrayInputStream(data);
    }

  }

  public void testPostFilePartUnknownLength() throws Exception {
    this.server.setHttpService(new EchoService());
    String enc = "ISO-8859-1";
    POST method = new POST();
    byte[] content = "Hello".getBytes(enc);
    MultipartRequestData entity = new MultipartRequestData(new Part[]{
      new FilePart("param1",
              new TestPartSource("filename.txt", content),
              "text/plain",
              enc)},
            method.getParams());
    method.setRequestData(entity);
    client.executeMethod(method);
    assertEquals(200, method.getStatusCode());
    String body = method.getResponseBodyAsString();
    assertTrue(body.indexOf("Content-Disposition: form-data; name=\"param1\"; filename=\"filename.txt\"") >= 0);
    assertTrue(body.indexOf("Content-Type: text/plain; charset=" + enc) >= 0);
    assertTrue(body.indexOf("Content-Transfer-Encoding: binary") >= 0);
    assertTrue(body.indexOf("Hello") >= 0);
  }

}
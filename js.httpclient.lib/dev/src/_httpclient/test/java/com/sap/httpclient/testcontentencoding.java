package com.sap.httpclient;

import java.io.*;
import java.util.zip.GZIPOutputStream;
import java.util.zip.DeflaterOutputStream;

import junit.framework.Test;
import junit.framework.TestSuite;
import com.sap.httpclient.http.methods.GET;
import com.sap.httpclient.server.HttpService;
import com.sap.httpclient.server.RequestLine;
import com.sap.httpclient.server.SimpleRequest;
import com.sap.httpclient.server.SimpleResponse;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.http.HttpVersion;
import com.sap.httpclient.http.HttpStatus;

/**
 * ContentEncoding test cases.
 */
public class TestContentEncoding extends HttpClientTestBase {

  private final static String GZIP_DATA = "This data will be gzip encoded";
  private final static String DEFLATE_DATA = "This data will be deflate encoded";


  public TestContentEncoding(final String testName) {
    super(testName);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestContentEncoding.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public static Test suite() {
    return new TestSuite(TestContentEncoding.class);
  }

  private class GzipContentResponse implements HttpService {

    public boolean process(final SimpleRequest request, final SimpleResponse response) throws IOException {
      RequestLine reqline = request.getRequestLine();
      HttpVersion ver = reqline.getHttpVersion();
      response.setStatusLine(ver, HttpStatus.SC_OK);
      response.setHeader(new Header(Header._CONTENT_ENCODING, "gzip"));
      response.setHeader(new Header("Connection", "close"));
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      GZIPOutputStream os = new GZIPOutputStream(bos);
      os.write(GZIP_DATA.getBytes());
      os.finish();
      response.setBody(new ByteArrayInputStream(bos.toByteArray()));
      return true;
    }
  }

 private class DeflateContentResponse implements HttpService {

    public boolean process(final SimpleRequest request, final SimpleResponse response) throws IOException {
      RequestLine reqline = request.getRequestLine();
      HttpVersion ver = reqline.getHttpVersion();
      response.setStatusLine(ver, HttpStatus.SC_OK);
      response.setHeader(new Header(Header._CONTENT_ENCODING, "deflate"));
      response.setHeader(new Header("Connection", "close"));

      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      DeflaterOutputStream os = new DeflaterOutputStream(bos);
      os.write(DEFLATE_DATA.getBytes());
      os.finish();
      response.setBody(new ByteArrayInputStream(bos.toByteArray()));
      return true;
    }
  }

  public void testGzipContentResponse() throws IOException {
    this.server.setHttpService(new GzipContentResponse());
    GET httpget = new GET();
    try {
      this.client.executeMethod(httpget);
      assertEquals(HttpStatus.SC_OK, httpget.getStatusCode());
      assertEquals(httpget.getResponseBodyAsString(),GZIP_DATA);
    } finally {
      httpget.releaseConnection();
    }
  }

  public void testDeflateContentResponse() throws IOException {
    this.server.setHttpService(new DeflateContentResponse());
    GET httpget = new GET();
    try {
      this.client.executeMethod(httpget);
      assertEquals(HttpStatus.SC_OK, httpget.getStatusCode());
      assertEquals(httpget.getResponseBodyAsString(),DEFLATE_DATA);
    } finally {
      httpget.releaseConnection();
    }
  }

}

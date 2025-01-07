package com.sap.httpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.sap.httpclient.http.methods.GET;
import com.sap.httpclient.http.HttpVersion;
import com.sap.httpclient.server.HttpRequestHandler;
import com.sap.httpclient.server.ResponseWriter;
import com.sap.httpclient.server.SimpleHttpServerConnection;
import com.sap.httpclient.server.SimpleRequest;

/**
 * Tests ability to abort method execution.
 */
public class TestMethodAbort extends HttpClientTestBase {

  public TestMethodAbort(final String testName) {
    super(testName);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestMethodAbort.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public static Test suite() {
    return new TestSuite(TestMethodAbort.class);
  }

  private class ProduceGarbageHandler implements HttpRequestHandler {

    public ProduceGarbageHandler() {
      super();
    }

    public boolean processRequest(final SimpleHttpServerConnection conn,
                                  final SimpleRequest request) throws IOException {

      final String garbage = "garbage!\r\n";
      final long count = 1000000000;
      HttpVersion httpversion = request.getRequestLine().getHttpVersion();
      ResponseWriter out = conn.getWriter();
      out.println(httpversion + " 200 OK");
      out.println("Content-Type: text/plain");
      out.println("Content-Length: " + count * garbage.length());
      out.println("Connection: close");
      out.println();
      for (int i = 0; i < count; i++) {
        out.print(garbage);
      }
      return true;
    }
  }

  public void testAbortMethod() throws IOException {
    this.server.setRequestHandler(new ProduceGarbageHandler());
    final GET httpget = new GET("/test/");
    Thread thread = new Thread(new Runnable() {
      public void run() {
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          // $JL-EXC$
        }
        httpget.abort();
      }

    });
    thread.setDaemon(true);
    thread.start();
    try {
      this.client.executeMethod(httpget);
      BufferedReader in = new BufferedReader(new InputStreamReader(httpget.getResponseBodyAsStream()));
      while ((in.readLine()) != null) {
      }
      fail("IOException must have been thrown");
    } catch (IOException is_OK) {
      // $JL-EXC$
    } finally {
      httpget.releaseConnection();
    }
    assertTrue(httpget.isAborted());
  }

  public void testAbortedMethodExecute() throws IOException {
    final GET httpget = new GET("/test/");
    try {
      httpget.abort();
      try {
        this.client.executeMethod(httpget);
        fail("IllegalStateException must have been thrown");
      } catch (IllegalStateException is_OK) {
        // $JL-EXC$
      }
    } finally {
      httpget.releaseConnection();
    }
    assertTrue(httpget.isAborted());
  }
}
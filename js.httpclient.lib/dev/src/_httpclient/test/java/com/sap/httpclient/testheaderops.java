package com.sap.httpclient;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.sap.httpclient.http.methods.GET;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.http.HttpVersion;
import com.sap.httpclient.http.HttpStatus;
import com.sap.httpclient.net.Protocol;
import com.sap.httpclient.server.HttpService;
import com.sap.httpclient.server.SimpleRequest;
import com.sap.httpclient.server.SimpleResponse;

public class TestHeaderOps extends HttpClientTestBase {

  public TestHeaderOps(String testName) throws Exception {
    super(testName);
  }

  public static Test suite() {
		return new TestSuite(TestHeaderOps.class);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestHeaderOps.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  class HeaderDumpService implements HttpService {

    public HeaderDumpService() {
      super();
    }

    public boolean process(final SimpleRequest request, final SimpleResponse response) throws IOException {
      HttpVersion httpversion = request.getRequestLine().getHttpVersion();
      response.setStatusLine(httpversion, HttpStatus.SC_OK);
      response.addHeader(new Header("Content-Type", "text/plain"));
      response.addHeader(new Header("HeaderSetByServlet", "Yes"));
      StringBuilder buffer = new StringBuilder();
      buffer.append("Request headers: \r\n");
      for (Iterator i = request.getHeaderIterator(); i.hasNext();) {
        Header header = (Header) i.next();
        buffer.append("name=\"");
        buffer.append(header.getName().toLowerCase());
        buffer.append("\";value=\"");
        buffer.append(header.getValue());
        buffer.append("\"\r\n");
      }
      response.setBodyString(buffer.toString());
      return true;
    }
  }

  /**
   * Test {@link HttpMethod#addRequestHeader}.
	 *
	 * @throws Exception if an exception occures
	 */
  public void testAddRequestHeader() throws Exception {
    this.server.setHttpService(new HeaderDumpService());
    GET method = new GET("/");
    method.setRequestHeader(new Header("addRequestHeader(Header)", "True"));
    method.setRequestHeader("addRequestHeader(String,String)", "Also True");
    try {
      this.client.executeMethod(method);
      String s = method.getResponseBodyAsString();
      assertTrue(s.indexOf("name=\"addrequestheader(header)\";value=\"True\"") >= 0);
      assertTrue(s.indexOf("name=\"addrequestheader(string,string)\";value=\"Also True\"") >= 0);
    } finally {
      method.releaseConnection();
    }
  }

  /**
   * Test {@link HttpMethod#removeRequestHeader}.
	 *
	 * @throws Exception if an exception occures
   */
  public void testRemoveRequestHeader() throws Exception {
    this.server.setHttpService(new HeaderDumpService());
    GET method = new GET("/");
    method.setRequestHeader(new Header("XXX-A-HEADER", "true"));
    method.removeRequestHeader("XXX-A-HEADER");
    try {
      this.client.executeMethod(method);
      String s = method.getResponseBodyAsString();
      assertTrue(!(s.indexOf("xxx-a-header") >= 0));
    } finally {
      method.releaseConnection();
    }
  }

  /**
   * Test {@link HttpMethod#setRequestHeader}.
	 *
	 * @throws Exception if an exception occures
   */
  public void testOverwriteRequestHeader() throws Exception {
    this.server.setHttpService(new HeaderDumpService());
    GET method = new GET("/");
    method.setRequestHeader(new Header("xxx-a-header", "one"));
    method.setRequestHeader("XXX-A-HEADER", "two");
    try {
      this.client.executeMethod(method);
      String s = method.getResponseBodyAsString();
      assertTrue(s.indexOf("name=\"xxx-a-header\";value=\"two\"") >= 0);
    } finally {
      method.releaseConnection();
    }
  }

  /**
   * Test {@link HttpMethod#getResponseHeader}.
	 *
	 * @throws Exception if an exception occures
   */
  public void testGetResponseHeader() throws Exception {
    this.server.setHttpService(new HeaderDumpService());
    GET method = new GET("/");
    try {
      this.client.executeMethod(method);
      Header h = new Header("HeaderSetByServlet", "Yes");
      assertEquals(h, method.getResponseHeader("headersetbyservlet"));
    } finally {
      method.releaseConnection();
    }
  }

  public void testHostRequestHeader() throws Exception {
    this.server.setHttpService(new HeaderDumpService());
    String hostname = this.server.getLocalAddress();
    int port = this.server.getLocalPort();
    InetAddress addr = InetAddress.getByName(hostname);
    String ip = addr.getHostAddress();
    GET get = new GET("/");
    // Open connection using IP.  Host header should be sent
    // Note: RFC 2616 is somewhat unclear on whether a host should
    // be sent in this context - however, both Mozilla and IE send
    // the header for an IP address, instead of sending blank.
    this.client.getHostConfiguration().setHost(ip, port);
    try {
      this.client.executeMethod(get);
      Header hostHeader = get.getRequestHeader("Host");
      assertTrue(hostHeader != null);
      if (port == Protocol.getProtocol("http").getDefaultPort()) {
        // no port information should be in the value
        assertTrue(hostHeader.getValue().equals(ip));
      } else {
        assertTrue(hostHeader.getValue().equals(ip + ":" + port));
      }
    } finally {
      get.releaseConnection();
    }
    get = new GET("/");
    // Open connection using Host.  Host header should
    // contain this value (this test will fail if DNS
    // is not available. Additionally, if the port is
    // something other that 80, then the port value
    // should also be present in the header.
    this.client.getHostConfiguration().setHost(hostname, port);
    try {
      this.client.executeMethod(get);
      Header hostHeader = get.getRequestHeader("Host");
      assertTrue(hostHeader != null);
      if (port == Protocol.getProtocol("http").getDefaultPort()) {
        // no port information should be in the value
        assertTrue(hostHeader.getValue().equals(hostname));
      } else {
        assertTrue(hostHeader.getValue().equals(hostname + ":" + port));
      }
    } finally {
      get.releaseConnection();
    }
  }
}
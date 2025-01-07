package com.sap.httpclient.server;

import java.io.IOException;
import java.net.UnknownHostException;

import com.sap.httpclient.http.Header;
import com.sap.httpclient.http.HttpVersion;
import com.sap.httpclient.exception.HttpException;
import com.sap.httpclient.http.HttpStatus;
import com.sap.httpclient.uri.URI;
import com.sap.httpclient.exception.URIException;
import com.sap.tc.logging.Location;

public class ProxyRequestHandler implements HttpRequestHandler {

  private static final Location LOG = Location.getLocation(ProxyRequestHandler.class);

  private SimpleConnManager connmanager = null;

  public ProxyRequestHandler(final SimpleConnManager connmanager) {
    super();
    if (connmanager == null) {
      throw new IllegalArgumentException("Connection manager may not be null");
    }
    this.connmanager = connmanager;
  }

  /**
   */
  public boolean processRequest(final SimpleHttpServerConnection conn,
                                final SimpleRequest request) throws IOException {
    httpProxy(conn, request);
    return true;
  }

  private void httpProxy(final SimpleHttpServerConnection conn,
                         final SimpleRequest request) throws IOException {

    RequestLine oldreqline = request.getRequestLine();
    URI uri;
		SimpleHost host;
    try {
      uri = new URI(oldreqline.getUri(), true);
      host = new SimpleHost(uri.getHost(), uri.getPort());
    } catch (URIException ex) {
      SimpleResponse response = ErrorResponse.getResponse(HttpStatus.SC_BAD_REQUEST);
      conn.writeResponse(response);
      return;
    }
    SimpleHttpServerConnection proxyconn;
    try {
      proxyconn = this.connmanager.openConnection(host);
    } catch (UnknownHostException e) {
      SimpleResponse response = ErrorResponse.getResponse(HttpStatus.SC_NOT_FOUND);
      conn.writeResponse(response);
      return;
    }
    try {
      proxyconn.setSocketTimeout(0);
      // Rewrite target url
      RequestLine newreqline = new RequestLine(oldreqline.getMethod(),
              uri.getEscapedPath(),
              oldreqline.getHttpVersion());
      request.setRequestLine(newreqline);
      // Remove proxy-auth headers if present
      request.removeHeaders("Proxy-Authorization");
      // Manage connection persistence
      Header connheader = request.getFirstHeader("Proxy-Connection");
      if (connheader != null) {
        if (connheader.getValue().equalsIgnoreCase("close")) {
          request.setHeader(new Header("Connection", "close"));
        }
      }
      request.removeHeaders("Proxy-Connection");

      proxyconn.writeRequest(request);

      SimpleResponse response = proxyconn.readResponse();
      if (response == null) {
        return;
      }
      response.setHeader(new Header("Via", "1.1 test (Test-Proxy)"));
      connheader = response.getFirstHeader("Connection");
      if (connheader != null) {
        String s = connheader.getValue();
        if (s.equalsIgnoreCase("close")) {
          response.setHeader(new Header("Proxy-Connection", "close"));
          conn.setKeepAlive(false);
          proxyconn.setKeepAlive(false);
          response.removeHeaders("Connection");
        }
        if (s.equalsIgnoreCase("keep-alive")) {
          response.setHeader(new Header("Proxy-Connection", "keep-alive"));
          conn.setKeepAlive(true);
          proxyconn.setKeepAlive(true);
          response.removeHeaders("Connection");
        }
      } else {
        // Use net default connection policy
        if (response.getHttpVersion().greaterEquals(HttpVersion.HTTP_1_1)) {
          conn.setKeepAlive(true);
          proxyconn.setKeepAlive(true);
        } else {
          conn.setKeepAlive(false);
          proxyconn.setKeepAlive(false);
        }
      }
      if ("HEAD".equalsIgnoreCase(request.getRequestLine().getMethod())) {
        // this is a head request, we don't want to send the actualy content
        response.setBody(null);
      }
      conn.writeResponse(response);

    } catch (HttpException e) {
      SimpleResponse response = ErrorResponse.getResponse(HttpStatus.SC_BAD_REQUEST);
      conn.writeResponse(response);
      proxyconn.setKeepAlive(false);
    } catch (IOException e) {
      LOG.warningT(e.getMessage());
      proxyconn.setKeepAlive(false);
    } finally {
      this.connmanager.releaseConnection(host, proxyconn);
    }
  }

}
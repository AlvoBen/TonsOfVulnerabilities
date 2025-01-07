package com.sap.httpclient.server;

import java.io.IOException;
import java.io.InputStream;

import com.sap.httpclient.http.Header;
import com.sap.httpclient.http.HttpVersion;

/**
 * This request handler provides service interface similar to that of Servlet API.
 */
public class HttpServiceHandler implements HttpRequestHandler {

  private HttpService service = null;

  public HttpServiceHandler(final HttpService service) {
    super();
    if (service == null) {
      throw new IllegalArgumentException("Service may not be null");
    }
    this.service = service;
  }

  public boolean processRequest(final SimpleHttpServerConnection conn,
                                final SimpleRequest request) throws IOException {
    if (conn == null) throw new IllegalArgumentException("Connection may not be null");
    if (request == null) throw new IllegalArgumentException("Request may not be null");
    SimpleResponse response = new SimpleResponse();
    this.service.process(request, response);
    // Nake sure the request if fully consumed
    request.getBodyBytes();
    // Ensure there's a content type header
    if (!response.containsHeader("Content-Type")) {
      response.addHeader(new Header("Content-Type", "text/plain"));
    }
    // Ensure there's a content length or transfer encoding header
    if (!response.containsHeader("Content-Length") && !response.containsHeader("Transfer-Encoding")) {
      InputStream content = response.getBody();
      if (content != null) {
        long len = response.getContentLength();
        if (len < 0) {
          if (response.getHttpVersion().lessEquals(HttpVersion.HTTP_1_0)) {
            throw new IOException("Chunked encoding not supported for HTTP version "
                    + response.getHttpVersion());
          }
          Header header = new Header("Transfer-Encoding", "chunked");
          response.addHeader(header);
        } else {
          Header header = new Header("Content-Length", Long.toString(len));
          response.setHeader(header);
        }
      } else {
        Header header = new Header("Content-Length", "0");
        response.addHeader(header);
      }
    }
    if (!response.containsHeader("Connection")) {
      // See if the the client explicitly handles connection persistence
      Header connheader = request.getFirstHeader("Connection");
      if (connheader != null) {
        if (connheader.getValue().equalsIgnoreCase("keep-alive")) {
          Header header = new Header("Connection", "keep-alive");
          response.addHeader(header);
          conn.setKeepAlive(true);
        }
        if (connheader.getValue().equalsIgnoreCase("close")) {
          Header header = new Header("Connection", "close");
          response.addHeader(header);
          conn.setKeepAlive(false);
        }
      } else {
        // Use net default connection policy
        if (response.getHttpVersion().greaterEquals(HttpVersion.HTTP_1_1)) {
          conn.setKeepAlive(true);
        } else {
          conn.setKeepAlive(false);
        }
      }
    }
    if ("HEAD".equalsIgnoreCase(request.getRequestLine().getMethod())) {
      // this is a head request, we don't want to send the actualy content
      response.setBody(null);
    }
    conn.writeResponse(response);
    return true;
  }

}
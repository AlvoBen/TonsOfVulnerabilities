package com.sap.httpclient;

import java.io.IOException;

import com.sap.httpclient.server.HttpService;
import com.sap.httpclient.server.SimpleRequest;
import com.sap.httpclient.server.SimpleResponse;
import com.sap.httpclient.http.HttpVersion;
import com.sap.httpclient.http.HttpStatus;

/**
 * A service that echos the request body.
 */
public class EchoService implements HttpService {

  public EchoService() {
    super();
  }

  public boolean process(final SimpleRequest request, final SimpleResponse response) throws IOException {
    HttpVersion httpversion = request.getRequestLine().getHttpVersion();
    response.setStatusLine(httpversion, HttpStatus.SC_OK);
    if (request.containsHeader("Content-Length")) {
      response.addHeader(request.getFirstHeader("Content-Length"));
    }
    if (request.containsHeader("Content-Type")) {
      response.addHeader(request.getFirstHeader("Content-Type"));
    }
    response.setBodyString(request.getBodyString());
    return true;
  }
}
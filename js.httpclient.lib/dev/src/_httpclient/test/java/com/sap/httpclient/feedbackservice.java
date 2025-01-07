package com.sap.httpclient;

import java.io.IOException;
import java.io.StringWriter;

import com.sap.httpclient.server.HttpService;
import com.sap.httpclient.server.RequestLine;
import com.sap.httpclient.server.SimpleRequest;
import com.sap.httpclient.server.SimpleResponse;
import com.sap.httpclient.http.HttpVersion;
import com.sap.httpclient.http.HttpStatus;

public class FeedbackService implements HttpService {

  public FeedbackService() {
    super();
  }

  public boolean process(final SimpleRequest request, final SimpleResponse response) throws IOException {
    RequestLine requestline = request.getRequestLine();
    HttpVersion httpversion = requestline.getHttpVersion();
    StringWriter buffer = new StringWriter(100);
    buffer.write("Method type: ");
    buffer.write(requestline.getMethod());
    buffer.write("\r\n");
    buffer.write("Requested resource: ");
    buffer.write(requestline.getUri());
    buffer.write("\r\n");
    buffer.write("Protocol version: ");
    buffer.write(httpversion.toString());
    buffer.write("\r\n");
    String requestbody = request.getBodyString();
    if (requestbody != null && !requestbody.equals("")) {
      buffer.write("\r\n");
      buffer.write("Request body: ");
      buffer.write(requestbody);
      buffer.write("\r\n");
    }
    response.setStatusLine(httpversion, HttpStatus.SC_OK);
    response.setBodyString(buffer.toString());
    return true;
  }
}
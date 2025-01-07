package com.sap.httpclient.server;

import java.util.HashMap;

import com.sap.httpclient.http.Header;
import com.sap.httpclient.http.HttpStatus;
import com.sap.httpclient.http.HttpVersion;

/**
 * Default error responses.
 */
public class ErrorResponse {

  private static final HashMap<Integer, SimpleResponse> responses = new HashMap<Integer, SimpleResponse>();

  private ErrorResponse() {
    super();
  }

  public static SimpleResponse getResponse(int statusCode) {
		SimpleResponse response = responses.get(statusCode);
    if (response == null) {
      response = new SimpleResponse();
      response.setStatusLine(HttpVersion.HTTP_1_0, statusCode);
      response.setHeader(new Header("Content-Type", "text/plain; charset=US-ASCII"));
      String s = HttpStatus.getReasonPhrase(statusCode);
      if (s == null) {
        s = "Error " + statusCode;
      }
      response.setBodyString(s);
      response.addHeader(new Header("Connection", "close"));
      response.addHeader(new Header("Content-Lenght", Integer.toString(s.length())));
      responses.put(statusCode, response);
    }
    return response;
  }
}
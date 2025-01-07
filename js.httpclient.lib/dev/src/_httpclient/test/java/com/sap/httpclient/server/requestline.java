package com.sap.httpclient.server;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import com.sap.httpclient.exception.HttpException;
import com.sap.httpclient.http.HttpVersion;
import com.sap.httpclient.exception.ProtocolException;

/**
 * Defines a HTTP request-line, consisting of method name, URI and net.
 * Instances of this class are immutable.
 */
public class RequestLine {

  private HttpVersion httpversion = null;
  private String method = null;
  private String uri = null;

  public static RequestLine parseLine(final String l) throws HttpException {
    String method;
    String uri;
    String protocol;
    try {
      StringTokenizer st = new StringTokenizer(l, " ");
      method = st.nextToken();
      uri = st.nextToken();
      protocol = st.nextToken();
    } catch (NoSuchElementException e) {
      throw new ProtocolException("Invalid request line: " + l);
    }
    return new RequestLine(method, uri, protocol);
  }

  public RequestLine(final String method, final String uri, final HttpVersion httpversion) {
    super();
    if (method == null) {
      throw new IllegalArgumentException("Method may not be null");
    }
    if (uri == null) {
      throw new IllegalArgumentException("URI may not be null");
    }
    if (httpversion == null) {
      throw new IllegalArgumentException("HTTP version may not be null");
    }
    this.method = method;
    this.uri = uri;
    this.httpversion = httpversion;
  }

  public RequestLine(final String method, final String uri, final String httpversion)
          throws ProtocolException {
    this(method, uri, HttpVersion.parse(httpversion));
  }

  public String getMethod() {
    return this.method;
  }

  public HttpVersion getHttpVersion() {
    return this.httpversion;
  }

  public String getUri() {
    return this.uri;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.method);
    sb.append(" ");
    sb.append(this.uri);
    sb.append(" ");
    sb.append(this.httpversion);
    return sb.toString();
  }
}
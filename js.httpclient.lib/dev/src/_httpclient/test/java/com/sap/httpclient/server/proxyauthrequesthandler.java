﻿package com.sap.httpclient.server;

import java.io.IOException;

import com.sap.httpclient.auth.Credentials;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.http.HttpStatus;
import com.sap.httpclient.auth.UserPassCredentials;
import com.sap.httpclient.auth.BasicScheme;

/**
 * This request handler guards access to a proxy when used in a request handler
 * chain. It checks the headers for valid credentials and performs the
 * authentication handshake if necessary.
 */
public class ProxyAuthRequestHandler implements HttpRequestHandler {

  private Credentials credentials = null;
  private String realm = null;
  private boolean keepalive = true;

  /**
   * The proxy authenticate response header.
   */
  public static final String PROXY_AUTH_RESP = "Proxy-Authorization";

  /**
   * Encapsulating all required information for a specific scheme
   *
   * @param creds the credentials
	 * @param realm the realm
	 * @param keepalive the keep alive setting
   */
  public ProxyAuthRequestHandler(final Credentials creds, final String realm, boolean keepalive) {
    if (creds == null) throw new IllegalArgumentException("Credentials may not be null");
    this.credentials = creds;
    this.keepalive = keepalive;
    if (realm != null) {
      this.realm = realm;
    } else {
      this.realm = "test";
    }
  }

  public ProxyAuthRequestHandler(final Credentials creds, final String realm) {
    this(creds, realm, true);
  }

  public ProxyAuthRequestHandler(final Credentials creds) {
    this(creds, null, true);
  }

  public boolean processRequest(final SimpleHttpServerConnection conn,
                                final SimpleRequest request) throws IOException {
    Header clientAuth = request.getFirstHeader(PROXY_AUTH_RESP);
    if (clientAuth != null && checkAuthorization(clientAuth)) {
      return false;
    } else {
      SimpleResponse response = performBasicHandshake(conn, request);
      // Make sure the request body is fully consumed
      request.getBodyBytes();
      conn.writeResponse(response);
      return true;
    }
  }

  private SimpleResponse performBasicHandshake(final SimpleHttpServerConnection conn,
                                               final SimpleRequest request) {

    SimpleResponse response = new SimpleResponse();
    response.setStatusLine(request.getRequestLine().getHttpVersion(),
            HttpStatus.SC_PROXY_AUTHENTICATION_REQUIRED);
    if (!request.getRequestLine().getMethod().equalsIgnoreCase("HEAD")) {
      response.setBodyString("unauthorized");
    }
    response.addHeader(new Header("Proxy-Authenticate", "basic realm=\"" + this.realm + "\""));
    if (this.keepalive) {
      response.addHeader(new Header("Proxy-Connection", "keep-alive"));
      conn.setKeepAlive(true);
    } else {
      response.addHeader(new Header("Proxy-Connection", "close"));
      conn.setKeepAlive(false);
    }
    return response;
  }

  /**
   * Checks if the credentials provided by the client match the required credentials
   *
   * @param clientAuth the authentication header
   * @return true if the client is authorized, false if not.
   */
  private boolean checkAuthorization(Header clientAuth) {
    String expectedAuthString = BasicScheme.authenticate((UserPassCredentials) credentials, "ISO-8859-1");
    return expectedAuthString.equals(clientAuth.getValue());
  }

}
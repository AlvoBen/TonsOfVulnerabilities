/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.httpclient.utils;

import com.sap.httpclient.HostConfiguration;
import com.sap.httpclient.HttpClient;
import com.sap.httpclient.HttpClientParameters;
import com.sap.httpclient.HttpMethod;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.http.methods.*;
import com.sap.tc.logging.Location;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Provides a <code>HttpURLConnection</code> wrapper around HttpClient's <code>HttpMethod</code>.
 *
 * @author Nikolai Neichev
 */
public class HttpURLConnection extends java.net.HttpURLConnection {

  /**
   * Log object for this class.
   */
  private static final Location LOG = Location.getLocation(HttpURLConnection.class);

  /**
   * The <code>HttpMethod</code> object that was used to connect to the
   * HTTP server. It contains all the returned data.
   */
  private HttpMethod method;

  /**
   * The URL to which we are connected
   */
  private URL url;

  /**
   * Used to execute methods.
   */
  private HttpClient client = new HttpClient();

  private HttpClientParameters params = null;

  // TODO proxy support

  private boolean isProxied = false;

  /**
   * Creates an <code>HttpURLConnection</code> from a <code>HttpMethod</code>.
   *
   * @param method the theMethod that was used to connect to the HTTP
   *               server and which contains the returned data.
   * @param url    the URL to which we are connected (includes query string)
   */
  public HttpURLConnection(HttpMethod method, URL url) {
    super(url);
    this.method = method;
    this.url = url;
    HostConfiguration hostConfig = new HostConfiguration();
    hostConfig.setHost(url.getHost(), url.getPort(), url.getProtocol());
    this.client.setHostConfiguration(hostConfig);
  }

  /**
   * Create an instance with a GET method by default.
   *
   * @param url The URL.
   */
  protected HttpURLConnection(URL url) {
    this(new GET(), url);
  }

  /**
   * Gets an incoming stream for the HttpMethod response body.
   *
   * @return The incoming stream. NULL if the method is not executed yet.
   * @throws IOException If an IO problem occurs.
   */
  public InputStream getInputStream() throws IOException {
    return this.method.getResponseBodyAsStream();
  }

  /**
   * Not yet implemented.
   * Return the error stream.
   */
  public InputStream getErrorStream() {
    try {
      return this.method.getResponseBodyAsStream();
    } catch (Exception e) { // $JL-EXC$
      return null;
    }
  }

  /**
   * Releases the connection.
   */
  public void disconnect() {
    LOG.debugT("enter HttpURLConnection.disconnect()");
    method.releaseConnection();
  }

  /**
   * Connects and processes the method.
   *
   * @throws IOException If an IO problem occurs.
   */
  public void connect() throws IOException {
    if (params != null) {
      client.setParams(params);
    }
    client.executeMethod(method);
  }

  /**
   * Not yet implemented.
   *
   * @return true if we are using a proxy.
   */
  public boolean usingProxy() {
    return isProxied;
  }

  /**
   * Return the request method.
   *
   * @return The request method.
   */
  public String getRequestMethod() {
    return this.method.getName();
  }

  /**
   * Return the response code.
   *
   * @return The response code.
   * @throws IOException If an IO problem occurs.
   */
  public int getResponseCode() throws IOException {
    return this.method.getStatusCode();
  }

  /**
   * Return the response message
   *
   * @return The response message
   * @throws IOException If an IO problem occurs.
   */
  public String getResponseMessage() throws IOException {
    return this.method.getStatusText();
  }

  /**
   * Return the header field
   *
   * @param name the name of the header
   * @return the header field.
   */
  public String getHeaderField(String name) {
    ArrayList<Header> headers = this.method.getResponseHeaders();
    for (int i = headers.size() - 1; i >= 0; i--) {
      if (headers.get(i).getName().equalsIgnoreCase(name)) {
        return headers.get(i).getValue();
      }
    }
    return null;
  }

  /**
   * Return the header field key
   *
   * @param keyPosition The key position
   * @return The header field key.
   */
  public String getHeaderFieldKey(int keyPosition) {
    // Note: HttpClient does not consider the returned Status Line as
    // a response header. However, getHeaderFieldKey(0) is supposed to
    // return null. Hence the special case below ...
    if (keyPosition == 0) {
      return null;
    }
    // Note: HttpClient does not currently keep headers in the same order
    // that they are read from the HTTP server.
    ArrayList<Header> headers = this.method.getResponseHeaders();
    if (keyPosition < 0 || keyPosition > headers.size()) {
      return null;
    }
    return headers.get(keyPosition - 1).getName();
  }

  /**
   * Return the header field at the specified position
   *
   * @param position The position
   * @return The header field.
   */
  public String getHeaderField(int position) {
    // Note: HttpClient does not consider the returned Status Line as
    // a response header. However, getHeaderField(0) is supposed to
    // return the status line. Hence the special case below ...
    if (position == 0) {
      return this.method.getStatusLine().toString();
    }
    // Note: HttpClient does not currently keep headers in the same order
    // that they are read from the HTTP server.
    ArrayList<Header> headers = this.method.getResponseHeaders();
    if (position < 0 || position > headers.size()) {
      return null;
    }
    return headers.get(position - 1).getValue();
  }

  /**
   * Return the URL
   *
   * @return The URL.
   */
  public URL getURL() {
    return this.url;
  }

  // Note: We don't implement the following methods so that they default to
  // the JDK implementation. They will all call
  // <code>getHeaderField(String)</code> which we have overridden.

  // java.net.HttpURLConnection#getHeaderFieldDate(String, long)
  // java.net.HttpURLConnection#getContentLength()
  // java.net.HttpURLConnection#getContentType()
  // java.net.HttpURLConnection#getContentEncoding()
  // java.net.HttpURLConnection#getDate()
  // java.net.HttpURLConnection#getHeaderFieldInt(String, int)
  // java.net.HttpURLConnection#getExpiration()
  // java.net.HttpURLConnection#getLastModified()

  /**
   * Sets whether HTTP redirects (requests with response code 3xx) should
   * be automatically followed by this <code>HttpURLConnection</code>
   * instance.
   *
   * @param isFollowingRedirects the setting
   */
  public void setInstanceFollowRedirects(boolean isFollowingRedirects) {
    method.setFollowRedirects(isFollowingRedirects);
  }

  /**
   * Returns the value of this <code>HttpURLConnection</code>'s
   * <code>instanceFollowRedirects</code> field.
   *
   * @return the value
   */
  public boolean getInstanceFollowRedirects() {
    return method.getFollowRedirects();
  }

  /**
   * Sets the request method
   * @param methodName the method name
   * @throws ProtocolException
   */
  public void setRequestMethod(String methodName) throws ProtocolException {
    if (!method.getName().equals(methodName)) {  // changing the method
      if (methodName.equals(HttpMethod.METHOD_CONNECT)) {
        method = new CONNECT();
      } else if (methodName.equals(HttpMethod.METHOD_DELETE)) {
        method = new DELETE();
      } else if (methodName.equals(HttpMethod.METHOD_HEAD)) {
        method = new HEAD();
      } else if (methodName.equals(HttpMethod.METHOD_OPTIONS)) {
        method = new OPTIONS();
      } else if (methodName.equals(HttpMethod.METHOD_POST)) {
        method = new POST();
      } else if (methodName.equals(HttpMethod.METHOD_PUT)) {
        method = new PUT();
      } else if (methodName.equals(HttpMethod.METHOD_TRACE)) {
        method = new TRACE();
      } else {
        throw new ProtocolException("Incorect HTTP method : " + methodName);
      }
    }
  }

  /**
   * Not yet implemented.
   */
  public Object getContent() throws IOException {
    throw new RuntimeException("Not implemented yet");
  }

  /**
   * Not yet implemented.
   */
  public Object getContent(Class[] classes) throws IOException {
    throw new RuntimeException("Not implemented yet");
  }

  public OutputStream getOutputStream() throws IOException {
    throw new RuntimeException("This class can only be used with already retrieved data");
  }

  /**
   * Not available: the data must have already been retrieved.
   */
  public void setDoInput(boolean isInput) {
    throw new RuntimeException("This class can only be used with already retrieved data");
  }

  /**
   * Not yet implemented.
   */
  public boolean getDoInput() {
    throw new RuntimeException("Not implemented yet");
  }

  /**
   * Not available: the data must have already been retrieved.
   */
  public void setDoOutput(boolean isOutput) {
    throw new RuntimeException("This class can only be used with already retrieved data");
  }

  /**
   * Not yet implemented.
   */
  public boolean getDoOutput() {
    throw new RuntimeException("Not implemented yet");
  }

  /**
   * Not available: the data must have already been retrieved.
   */
  public void setAllowUserInteraction(boolean isAllowInteraction) {
    throw new RuntimeException("This class can only be used with already retrieved data");
  }

  /**
   * Not yet implemented.
   */
  public boolean getAllowUserInteraction() {
    throw new RuntimeException("Not implemented yet");
  }

  /**
   * Not available: the data must have already been retrieved.
   */
  public void setUseCaches(boolean isUsingCaches) {
    throw new RuntimeException("This class can only be used with already retrieved data");
  }

  /**
   * Not yet implemented.
   */
  public boolean getUseCaches() {
    throw new RuntimeException("Not implemented yet");
  }

  /**
   * Not available: the data must have already been retrieved.
   */
  public void setIfModifiedSince(long modificationDate) {
    throw new RuntimeException("This class can only be used with already retrieved data");
  }

  /**
   * Not yet implemented.
   */
  public long getIfModifiedSince() {
    throw new RuntimeException("Not implemented yet");
  }

  /**
   * Not available: the data must have already been retrieved.
   */
  public boolean getDefaultUseCaches() {
    throw new RuntimeException("Not implemented yet");
  }

  /**
   * Not available: the data must have already been retrieved.
   */
  public void setDefaultUseCaches(boolean isUsingCaches) {
    throw new RuntimeException("This class can only be used with already retrieved data");
  }

  /**
   * Sets a property
   */
  public void setRequestProperty(String key, String value) {
    params.setParameter(key, value);
  }

  /**
   * Gets a property
   */
  public String getRequestProperty(String key) {
    return (String) params.getParameter(key);
  }

}


// Example

//URL u = new URL(dir);
//HttpURLConnection huc = (HttpURLConnection) u.openConnection();
//HttpURLConnection huc = (HttpURLConnection) u.openConnection(proxy); -- this is proxy ussage..
///*GET will be our method to download a file*/
//huc.setRequestMethod("GET");
///*Stablishing the connection*/
//huc.connect();
///*Input stream to read from our connection*/
//InputStream is = huc.getInputStream();
///* the response code returned by the request*/
//int code = huc.getResponseCode();
//...

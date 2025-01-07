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
package com.sap.httpclient;

import com.sap.httpclient.auth.AuthState;
import com.sap.httpclient.exception.URIException;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.http.StatusLine;
import com.sap.httpclient.uri.URI;
import com.sap.httpclient.net.connection.HttpConnection;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * HttpMethod interface represents a http request and its corresponding response.
 *
 * @author Nikolai Neichev
 */
public interface HttpMethod {

  // method names constants
  String METHOD_GET     = "GET";
  String METHOD_OPTIONS = "OPTIONS";
  String METHOD_HEAD    = "HEAD";
  String METHOD_POST    = "POST";
  String METHOD_PUT     = "PUT";
  String METHOD_DELETE  = "DELETE";
  String METHOD_TRACE   = "TRACE";
  String METHOD_CONNECT = "CONNECT";

  //webdav methods
  String METHOD_COPY = "COPY";
  String METHOD_PROPFIND = "PROPFIND";
  String METHOD_PROPPATCH = "PROPPATCH";
  String METHOD_MKCOL = "MKCOL";
  String METHOD_MOVE = "MOVE";
  String METHOD_LOCK = "LOCK";
  String METHOD_UNLOCK = "UNLOCK";
  
  /**
   * Obtains the name of the HTTP method.
   *
   * @return the name of this method
   */
  String getName();

  /**
   * Sets the path of the HTTP method.
   *
   * @param path the path of the HTTP method. The path should be URL-encoded
   */
  void setPath(String path);

  /**
   * Returns the path of the HTTP method. Calling this method <em>after</em> the request has been
   * executed will return the <em>actual</em> path
   *
   * @return the path of the HTTP method, in URL encoded form
   */
  String getPath();

  /**
   * Returns the URI of the HTTP method
   *
   * @return The URI
   * @throws com.sap.httpclient.exception.URIException If the URI cannot be created.
   */
  URI getURI() throws URIException;

  /**
   * Sets the URI for this method.
   *
   * @param uri URI to be set
   * @throws URIException if a URI cannot be set
   */
  void setURI(URI uri) throws URIException;

  /**
   * Set the specified request header, overwriting any previous value.
   *
   * @param headerName  the header's name
   * @param headerValue the header's value
   */
  void setRequestHeader(String headerName, String headerValue);

  /**
   * Sets the specified request header, overwriting any previous value.
   *
   * @param header the header
   */
  void setRequestHeader(Header header);

  /**
   * Adds the specified request header, NOT overwriting any previous value.
   *
   * @param headerName  the header's name
   * @param headerValue the header's value
   */
  void addRequestHeader(String headerName, String headerValue);

  /**
   * Adds the specified request header, NOT overwriting any previous value.
   *
   * @param header the header to add to the request
   */
  void addRequestHeader(Header header);

  /**
   * Returns the specified request header.
   *
   * @param headerName The name of the header to be returned.
   * @return The specified request header, <tt>null</tt> if not present
   */
  Header getRequestHeader(String headerName);

  /**
   * Remove the request header associated with the specified name.
   *
   * @param headerName the header name
   */
  void removeRequestHeader(String headerName);

  /**
   * Removes the specified request header.
   *
   * @param header the header
   */
  void removeRequestHeader(Header header);

  /**
   * Checks whether the method should follow redirects automatically
   *
   * @return <tt>true</tt> if the method will follow HTTP redirects, <tt>false</tt> otherwise.
   */
  boolean getFollowRedirects();

  /**
   * Sets whether or not the HTTP method should automatically follow HTTP redirects
   *
   * @param follow <tt>true</tt> if the method will automatically follow redirects, <tt>false</tt> otherwise.
   */
  void setFollowRedirects(boolean follow);

  /**
   * Sets the query string of this HTTP method. The caller must ensure that the string is properly
   * URL encoded. The query string should not start with the question mark character.
   *
   * @param query the query string
   */
  void setQuery(String query);

  /**
   * Sets the query string of this HTTP method. The pairs are encoded as UTF-8 characters.
   *
   * @param params an array of {@link NameValuePair}s to add as query string parameters.
   *  The name/value pairs will be automcatically URL encoded
   */
  void setQueryString(NameValuePair[] params);

  /**
   * Gets the query string of this HTTP method.
   *
   * @return The query string
   */
  String getQuery();

  /**
   * Returns an array of the requests headers that the HTTP method currently has
   *
   * @return an array list of my request headers.
   */
  ArrayList<Header> getRequestHeaders();

  /**
   * Returns the request headers with the specified name.
   *
   * @param headerName the name of the headers to be returned.
   * @return an array list of zero or more headers
   */
  ArrayList<Header> getRequestHeaders(String headerName);

  /**
   * Returns the response status code.
   *
   * @return the status code associated with the latest response.
   */
  int getStatusCode();

  /**
   * Returns the status text (reason phrase) associated with the latest response.
   *
   * @return The status text.
   */
  String getStatusText();

  /**
   * Returns an array of the response headers that the HTTP method currently has
   *
   * @return an array of response headers.
   */
  ArrayList<Header> getResponseHeaders();

  /**
   * Gets the response header with the specified name.
   *
   * @param headerName the header name
   * @return the matching header, <tt>null</tt> if not present
   */
  Header getResponseHeader(String headerName);

  /**
   * Returns the response headers with the specified name. Note that header-name matching is
   * case insensitive.
   *
   * @param headerName the name of the headers to be returned.
   * @return an array list of zero or more headers
   */
  ArrayList<Header> getResponseHeaders(String headerName);

  /**
   * Returns the response footers from the last execution of this request.
   *
   * @return an array list containing the response footers in the order that they appeared in the response.
   */
  ArrayList<Header> getResponseFooters();

  /**
   * Return the specified response footer.
   *
   * @param footerName The name of the footer.
   * @return The response footer. <tt>null</tt> if no footers are available. If there are more than 1 footers
   * with this name, there values will be combined with the ',' separator(RFC2616)
   */
  Header getResponseFooter(String footerName);

  /**
   * Returns the response body of the HTTP method as a byte array.
   *
   * @return The response body. <tt>null</tt> if not available
   * @throws IOException If an I/O error occurs while obtaining the response body, or the content is too big.
   */
  byte[] getResponseBody() throws IOException;

  /**
   * Returns the response body of the HTTP method, if any, as a String. The string conversion on
   * the data is done using the character encoding specified in <tt>Content-Type</tt> header.
   *
   * @return The response body. <tt>null</tt> if not available or cannot be read
   * @throws IOException If an I/O error occurs while obtaining the response body.
   */
  String getResponseBodyAsString() throws IOException;

  /**
   * Returns the response body of the HTTP method, if any, as an InputStream.
   *
   * @return The response body, <tt>null</tt> if not available
   * @throws IOException If an I/O error occurs while obtaining the response body.
   */
  InputStream getResponseBodyAsStream() throws IOException;

  /**
   * Returns <tt>true</tt> if the HTTP method has been executed.
   *
   * @return <tt>true</tt> if the method has been executed, <tt>false</tt> otherwise
   */
  boolean hasBeenUsed();

  /**
   * Executes this method using the specified <code>HttpConnection</code> and <code>HttpState</code>.
   *
   * @param state the {@link HttpState state} information to associate with this method
   * @param connection the {@link HttpConnection connection} used to execute the HTTP method
   * @return the integer status code if one was obtained, or <tt>-1</tt>
   * @throws IOException   If an I/O (transport) error occurs.
   */
  int execute(HttpState state, HttpConnection connection) throws IOException;

  /**
   * Aborts the execution of the HTTP method.
   */
  void abort();

  /**
   * Releases the connection being used by this HTTP method.
   */
  void releaseConnection();

  /**
   * Add a footer to this method's response.
   *
   * @param footer the footer to add
   */
  void addResponseFooter(Header footer);

  /**
   * Gets the response status line.
   *
   * @return the status line object from the latest response.
   */
  StatusLine getStatusLine();

  /**
   * Returns <tt>true</tt> if the HTTP method should automatically handle HTTP
   * authentication challenges, <tt>false</tt> otherwise
   *
   * @return <tt>true</tt> if authentication challenges will be processed automatically, <tt>false</tt> otherwise.
   */
  boolean getDoAuthentication();

  /**
   * Sets whether or not the HTTP method should automatically handle HTTP authentication challenges
   *
   * @param doAuthentication <tt>true</tt> to process authentication challenges authomatically,
   * <tt>false</tt> otherwise.
   */
  void setDoAuthentication(boolean doAuthentication);

  /**
   * Returns the method parameters.
   *
   * @return {@link HttpClientParameters}.
   */
  public HttpClientParameters getParams();

  /**
   * Sets the method parameters.
	 * @param params the parameters to set
	 */
  public void setParams(final HttpClientParameters params);

  /**
   * Returns the target host {@link AuthState authentication state}
   *
   * @return host authentication state
   */
  public AuthState getHostAuthState();

  /**
   * Returns the proxy {@link AuthState authentication state}
   *
   * @return host authentication state
   */
  public AuthState getProxyAuthState();

  /**
   * Returns <tt>true</tt> if the HTTP has been sent to the target server, <tt>false</tt> otherwise.
   *
   * @return <tt>true</tt> if the request has been sent, <tt>false</tt> otherwise
   */
  boolean isRequestSent();

  /**
   * Tests whether the execution of this method has been aborted
   *
   * @return <tt>true</tt> if the execution of this method has been aborted, <tt>false</tt> otherwise
   */
  public boolean isAborted();

}
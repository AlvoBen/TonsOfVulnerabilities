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
import com.sap.httpclient.exception.*;
import com.sap.httpclient.http.*;
import com.sap.httpclient.http.cache.CacheObject;
import com.sap.httpclient.http.cache.CacheManager;
import com.sap.httpclient.http.cache.ItemID;
import com.sap.httpclient.http.cookie.Cookie;
import com.sap.httpclient.http.cookie.CookiePolicy;
import com.sap.httpclient.http.cookie.CookieSpec;
import com.sap.httpclient.net.Protocol;
import com.sap.httpclient.net.connection.HttpConnection;
import com.sap.httpclient.uri.EncodingUtil;
import com.sap.httpclient.uri.URI;
import com.sap.httpclient.utils.dump.Dump;
import com.sap.httpclient.utils.dump.DumpInputStream;
import com.sap.httpclient.utils.OutputStreamInterceptor;
import com.sap.tc.logging.Location;

import java.io.*;
import java.util.Collection;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

/**
 * Base implementation of HTTP Method.
 *
 * @author Nikolai Neichev
 */
public abstract class HttpMethodImpl implements HttpMethod {

  /**
   * Log object for this class.
   */
  private static final Location LOG = Location.getLocation(HttpMethodImpl.class);

  private HeaderGroup requestHeaders = new HeaderGroup();

  private StatusLine statusLine = null;

  private HeaderGroup responseHeaders = new HeaderGroup();

  private HeaderGroup trailerHeaders = new HeaderGroup();

  /**
   * Path of the HTTP method.
   */
  private String path = null;

  /**
   * Query string of the HTTP method, if any.
   */
  private String query = null;

  /**
   * The response body of the HTTP method
   */
  private InputStream responseStream = null;

  /**
   * The response connection
   */
  private HttpConnection responseConnection = null;

  /**
   * the response buffer
   */
  private byte[] responseBody = null;

  /**
   * True if the HTTP method should automatically follow HTTP redirects.
   */
  private boolean followRedirects = false;

  /**
   * True if the HTTP method should automatically handle HTTP authentication challenges.
   */
  private boolean doAuthentication = true;

  /**
   * The parameters.
   */
  private HttpClientParameters params = new HttpClientParameters();

  /**
   * Host authentication state
   */
  private AuthState hostAuthState = new AuthState();

  /**
   * Proxy authentication state
   */
  private AuthState proxyAuthState = new AuthState();

  /**
   * True if this method has already been executed.
   */
  private boolean used = false;

  /**
   * the host for this HTTP method, can be null
   */
  private HttpHost httphost = null;

  /**
   * True if the connection must be closed when no longer needed
   */
  private boolean connectionCloseForced = false;

  /**
   * Number of milliseconds to wait for 100-contunue response.
   */
  private static final int RESPONSE_WAIT_TIME_MS = 3000;

  /**
   * The HTTP version used for execution of this method.
   */
  private HttpVersion effectiveVersion = null;

  /**
   * Whether the execution of this method has been aborted
   */
  private transient boolean aborted = false;

  /**
   * Whether the whole HTTP request has been sent to the target server
   */
  private boolean requestSent = false;

  /**
   * Actual cookie policy
   */
  private CookieSpec cookieSpec = null;

  private String cacheKey = null;
  private ItemID cacheId = null;
  private CacheObject cObject = null;

  /**
   * Default initial size of the response buffer if content length is unknown.
   */
  private static final int DEFAULT_INITIAL_BUFFER_SIZE = 4 * 1024; // 4 kB

  /**
   * No-arg constructor.
   */
  public HttpMethodImpl() {
  }

  /**
   * Constructor specifying a URI.
   *
   * @param uri either an absolute or relative URI. The URI is expected to be URL-encoded
   * @throws IllegalArgumentException when URI is invalid
   * @throws IllegalStateException    when net of the absolute URI is not recognised
   */
  public HttpMethodImpl(String uri) throws IllegalArgumentException, IllegalStateException {
    try {
      if (uri == null || uri.equals("")) {
        uri = "/";
      }
      setURI(new URI(uri, true));
    } catch (URIException e) {
      throw new IllegalArgumentException("Invalid uri '" + uri + "': " + e.getMessage());
    }
  }

  /**
   * Obtains the name of the HTTP method
   *
   * @return the name of this method
   */
  public abstract String getName();

  public URI getURI() throws URIException {
    StringBuilder buffer = new StringBuilder(64);
    if (this.httphost != null) {
      buffer.append(this.httphost.getProtocol().getScheme());
      buffer.append("://");
      buffer.append(this.httphost.getHostName());
      int port = this.httphost.getPort();
      if (port != -1 && port != this.httphost.getProtocol().getDefaultPort()) {
        buffer.append(":");
        buffer.append(port);
      }
    }
    if (this.path == null) {
      buffer.append("/");
    } else {
      buffer.append(this.path);
    }
    if (this.query != null) {
      buffer.append('?');
      buffer.append(this.query);
    }
    return new URI(buffer.toString(), true);
  }

  /**
   * Sets the URI for this HTTP method.
   *
   * @param uri URI to be set
   * @throws com.sap.httpclient.exception.URIException
   *          if a URI cannot be set
   */
  public void setURI(URI uri) throws URIException {
    if (uri.isAbsoluteURI()) { // set the host if specified in the URI
      if (params.getBoolean(Parameters.ENCODE_URLS, true)) { // encode by default
        this.httphost = new HttpHost(uri);
      } else {
        this.httphost = new HttpHost(uri, true);
      }
    }
    setPath(uri.getPath() == null ? "/" : uri.getEscapedPath());   // default "/"(root)
    setQuery(uri.getEscapedQuery());
  }

  /**
   * Sets whether or not the HTTP method should automatically follow HTTP redirects
   *
   * @param follow <tt>true</tt> if the method will automatically follow redirects, <tt>false</tt> otherwise.
   */
  public void setFollowRedirects(boolean follow) {
    this.followRedirects = follow;
  }

  /**
   * Checks whether the method should follow redirects automatically
   *
   * @return <tt>true</tt> if the method will follow HTTP redirects, <tt>false</tt> otherwise.
   */
  public boolean getFollowRedirects() {
    return this.followRedirects;
  }

  /**
   * Returns <tt>true</tt> if the HTTP method should automatically handle HTTP
   * authentication challenges, <tt>false</tt> otherwise
   *
   * @return <tt>true</tt> if authentication challenges will be processed automatically, <tt>false</tt> otherwise.
   */
  public boolean getDoAuthentication() {
    return doAuthentication;
  }

  /**
   * Sets whether or not the HTTP method should automatically handle HTTP authentication challenges
   *
   * @param doAuthentication <tt>true</tt> to process authentication challenges authomatically,
   *                         <tt>false</tt> otherwise.
   */
  public void setDoAuthentication(boolean doAuthentication) {
    this.doAuthentication = doAuthentication;
  }

  /**
   * Sets the path of the HTTP method.
   *
   * @param path the path of the HTTP method. The path should be URL-encoded
   */
  public void setPath(String path) {
    this.path = path;
    cacheKey = null;  // cacheKey should be calculated again
  }

  /**
   * Adds the specified request header, NOT overwriting any previous value.
   *
   * @param header the header to add to the request
   */
  public void addRequestHeader(Header header) {
    LOG.debugT("HttpMethodImpl.addRequestHeader(Header)");
    if (header == null) {
      LOG.debugT("null header value - ignoring...");
    } else {
      getRequestHeaderGroup().addHeader(header);
    }
  }

  /**
   * Add a footer to this method's response.
   *
   * @param footer the footer to add
   */
  public void addResponseFooter(Header footer) {
    getTrailerHeaderGroup().addHeader(footer);
  }

  /**
   * Returns the path of the HTTP method. Calling this method <em>after</em> the request has been
   * executed will return the <em>actual</em> path
   *
   * @return the path of the HTTP method, in URL encoded form
   */
  public String getPath() {
    return (path == null || path.equals("")) ? "/" : path;
  }

  /**
   * Sets the query string of this HTTP method. The caller must ensure that the string is properly
   * URL encoded. The query string should not start with the question mark character.
   *
   * @param query the query string
   */
  public void setQuery(String query) {
    this.query = query;
    cacheKey = null;  // cacheKey should be calculated again
  }

  /**
   * Sets the query string of this HTTP method. The pairs are encoded as UTF-8 characters.
   *
   * @param params an array of {@link NameValuePair}s to add as query string parameters.
   *               The name/value pairs will be automcatically URL encoded
   */
  public void setQueryString(NameValuePair[] params) {
    query = EncodingUtil.getURLEncodedString(params, "UTF-8");
    cacheKey = null;  // cacheKey should be calculated again
  }

  /**
   * Gets the query string of this HTTP method.
   *
   * @return The query string
   */
  public String getQuery() {
    return query;
  }

  /**
   * Get the path?query string, used for cacheID
   *
   * @return the path?query string
   */
  public String getPathQuery() {
    if (getPath() == null && getQuery() == null) {
      return null;
    }

    StringBuilder buff = new StringBuilder();
    if (path != null) {
      buff.append(path);
    }

    if (query != null) {
      buff.append('?');
      buff.append(query);
    }
    return buff.toString();
  }

  /**
   * Set the specified request header, overwriting any previous value.
   *
   * @param headerName  the header's name
   * @param headerValue the header's value
   */
  public void setRequestHeader(String headerName, String headerValue) {
    Header header = new Header(headerName, headerValue);
    setRequestHeader(header);
  }

  /**
   * Sets the specified request header, overwriting any previous value.
   *
   * @param header the header
   */
  public void setRequestHeader(Header header) {
    ArrayList<Header> headers = getRequestHeaderGroup().getHeaders(header.getName());
    for (Header head : headers) {
      getRequestHeaderGroup().removeHeader(head);
    }
    getRequestHeaderGroup().addHeader(header);
  }

  /**
   * Returns the specified request header.
   *
   * @param headerName The name of the header to be returned.
   * @return The specified request header, <tt>null</tt> if not present
   */
  public Header getRequestHeader(String headerName) {
    if (headerName == null) {
      return null;
    } else {
      return getRequestHeaderGroup().getCondensedHeader(headerName);
    }
  }

  /**
   * Returns an array of the requests headers that the HTTP method currently has
   *
   * @return an array of my request headers.
   */
  public ArrayList<Header> getRequestHeaders() {
    return getRequestHeaderGroup().getAllHeaders();
  }

  /**
   * Returns the request headers with the specified name.
   *
   * @param headerName the name of the headers to be returned.
   * @return an array of zero or more headers
   */
  public ArrayList<Header> getRequestHeaders(String headerName) {
    return getRequestHeaderGroup().getHeaders(headerName);
  }

  /**
   * Gets the {@link HeaderGroup header group} storing the request headers.
   *
   * @return the HeaderGroup
   */
  protected HeaderGroup getRequestHeaderGroup() {
    return requestHeaders;
  }

  /**
   * Gets the {@link HeaderGroup header group} storing the response trailer headers
   *
   * @return the HeaderGroup
   */
  protected HeaderGroup getTrailerHeaderGroup() {
    return trailerHeaders;
  }

  /**
   * Gets the {@link HeaderGroup header group} storing the response headers.
   *
   * @return the HeaderGroup
   */
  protected HeaderGroup getResponseHeaderGroup() {
    return responseHeaders;
  }

  public ArrayList<Header> getResponseHeaders(String headerName) {
    return getResponseHeaderGroup().getHeaders(headerName);
  }

  /**
   * Returns the response status code.
   *
   * @return the status code associated with the latest response.
   */
  public int getStatusCode() {
    return statusLine.getStatusCode();
  }

  /**
   * Gets the response status line.
   *
   * @return the status line object from the latest response.
   */
  public StatusLine getStatusLine() {
    return statusLine;
  }

  /**
   * Checks if response data is available.
   *
   * @return <tt>true</tt> if response data is available, <tt>false</tt> otherwise.
   */
  private boolean responseAvailable() {
    return (responseBody != null) || (responseStream != null);
  }

  /**
   * Returns an array of the response headers that the HTTP method currently has
   *
   * @return an array of response headers.
   */
  public ArrayList<Header> getResponseHeaders() {
    return getResponseHeaderGroup().getAllHeaders();
  }

  /**
   * Gets the response header with the specified name.
   *
   * @param headerName the header name
   * @return the matching header, <tt>null</tt> if not present
   */
  public Header getResponseHeader(String headerName) {
    if (headerName == null) {
      return null;
    } else {
      return getResponseHeaderGroup().getCondensedHeader(headerName);
    }
  }

  /**
   * Return the length (in bytes) of the response body, as specified in a <tt>Content-Length</tt> header.
   *
   * @return the content length, if <tt>Content-Length</tt> header is available.
   *         <tt>0</tt> indicates that the request has no body.
   *         <tt>-1</tt> if there is no <tt>Content-Length</tt> header
   */
  public long getResponseContentLength() {
    ArrayList<Header> headers = getResponseHeaderGroup().getHeaders(Header._CONTENT_LENGTH);
    if (headers.size() == 0) {
      return -1; // no content-length header
    }
    if (headers.size() > 1) {
      LOG.warningT("Multiple content-length headers are present !");
    }
    for (int i = headers.size() - 1; i >= 0; i--) {
      Header header = headers.get(i);
      try {
        return Long.parseLong(header.getValue());
      } catch (NumberFormatException e) {
        if (LOG.beWarning()) {
          LOG.warningT("Bad content-length value : " + header.getValue() + ", error is : " + e.getMessage());
        }
      }
    }
    return -1; // bad luck :(
  }

  /**
   * Returns the response body of the HTTP method as a byte array.
   *
   * @return The response body. <tt>null</tt> if not available
   * @throws IOException If an I/O error occurs while obtaining the response body, or the content is too big.
   */
  public byte[] getResponseBody() throws IOException {
    if (this.responseBody == null) {
      InputStream instream = getResponseBodyAsStream();
      if (instream != null) {
        long contentLength = getResponseContentLength();
        if (contentLength > Integer.MAX_VALUE) { // content too big, we don't want an OOM error
          throw new IOException("Can't create so large byte[], size is " + contentLength + " bytes");
        }
        int limit = getParams().getInt(HttpClientParameters.BUFFER_WARN_TRIGGER_LIMIT, 1024 * 1024);
        if (contentLength > limit) {
          LOG.warningT("Buffer warning ! The content size is big : " + contentLength);
        }
        if (contentLength == -1) {
          LOG.warningT("Buffer size is unknown !");
        }
        LOG.debugT("Buffering response body");
        int bufLen = contentLength > 0 ? (int) contentLength : DEFAULT_INITIAL_BUFFER_SIZE;
        ByteArrayOutputStream outstream = new ByteArrayOutputStream(bufLen);
        byte[] buffer = new byte[bufLen];
        int len;
        while ((len = instream.read(buffer)) > 0) {
          outstream.write(buffer, 0, len);
        }
        outstream.close();
        setResponseStream(null);
        this.responseBody = outstream.toByteArray();
      }
    }
    return this.responseBody;
  }

  /**
   * Returns the response body of the HTTP method, if any, as an InputStream.
   *
   * @return The response body, <tt>null</tt> if not available
   * @throws IOException If an I/O error occurs while obtaining the response body.
   */
  public InputStream getResponseBodyAsStream() throws IOException {
    if (responseStream != null) {
      return responseStream;
    }
    if (responseBody != null) {
      InputStream byteResponseStream = new ByteArrayInputStream(responseBody);
      LOG.debugT("Creating response stream from the byte array");
      return byteResponseStream;
    }
    return null;
  }


  private String getCacheKey() {
    if (cacheKey == null) {
      cacheKey = getName() + "#" + getPathQuery().replace('/', '_');
    }
    return cacheKey;
  }

  /**
   * Returns the response body of the HTTP method, if any, as a String. The string conversion on
   * the data is done using the character encoding specified in <tt>Content-Type</tt> header.
   *
   * @return The response body. <tt>null</tt> if not available or cannot be read/
   * @throws IOException If an I/O error occurs while obtaining the response body.
   */
  public String getResponseBodyAsString() throws IOException {

    if (CacheManager.isRunning() && cObject != null) {
      return EncodingUtil.getString(cObject.getValue(), getResponseCharSet());
    }

    if (CacheManager.isRunning()) {
      if (CacheManager.isStored(getCacheKey())) {
        ItemID id = CacheManager.getInstance().loadFile(new File(getCacheKey()));
        if (id != null) {
          this.cObject = CacheManager.getInstance().getCacheEntry(id);
          return EncodingUtil.getString(cObject.getValue(), getResponseCharSet());
        }
      }
    }

    byte[] rawdata = null;
    if (responseAvailable()) {
      rawdata = getResponseBody();
    }
    if (rawdata != null) {
      if (CacheManager.isRunning() && CacheManager.isCacheable(this)) {
        ItemID id = new ItemID(getCacheKey());
        CacheObject cObj = new CacheObject(id, rawdata);
        cObj.setHeaders(getResponseHeaders());
        cObj.setStatusLine(statusLine);
        CacheManager.getInstance().addEntry(cObj);
      }
      return EncodingUtil.getString(rawdata, getResponseCharSet());
    } else {
      this.cacheId = null;
      this.cacheKey = null;
      return null;
    }
  }

  /**
   * Returns the response footers from the last execution of this request.
   *
   * @return an array containing the response footers in the order that they appeared in the response.
   */
  public ArrayList<Header> getResponseFooters() {
    return getTrailerHeaderGroup().getAllHeaders();
  }

  /**
   * Return the specified response footer.
   *
   * @param footerName The name of the footer.
   * @return The response footer. <tt>null</tt> if no footers are available. If there are more than 1 footers
   *         with this name, there values will be combined with the ',' separator(RFC2616)
   */
  public Header getResponseFooter(String footerName) {
    if (footerName == null) {
      return null;
    } else {
      return getTrailerHeaderGroup().getCondensedHeader(footerName);
    }
  }

  /**
   * Sets the response stream.
   *
   * @param responseStream The new response stream.
   */
  protected void setResponseStream(InputStream responseStream) {
    this.responseStream = responseStream;
  }

  /**
   * Returns the stream from which the body of the response may be read.
   *
   * @return the current response stream, <code>null</code> if not available
   */
  protected InputStream getResponseStream() {
    return responseStream;
  }

  /**
   * Returns the status text (reason phrase) associated with the latest response.
   *
   * @return The status text.
   */
  public String getStatusText() {
    return statusLine.getReasonPhrase();
  }

  /**
   * Adds the specified request header, NOT overwriting any previous value.
   *
   * @param headerName  the header's name
   * @param headerValue the header's value
   */
  public void addRequestHeader(String headerName, String headerValue) {
    addRequestHeader(new Header(headerName, headerValue));
  }

  /**
   * Tests if the connection should be force-closed when no longer needed.
   *
   * @return <code>true</code> if the connection must be closed
   */
  protected boolean isConnectionCloseForced() {
    return this.connectionCloseForced;
  }

  /**
   * Sets whether or not the connection should be force-closed when no longer needed.
   *
   * @param b <code>true</code> if the connection must be closed, <code>false</code> otherwise.
   */
  protected void setConnectionCloseForced(boolean b) {
    if (LOG.beDebug()) {
      LOG.debugT("Force-close connection: " + b);
    }
    this.connectionCloseForced = b;
  }

  /**
   * Tests if the connection should be closed after the method has been executed.
   *
   * @param conn the connection in question
   * @return boolean true if we should close the connection.
   */
  protected boolean shouldCloseConnection(HttpConnection conn) {
    if (isConnectionCloseForced()) { // close if forsed
      LOG.debugT("Should force-close connection.");
      return true;
    }
    Header connectionHeader = null;
    if (!conn.isTransparent()) { // maybe trough through proxy
      connectionHeader = responseHeaders.getFirstHeader("proxy-connection");
    }
    if (connectionHeader == null) { // some non-complaint proxy servers send 'connection' instread of 'proxy-connection'
      connectionHeader = responseHeaders.getFirstHeader("connection");
    }
    if (connectionHeader == null) { // check whether the request has connection directives
      connectionHeader = requestHeaders.getFirstHeader("connection");
    }
    if (connectionHeader != null) { // connection directive is found
      if (connectionHeader.getValue().equalsIgnoreCase("close")) {
        if (LOG.beDebug()) {
          LOG.debugT("Close connection found");
        }
        return true;
      } else if (connectionHeader.getValue().equalsIgnoreCase("keep-alive")) {
        if (LOG.beDebug()) {
          LOG.debugT("Keep-alive connecion found");
        }
        return false;
      } else {
        if (LOG.beDebug()) {
          LOG.debugT("Bad value : " + connectionHeader.toText());
        }
      }
    }
    if (LOG.beDebug()) {
      LOG.debugT("Connection directive not found, HTTP version is : " + this.effectiveVersion.toString());
    }
    return this.effectiveVersion.lessEquals(HttpVersion.HTTP_1_0);
  }

  /**
   * Tests if the this method is ready to be executed.
   *
   * @param state the {@link HttpState state} information associated with this method
   * @param conn  the {@link com.sap.httpclient.net.connection.HttpConnection connection} to be used
   */
  private void checkExecuteConditions(HttpState state, HttpConnection conn) {
    if (state == null) {
      throw new IllegalArgumentException("HttpState parameter is null");
    }
    if (conn == null) {
      throw new IllegalArgumentException("HttpConnection parameter is null");
    }
    if (this.aborted) {
      throw new IllegalStateException("Method has been aborted");
    }
  }

  /**
   * Executes this method using the specified <code>HttpConnection</code> and <code>HttpState</code>.
   *
   * @param state      the {@link HttpState state} information to associate with this method
   * @param connection the {@link HttpConnection connection} used to execute the HTTP method
   * @return the integer status code if one was obtained, or <tt>-1</tt>
   * @throws IOException If an I/O (transport) error occurs.
   */
  public int execute(HttpState state, HttpConnection connection) throws IOException {
    if (CacheManager.isRunning()) {
      this.cacheId = new ItemID(getCacheKey());
      cObject = CacheManager.getInstance().getCacheEntry(this.cacheId);
      if (cObject != null) {
        statusLine = cObject.getStatusLine();
        getResponseHeaderGroup().setHeaders(cObject.getHeaderList());
        return statusLine.getStatusCode();
      }
    }

    this.responseConnection = connection;
    checkExecuteConditions(state, connection);
    this.statusLine = null;
    this.connectionCloseForced = false;
    connection.setLastResponseInputStream(null);
    if (this.effectiveVersion == null) {
      this.effectiveVersion = this.params.getVersion();
    }
//		int protocol = HttpHandler.HTTP_PROTOCOL;  // usually its http
//		if (connection.getProtocol().isSecure()) {
//			protocol = HttpHandler.HTTPS_PROTOCOL;  // so it is secure
//		}
//		String sap_passport = HttpHandler.beforeSendingRequest(protocol, connection.getHostConfiguration().getHost(), null, connection.getPort(), getPath());
//		setRequestHeader("SAP-PASSPORT", sap_passport);
    writeRequest(state, connection);
    this.requestSent = true;
    readResponse(state, connection);
//		HttpHandler.afterReceivingResponse();
    used = true;
    return statusLine.getStatusCode();
  }

  /**
   * Aborts the execution of this method.
   */
  public void abort() {
    if (this.aborted) {
      return;
    }
    this.aborted = true;
    HttpConnection conn = this.responseConnection;
    if (conn != null) {
      conn.close();
    }
  }

  /**
   * Returns <tt>true</tt> if the HTTP method has been executed.
   *
   * @return <tt>true</tt> if the method has been executed, <tt>false</tt> otherwise
   */
  public boolean hasBeenUsed() {
    return used;
  }

  /**
   * Releases the connection being used by this HTTP method.
   */
  public void releaseConnection() {
    try {
      if (this.responseStream != null) {
        try {
          this.responseStream.close();
        } catch (IOException ioe) {
          // $JL-EXC$
        }
      }
    } finally {
      ensureConnectionRelease();
    }
  }

  /**
   * Remove the request header associated with the specified name.
   *
   * @param headerName the header name
   */
  public void removeRequestHeader(String headerName) {
    ArrayList<Header> headers = getRequestHeaderGroup().getHeaders(headerName);
    for (Header header : headers) {
      getRequestHeaderGroup().removeHeader(header);
    }
  }

  /**
   * Removes the specified request header.
   *
   * @param header the header
   */
  public void removeRequestHeader(final Header header) {
    if (header == null) {
      return;
    }
    getRequestHeaderGroup().removeHeader(header);
  }

  /**
   * Returns the actual cookie policy
   *
   * @return cookie spec
   */
  private CookieSpec getCookieSpec() {
    if (this.cookieSpec == null) {
      this.cookieSpec = CookiePolicy.getCookieSpec(this.params.getCookiePolicy());
      this.cookieSpec.setValidDateFormats((Collection<String>) this.params.getParameter(HttpClientParameters.DATE_PATTERNS));
    }
    return this.cookieSpec;
  }

  /**
   * Generates <tt>Cookie</tt> request headers for those that match the specified host, port and path.
   *
   * @param state the {@link HttpState state} information associated with this method
   * @param conn  the {@link HttpConnection connection} used to execute this HTTP method
   * @throws IOException if an I/O (transport) error occurs.
   */
  protected void addCookieRequestHeader(HttpState state, HttpConnection conn) throws IOException {
    ArrayList<Header> cookieheaders = getRequestHeaderGroup().getHeaders(Header.COOKIE);
    for (Header cookieheader : cookieheaders) {
      if (cookieheader.isAutogenerated()) {
        getRequestHeaderGroup().removeHeader(cookieheader);
      }
    }
    CookieSpec cookieSpec = getCookieSpec();
    String host = this.params.getVirtualHost();
    if (host == null) {
      host = conn.getHost();
    }
    Cookie[] cookies = cookieSpec.match(host, conn.getPort(), getPath(), conn.isSecure(), state.getCookies());
    if ((cookies != null) && (cookies.length > 0)) {
      if (getParams().isTrue(Parameters.SINGLE_COOKIE_HEADER)) {
        // In strict mode put all cookies on the same header
        String s = cookieSpec.formatCookies(cookies);
        getRequestHeaderGroup().addHeader(new Header(Header.COOKIE, s, true));
      } else {
        // In non-strict mode put each cookie on a separate header
        for (Cookie cookie : cookies) {
          String s = cookieSpec.formatCookie(cookie);
          getRequestHeaderGroup().addHeader(new Header(Header.COOKIE, s, true));
        }
      }
    }
  }

  /**
   * Generates <tt>Host</tt> request header.
   *
   * @param state the {@link HttpState state} information associated with this method
   * @param conn  the {@link HttpConnection connection} used to execute this HTTP method
   * @throws IOException if an I/O (transport) error occurs.
   */
  protected void addHostRequestHeader(HttpState state, HttpConnection conn) throws IOException {
    if (getParams().isTrue(Parameters.DISABLE_HOST_SENDING)) { // check if host sending is disabled
      LOG.debugT("Sending host header is disabled.");
      return;
    }
    String host = this.params.getVirtualHost();
    if (host != null) {
      if (LOG.beDebug()) {
        LOG.debugT("Using virtual host name: " + host);
      }
    } else {
      host = conn.getHost();
    }
    int port = conn.getPort();
    if (conn.getProtocol().getDefaultPort() != port) { // append the port if not default
      host += (":" + port);
    }
    setRequestHeader("Host", host);
  }

  /**
   * Generates <tt>Proxy-Connection: Keep-Alive</tt> request header when communicating via a proxy server.
   *
   * @param state the {@link HttpState state} information associated with this method
   * @param conn  the {@link HttpConnection connection} used to execute this HTTP method
   * @throws IOException if an I/O (transport) error occurs.
   */
  protected void addProxyConnectionHeader(HttpState state, HttpConnection conn) throws IOException {
    if (!conn.isTransparent()) {
      if (getRequestHeader("Proxy-Connection") == null) {
        addRequestHeader("Proxy-Connection", "Keep-Alive");
      }
    }
  }

  /**
   * Generates all the required request {@link Header header}s
   * Adds <tt>User-Agent</tt>, <tt>Host</tt>, <tt>Cookie</tt>, <tt>Authorization</tt>,
   * <tt>Proxy-Authorization</tt> and <tt>Proxy-Connection</tt> headers, when appropriate.
   *
   * @param state the {@link HttpState state} information associated with this method
   * @param conn  the {@link HttpConnection connection} used to execute this HTTP method
   * @throws IOException   if an I/O (transport) error occurs.
   * @throws HttpException if a net exception occurs.
   */
  protected void addRequestHeaders(HttpState state, HttpConnection conn) throws IOException {
    addUserAgentRequestHeader(state, conn);
    addHostRequestHeader(state, conn);
    addCookieRequestHeader(state, conn);
    addProxyConnectionHeader(state, conn);
//    // TODO - to be tested
//    // adding the Accept-Encoding header...
//    String contentEncoding = (String) getParams().getParameter(Parameters.FORCE_CONTENT_ENCODING);
//    if (contentEncoding != null) { // there is a forced encoding
//      if (getRequestHeader(Header.ACCEPT_ENCODING) == null) { // header is not already set
//        addRequestHeader(Header.ACCEPT_ENCODING, "gzip, deflate");
//      }
//    }
  }

  /**
   * Generates default <tt>User-Agent</tt> request header.
   *
   * @param state the {@link HttpState state} information associated with this method
   * @param conn  the {@link HttpConnection connection} used to execute this HTTP method
   * @throws IOException if an I/O (transport) error occurs.
   */
  protected void addUserAgentRequestHeader(HttpState state, HttpConnection conn) throws IOException {
    if (getRequestHeader(Header.USER_AGENT) == null) {
      String agent = (String) getParams().getParameter(HttpClientParameters.USER_AGENT);
      if (agent == null) {
        agent = "SAP HttpClient v1.0";
      }
      setRequestHeader(Header.USER_AGENT, agent);
    }
  }

  /**
   * Checks if the method is not used.
   *
   * @throws IllegalStateException if the method has been used
   */
  protected void assertNotUsed() throws IllegalStateException {
    if (used) {
      throw new IllegalStateException("Method already used.");
    }
  }

  /**
   * Checks if the method has been used.
   *
   * @throws IllegalStateException if not used
   */
  protected void assertUsed() throws IllegalStateException {
    if (!used) {
      throw new IllegalStateException("Method not used.");
    }
  }

  /**
   * Generates HTTP request line according to the specified attributes.
   *
   * @param connection  the {@link HttpConnection connection} used to execute this HTTP method
   * @param name        the method name generate a request for
   * @param requestPath the path string for the request
   * @param query       the query string for the request
   * @param version     the net version to use
   * @return HTTP request line
   */
  protected static String generateRequestLine(HttpConnection connection,
                                              String name, String requestPath, String query, String version) {
    StringBuilder buf = new StringBuilder();
    // method name
    buf.append(name);
    buf.append(" ");
    // Absolute or relative URL?
    if (!connection.isTransparent()) {
      Protocol protocol = connection.getProtocol();
      buf.append(protocol.getScheme().toLowerCase());
      buf.append("://");
      buf.append(connection.getHost());
      if ((connection.getPort() != -1) && (connection.getPort() != protocol.getDefaultPort())) {
        buf.append(":");
        buf.append(connection.getPort());
      }
    }
    // path
    if (requestPath == null) {
      buf.append("/");
    } else {
      if (!connection.isTransparent() && !requestPath.startsWith("/")) {
        buf.append("/");
      }
      buf.append(requestPath);
    }
    // query
    if (query != null) {
      if (query.indexOf("?") != 0) {
        buf.append("?");
      }
      buf.append(query);
    }
    // version
    buf.append(" ");
    buf.append(version);
    // new line
    buf.append("\r\n");
    return buf.toString();
  }

  /**
   * Processes the read response body if necessary
   *
   * @param state the {@link HttpState state} information associated with this method
   * @param conn  the {@link HttpConnection connection} used to execute this HTTP method
   */
  protected void processResponseBody(HttpState state, HttpConnection conn) {
  }

  /**
   * Processes the read response headers if necessary
   * This implementation will handle the <tt>Set-Cookie</tt> and <tt>Set-Cookie2</tt> headers, if any
   *
   * @param state the {@link HttpState state} information associated with this method
   * @param conn  the {@link HttpConnection connection} used to execute this HTTP method
   */
  protected void processResponseHeaders(HttpState state, HttpConnection conn) {
    ArrayList<Header> headers = getResponseHeaderGroup().getHeaders("set-cookie2");
    if (headers.size() == 0) { // no new style headers, will process only the old style
      headers = getResponseHeaderGroup().getHeaders("set-cookie");
    }
    CookieSpec parser = getCookieSpec();
    String host = this.params.getVirtualHost();
    if (host == null) {
      host = conn.getHost();
    }
    for (Header header : headers) {
      Cookie[] cookies = null;
      try {
        cookies = parser.parse(host, conn.getPort(), getPath(), conn.isSecure(), header);
      } catch (MalformedCookieException e) {
        if (LOG.beWarning()) {
          LOG.warningT("Invalid cookie : '" + header.getValue() + "'. Error : " + e.getMessage());
        }
      }
      if (cookies != null) {
        for (Cookie cookie : cookies) {
          try {
            parser.validate(host, conn.getPort(), getPath(), conn.isSecure(), cookie);
            state.addCookie(cookie);
            if (LOG.beDebug()) {
              LOG.debugT("Cookie accepted : '" + parser.formatCookie(cookie) + "'");
            }
          } catch (MalformedCookieException e) {
            if (LOG.beWarning()) {
              LOG.warningT("Cookie rejected : '" + parser.formatCookie(cookie) + "'. Error : " + e.getMessage());
            }
          }
        }
      }
    }
  }

  /**
   * Reads the response from the specified {@link HttpConnection connection}.
   *
   * @param state the {@link HttpState state} information associated with this method
   * @param conn  the {@link HttpConnection connection} used to execute this HTTP method
   * @throws IOException if an I/O (transport) error occurs.
   */
  protected void readResponse(HttpState state, HttpConnection conn) throws IOException {
    // Status line & line may have already been received if 'expect - continue' handshake has been used
    while (this.statusLine == null) {
      readStatusLine(state, conn);
      readResponseHeaders(state, conn);
      processResponseHeaders(state, conn);
      int status = this.statusLine.getStatusCode();
      if ((status >= 100) && (status < 200)) { // discard this responses
        if (LOG.beInfo()) {
          LOG.infoT("Unexpected response : " + this.statusLine.toString());
        }
        this.statusLine = null;
      }
    }
    readResponseBody(state, conn);
    processResponseBody(state, conn);
  }

  /**
   * Read the response body from the specified {@link HttpConnection}.
   *
   * @param state the {@link HttpState state} information associated with this method
   * @param conn  the {@link HttpConnection connection} used to execute this HTTP method
   * @throws IOException if an I/O (transport) error occurs.
   */
  protected void readResponseBody(HttpState state, HttpConnection conn) throws IOException {
    InputStream stream = readResponseBody(conn);
    if (stream == null) { // done using this conenction
      responseBodyConsumed();
    } else {
      conn.setLastResponseInputStream(stream);
      setResponseStream(stream);
    }
  }

  /**
   * Returns the response body as an {@link InputStream incoming stream} corresponding to the values of the
   * <tt>Content-Length</tt> and <tt>Transfer-Encoding</tt> headers.
   *
   * @param conn the {@link HttpConnection connection} used to execute this HTTP method
   * @return the response input stream, <tt>null</tt> if not available
   * @throws IOException if an I/O (transport) error occurs.
   */
  private InputStream readResponseBody(HttpConnection conn) throws IOException {
    responseBody = null;
    InputStream is = conn.getInputStream();
    if (Dump.CONTENT_DUMP.enabled() || Dump.DEBUG) {
      is = new DumpInputStream(is, Dump.CONTENT_DUMP);
    }
    boolean canHaveBody = canResponseHaveBody(statusLine.getStatusCode());
    InputStream result = null;
    Header transferEncodingHeader = responseHeaders.getFirstHeader(Header.__TRANSFER_ENCODING);
    // (RFC2616, 4.4 / 3) if Transfer-Encoding is present we MUST ignore Content-Length.
    if (transferEncodingHeader != null) {
      String transferEncoding = transferEncodingHeader.getValue();

      if (!"chunked".equalsIgnoreCase(transferEncoding) && !"identity".equalsIgnoreCase(transferEncoding)) {
        if (LOG.beWarning()) {
          LOG.warningT("Unsupported transfer encoding : " + transferEncoding);
        }
      }
      HeaderElement[] encodings = transferEncodingHeader.getElements();
      // (RFC2616, 14.41) The chunked encoding must be the last one applied
      int len = encodings.length;
      if ((len > 0) && ("chunked".equalsIgnoreCase(encodings[len - 1].getName()))) {
        if (conn.isResponseAvailable(conn.getParams().getSoTimeout())) {
          result = new ChunkedInputStream(is, this);
        } else { // response body is empty
          if (getParams().isTrue(HttpClientParameters.STRICT_TRANSFER_ENCODING)) {
            throw new ProtocolException("Chunk-encoded body declared but not sent");
          } else {
            LOG.warningT("Chunk-encoded body missing");
          }
        }
      } else { // no chunked content
        LOG.infoT("Response content is not chunk-encoded");
        // (RFC 2616, 3.6) The connection must be terminated by closing the socket
        setConnectionCloseForced(true);
        result = is;
      }
    } else { // no Transfer-Encoding header
      long expectedLength = getResponseContentLength();
      if (expectedLength == -1) { // no Content-Length header
        if (canHaveBody && this.effectiveVersion.greaterEquals(HttpVersion.HTTP_1_1)) {
          Header connectionHeader = responseHeaders.getFirstHeader("Connection");
          String connectionDirective = null;
          if (connectionHeader != null) {
            connectionDirective = connectionHeader.getValue();
          }
          if (!"close".equalsIgnoreCase(connectionDirective)) {
            LOG.infoT("Response content length not known");
            setConnectionCloseForced(true);
          }
        }
        result = is;
      } else { // has content length
        result = new ContentLengthInputStream(is, expectedLength);
      }
    }

    // check for Content-Encoding header : "gzip" or "deflate"
    Header contentHeader = responseHeaders.getFirstHeader(Header._CONTENT_ENCODING);
    if (contentHeader != null) {
      String contentEncoding = contentHeader.getValue();
      LOG.infoT("Response content encoding is : " + contentEncoding);
      if ("gzip".equalsIgnoreCase(contentEncoding)) {  // the content is gzip encoded
        result = new GZIPInputStream(result);
      } else if ("deflate".equalsIgnoreCase(contentEncoding)) { // the content is deflate encoded
        result = new InflaterInputStream(result);
      }
    }

    if (!canHaveBody) { // not supposed to have a response body
      result = null;
    }

    if (result != null) {
      result = new CloseNotifyInputStream(result,
              new ResponseConsumedListener() {
                public void responseConsumed() {
                  responseBodyConsumed();
                }
              });

    }
    return result;
  }

  /**
   * Reads the response headers from the specified {@link HttpConnection connection}.
   *
   * @param state the {@link HttpState state} information associated with this method
   * @param conn  the {@link HttpConnection connection} used to execute this HTTP method
   * @throws IOException if an I/O (transport) error occurs.
   */
  protected void readResponseHeaders(HttpState state, HttpConnection conn) throws IOException {
    getResponseHeaderGroup().clear();
    ArrayList<Header> headers = HttpParser.parseHeaders(conn.getInputStream(), getParams().getHttpElementCharset());
    if (Dump.HEADER_DUMP.enabled() || Dump.DEBUG) {
      for (Header header : headers) {
        Dump.HEADER_DUMP.incoming(header.toText());
      }
    }
    getResponseHeaderGroup().setHeaders(headers);
  }

  /**
   * Read the status line from the specified {@link HttpConnection}
   *
   * @param state the {@link HttpState state} information associated with this method
   * @param conn  the {@link HttpConnection connection} used to execute this HTTP method
   * @throws IOException if an I/O (transport) error occurs.
   */
  protected void readStatusLine(HttpState state, HttpConnection conn) throws IOException {
    final int maxGarbageLines =
            getParams().getInt(HttpClientParameters.STATUS_LINE_GARBAGE_LIMIT, Integer.MAX_VALUE);

    int count = 0;
    String s;
    //read out the HTTP status string
    do {
      s = conn.readLine(getParams().getHttpElementCharset());
      if (s == null && count == 0) { // The server doesn't respond
        throw new NoHttpResponseException("The server " + conn.getHost() + " is not responding.");
      }
      if (Dump.HEADER_DUMP.enabled() || Dump.DEBUG) {
        Dump.HEADER_DUMP.incoming(s + "\r\n");
      }
      if (s != null && StatusLine.startsWithHTTP(s)) { // is this the status line
        break; // yes !
      } else if (s == null || count >= maxGarbageLines) {
        // we'll give up looking for the status line
        throw new ProtocolException("The server " + conn.getHost() + " is not responding with a valid HTTP response");
      }
      count++;
    } while (true);
    statusLine = new StatusLine(s);
    String versionStr = statusLine.getHttpVersion();
    if (getParams().isFalse(HttpClientParameters.UNAMBIGUOUS_STATUS_LINE) && versionStr.equals("HTTP")) {
      getParams().setVersion(HttpVersion.HTTP_1_0);
      if (LOG.beWarning()) {
        LOG.warningT("Ambiguous status line (HTTP version missing): " + statusLine.toString());
      }
    } else {
      this.effectiveVersion = HttpVersion.parse(versionStr);
    }
  }

  /**
   * Sends the request via the specified {@link HttpConnection connection}.
   *
   * @param state the {@link HttpState state} information associated with this method
   * @param conn  the {@link HttpConnection connection} used to execute this HTTP method
   * @throws IOException if an I/O (transport) error occurs.
   */
  protected void writeRequest(HttpState state, HttpConnection conn) throws IOException {
    writeRequestLine(state, conn);
    writeRequestHeaders(state, conn);
    conn.writeLine(); // end of head
    if (Dump.HEADER_DUMP.enabled() || Dump.DEBUG) {
      Dump.HEADER_DUMP.outgoing("\r\n");
    }
    HttpVersion ver = getParams().getVersion();
    Header expectheader = getRequestHeader(Header.EXPECT);
    String expectvalue = null;
    if (expectheader != null) {
      expectvalue = expectheader.getValue();
    }
    if ((expectvalue != null) && (expectvalue.compareToIgnoreCase("100-continue") == 0)) {
      if (ver.greaterEquals(HttpVersion.HTTP_1_1)) {
        conn.flushRequest(); // flush the request head (request line and headers)
        int readTimeout = conn.getParams().getSoTimeout();
        try {
          conn.setSocketTimeout(RESPONSE_WAIT_TIME_MS);
          readStatusLine(state, conn);
          readResponseHeaders(state, conn);
          processResponseHeaders(state, conn);
          if (this.statusLine.getStatusCode() == HttpStatus.SC_CONTINUE) {
            this.statusLine = null;
            LOG.debugT("100 (continue) received as expected");
          } else {
            return;
          }
        } catch (InterruptedIOException e) {
          if (e instanceof java.net.SocketTimeoutException) {
            removeRequestHeader("Expect"); // clear just in case
            LOG.infoT("100 (continue) read timeout. Resume sending the request");
          } else {
            throw e;
          }
        } finally {
          conn.setSocketTimeout(readTimeout); // restore socket timeout
        }
      } else { // version lesser than HTTP 1/1
        removeRequestHeader("Expect");
        LOG.infoT("'Expect: 100-continue' is only supported by HTTP/1.1 or higher");
      }
    }

    OutputStreamInterceptor osInterceptor = (OutputStreamInterceptor) getParams().getParameter(Parameters.OUTPUT_STREAM_INTERCEPTOR);
    if (osInterceptor != null) {
      osInterceptor.writeRequestBodyToStream(conn.getOutputStream());
    } else {
      writeRequestBody(state, conn);
    }
    conn.flushRequest(); // flush the request
  }

  /**
   * Writes the request body to the specified {@link HttpConnection connection}.
   *
   * @param state the {@link HttpState state} information associated with this method
   * @param conn  the {@link HttpConnection connection} used to execute this HTTP method
   * @return <tt>true</tt>
   * @throws IOException if an I/O (transport) error occurs.
   */
  protected boolean writeRequestBody(HttpState state, HttpConnection conn) throws IOException {
    return true;
  }

  /**
   * Writes the request headers to the specified {@link HttpConnection connection}.
   *
   * @param state the {@link HttpState state} information associated with this method
   * @param conn  the {@link HttpConnection connection} used to execute this HTTP method
   * @throws IOException   if an I/O (transport) error occurs.
   * @throws HttpException if a net exception occurs.
   */
  protected void writeRequestHeaders(HttpState state, HttpConnection conn) throws IOException {
    addRequestHeaders(state, conn);
    String charset = getParams().getHttpElementCharset();
    ArrayList<Header> headers = getRequestHeaders();
    for (Header header : headers) {
      String s = header.toText();
      if (Dump.HEADER_DUMP.enabled() || Dump.DEBUG) {
        Dump.HEADER_DUMP.outgoing(s);
      }
      conn.print(s, charset);
    }
  }

  /**
   * Writes the request line to the specified {@link HttpConnection connection}.
   *
   * @param state the {@link HttpState state} information associated with this method
   * @param conn  the {@link HttpConnection connection} used to execute this HTTP method
   * @throws IOException   if an I/O (transport) error occurs.
   * @throws HttpException if a net exception occurs.
   */
  protected void writeRequestLine(HttpState state, HttpConnection conn) throws IOException {
    String requestLine = getRequestLine(conn);
    if (Dump.HEADER_DUMP.enabled() || Dump.DEBUG) {
      Dump.HEADER_DUMP.outgoing(requestLine);
    }
    conn.print(requestLine, getParams().getHttpElementCharset());
  }

  /**
   * Returns the request line.
   *
   * @param conn the {@link HttpConnection connection} used to execute
   *             this HTTP method
   * @return The request line.
   */
  private String getRequestLine(HttpConnection conn) {
    return generateRequestLine(conn, getName(), getPath(), getQuery(), effectiveVersion.toString());
  }

  /**
   * Returns the method parameters.
   *
   * @return HTTP parameters.
   */
  public HttpClientParameters getParams() {
    return this.params;
  }

  /**
   * Sets the method parameters.
   */
  public void setParams(final HttpClientParameters params) {
    if (params == null) {
      throw new IllegalArgumentException("Parameters is null");
    }
    this.params = params;
  }

  /**
   * Returns the HTTP version used with this method.
   *
   * @return HTTP version, <tt>null</tt> if not specified (the method has not been executed)
   */
  public HttpVersion getEffectiveVersion() {
    return this.effectiveVersion;
  }

  /**
   * Checks whether the response can have a body.
   *
   * @param status - the HTTP status code
   * @return <tt>true</tt> if the message may contain a body, <tt>false</tt> if it can not
   */
  private static boolean canResponseHaveBody(int status) {
    // 100-CONTINUE, 204-No Content, 304-Not Modified
    return !((status >= 100 && status <= 199) || (status == 204) || (status == 304));
  }

  /**
   * Returns the character set from the <tt>Content-Type</tt> header.
   *
   * @param contentheader The content header.
   * @return String The character set.
   */
  protected String getContentCharSet(Header contentheader) {
    String charset = null;
    if (contentheader != null) {
      HeaderElement values[] = contentheader.getElements();
      if (values.length == 1) { // should be only 1
        NameValuePair param = values[0].getParameterByName("charset");
        if (param != null) {
          charset = param.getValue();
        }
      }
    }
    if (charset == null) {
      charset = getParams().getContentCharset();
      if (LOG.beDebug()) {
        LOG.debugT("Default charset used : " + charset);
      }
    }
    return charset;
  }

  /**
   * Returns the character encoding of the request from the <tt>Content-Type</tt> header.
   *
   * @return String The character set.
   */
  public String getRequestCharSet() {
    return getContentCharSet(getRequestHeader(Header._CONTENT_TYPE));
  }

  /**
   * Returns the character encoding of the response from the <tt>Content-Type</tt> header.
   *
   * @return String The character set.
   */
  public String getResponseCharSet() {
    return getContentCharSet(getResponseHeader(Header._CONTENT_TYPE));
  }

  /**
   * The response has been consumed.
   * <p/>
   * <p>The default behavior for this class is to check to see if the connection
   * should be closed, and close if need be, and to ensure that the connection
   * is returned to the connection manager - if and only if we are not still
   * inside the execute call.</p>
   */
  protected void responseBodyConsumed() {
    responseStream = null;
    if (responseConnection != null) { // not closed
      responseConnection.setLastResponseInputStream(null);
      if (shouldCloseConnection(responseConnection)) {
        responseConnection.close();
      } else {
        try {
          if (responseConnection.isResponseAvailable()) { // has some more to read, not ok... will close
            if (getParams().isTrue(HttpClientParameters.WARN_EXTRA_INPUT)) {
              LOG.warningT("Extra response data detected - closing connection");
            }
            responseConnection.close();
          }
        } catch (IOException e) {
          LOG.warningT(e.getMessage());
          responseConnection.close();
        }
      }
    }
    this.connectionCloseForced = false;
    ensureConnectionRelease();
  }

  /**
   * Insure that the connection is released back to the pool.
   */
  private void ensureConnectionRelease() {
    if (responseConnection != null) {
      responseConnection.releaseConnection();
      responseConnection = null;
    }
  }

  /**
   * Sets the connect response
   *
   * @param statusline      connect response status line
   * @param responseheaders connect response headers
   * @param responseStream  connect response response stream
   */
  void setConnectResponse(StatusLine statusline, HeaderGroup responseheaders, InputStream responseStream) {
    this.used = true;
    this.statusLine = statusline;
    this.responseHeaders = responseheaders;
    this.responseBody = null;
    this.responseStream = responseStream;
  }

  /**
   * Returns the target host {@link AuthState authentication state}
   *
   * @return host authentication state
   */
  public AuthState getHostAuthState() {
    return this.hostAuthState;
  }

  /**
   * Returns the proxy {@link AuthState authentication state}
   *
   * @return host authentication state
   */
  public AuthState getProxyAuthState() {
    return this.proxyAuthState;
  }

  /**
   * Tests whether the execution of this method has been aborted
   *
   * @return <tt>true</tt> if the execution of this method has been aborted,
   *         <tt>false</tt> otherwise
   */
  public boolean isAborted() {


    return this.aborted;
  }

  /**
   * Returns <tt>true</tt> if the HTTP has been sent to the target server, <tt>false</tt> otherwise.
   *
   * @return <tt>true</tt> if the request has been sent, <tt>false</tt> otherwise
   */
  public boolean isRequestSent() {
    return this.requestSent;
  }

  protected void finalize() throws Throwable {
    ensureConnectionRelease();
    super.finalize();
  }

}

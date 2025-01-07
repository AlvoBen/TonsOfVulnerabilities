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

/**
 * This interface represents a collection of parameters.
 *
 * @author Nikolai Neichev
 */
public interface Parameters {

  /**
   * Defines the request headers to be sent per default with each request.
   * <p/>
   * This parameter expects a value of type {@link java.util.Collection}. The
   * collection is expected to contain {@link com.sap.httpclient.http.Header}s.
   * </p>
   */
  public static final String DEFAULT_HEADERS = "default-headers";

  /**
   * Sets the timeout in milliseconds used when retrieving an
   * {@link com.sap.httpclient.net.connection.HttpConnection HTTP connection} from the
   * {@link com.sap.httpclient.net.connection.HttpConnectionManager HTTP connection manager}.
   * <p/>
   * This parameter expects a value of type {@link Long}.
   * </p>
   */
  public static final String CONNECTION_MANAGER_TIMEOUT = "connection-manager-timeout";

  /**
   * Defines the default
   * {@link com.sap.httpclient.net.connection.HttpConnectionManager HTTP connection manager} class.
   * <p/>
   * This parameter expects a value of type {@link Class}.
   * </p>
   */
  public static final String CONNECTION_MANAGER_CLASS = "connection-manager-class";

  /**
   * Defines whether authentication should be attempted preemptively.
   * <p/>
   * This parameter expects a value of type {@link Boolean}.
   * </p>
   */
  public static final String PREEMPTIVE_AUTHENTICATION = "preemptive-authentication";

  /**
   * Defines whether relative redirects should be rejected.
   * <p/>
   * This parameter expects a value of type {@link Boolean}.
   * </p>
   */
  public static final String REJECT_RELATIVE_REDIRECT = "reject-relative-redirect";

  /**
   * Defines the maximum number of redirects to be followed.
   * The limit on number of redirects is intended to prevent infinite loops.
   * <p/>
   * This parameter expects a value of type {@link Integer}.
   * </p>
   */
  public static final String MAX_REDIRECTS = "max-redirects";

  /**
   * Defines whether circular redirects (redirects to the same location) should be allowed.
   * The HTTP spec is not sufficiently clear whether circular redirects are permitted,
   * therefore optionally they can be enabled
   * <p/>
   * This parameter expects a value of type {@link Boolean}.
   * </p>
   */
  public static final String ALLOW_CIRCULAR_REDIRECTS = "allow-circular-redirects";

    /**
   * Defines the maximum number of connections allowed per host configuration.
   * These values only apply to the number of connections from a particular instance
   * of HttpConnectionManager.
   * <p/>
   * This parameter expects a value of type {@link java.util.Map}.  The value
   * should map instances of {@link com.sap.httpclient.HostConfiguration}
   * to {@link Integer integers}.  The default value can be specified using
   * {@link com.sap.httpclient.HostConfiguration#ANY_HOST_CONFIGURATION}.
   * </p>
   */
  public static final String MAX_HOST_CONNECTIONS = "max-host-connection";

  /**
   * Defines the maximum number of connections allowed overall. This value only applies
   * to the number of connections from a particular instance of HttpConnectionManager.
   * <p/>
   * This parameter expects a value of type {@link Integer}.
   * </p>
   */
  public static final String MAX_TOTAL_CONNECTIONS = "max-total-connection";

  /**
   * Determines whether Nagle's algorithm is to be used. The Nagle's algorithm
   * tries to conserve bandwidth by minimizing the number of segments that are
   * sent. When applications wish to decrease network latency and increase
   * performance, they can disable Nagle's algorithm (that is enable TCP_NODELAY).
   * Data will be sent earlier, at the cost of an increase in bandwidth consumption.
   * <p/>
   * This parameter expects a value of type {@link Boolean}.
   * </p>
   */
  public static final String TCP_NODELAY = "tcp-nodelay";

  /**
   * Determines a hint the size of the underlying buffers used by the platform
   * for outgoing network I/O. This value is a suggestion to the kernel from
   * the application about the size of buffers to use for the data to be sent
   * over the socket.
   * <p/>
   * This parameter expects a value of type {@link Integer}.
   * </p>
   */
  public static final String SO_SNDBUF = "socket-sendbuffer";

  /**
   * Determines a hint the size of the underlying buffers used by the platform
   * for incoming network I/O. This value is a suggestion to the kernel from
   * the application about the size of buffers to use for the data to be received
   * over the socket.
   * <p/>
   * This parameter expects a value of type {@link Integer}.
   * </p>
   */
  public static final String SO_RCVBUF = "socket-receivebuffer";

  /**
   * Sets SO_LINGER with the specified linger time in seconds. The maximum timeout
   * value is platform specific. Value <tt>0</tt> implies that the option is disabled.
   * Value <tt>-1</tt> implies that the JRE default is used. The setting only affects
   * socket close.
   * <p/>
   * This parameter expects a value of type {@link Integer}.
   * </p>
   */
  public static final String SO_LINGER = "socket-linger";

  /**
   * Determines the timeout until a connection is etablished. A value of zero
   * means the timeout is not used. The default value is zero.
   * <p/>
   * This parameter expects a value of type {@link Integer}.
   * </p>
   */
  public static final String CONNECTION_TIMEOUT = "connection-timeout";

  /**
   * Determines whether stale connection check is to be used. Disabling
   * stale connection check may result in slight performance improvement
   * at the risk of getting an I/O error when executing a request over a
   * connection that has been closed at the server side.
   * <p/>
   * This parameter expects a value of type {@link Boolean}.
   * </p>
   */
  public static final String STALE_CONNECTION_CHECK = "stale-connection-check";

/**
   * Defines the content of the <tt>User-Agent</tt> header used by
   * {@link com.sap.httpclient.HttpMethod HTTP methods}.
   * <p/>
   * This parameter expects a value of type {@link String}.
   * </p>
   */
  public static final String USER_AGENT = "user-agent";

  /**
   * Defines the {@link com.sap.httpclient.http.HttpVersion HTTP net version} used by
   * {@link com.sap.httpclient.HttpMethod HTTP methods} per
   * default.
   * <p/>
   * This parameter expects a value of type {@link com.sap.httpclient.http.HttpVersion}.
   * </p>
   */
  public static final String PROTOCOL_VERSION = "net-version";

  /**
   * Defines whether {@link com.sap.httpclient.HttpMethod HTTP methods} should
   * reject ambiguous {@link com.sap.httpclient.http.StatusLine HTTP status line}.
   * <p/>
   * This parameter expects a value of type {@link Boolean}.
   * </p>
   */
  public static final String UNAMBIGUOUS_STATUS_LINE = "unambiguous-statusline";

  /**
   * Defines whether {@link com.sap.httpclient.http.cookie.Cookie cookies} should be put on
   * a single {@link com.sap.httpclient.http.Header response header}.
   * <p/>
   * This parameter expects a value of type {@link Boolean}.
   * </p>
   */
  public static final String SINGLE_COOKIE_HEADER = "single-cookie-header";

  /**
   * Defines whether COPY and MOVE requests should use the overwrite header allways
   * <p/>
   * This parameter expects a value of type {@link Boolean}.
   * </p>
   */
  public static final String USE_OVERWRITE_HEADER = "use-overwrite-header";

  /**
   * Defines whether to include the Depth header even if not nessesary, because of a specified default behaviour
   * <p/>
   * This parameter expects a value of type {@link Boolean}.
   * </p>
   */
  public static final String USE_DEPTH_HEADER = "use-depth-header";

  /**
   * Defines whether responses with an invalid <tt>Transfer-Encoding</tt> header should be rejected.
   * <p/>
   * This parameter expects a value of type {@link Boolean}.
   * </p>
   */
  public static final String STRICT_TRANSFER_ENCODING = "strict-transfer-encoding";

  /**
   * Defines whether the content body sent in response to
   * {@link com.sap.httpclient.http.methods.HEAD} should be rejected.
   * <p/>
   * This parameter expects a value of type {@link Boolean}.
   * </p>
   */
  public static final String REJECT_HEAD_BODY = "reject-head-body";

  /**
   * Sets period of time in milliseconds to wait for a content body sent in response to
   * {@link com.sap.httpclient.http.methods.HEAD HEAD method} from a non-compliant server.
   * If the parameter is not set or set to <tt>-1</tt> non-compliant response body check is disabled.
   * <p/>
   * This parameter expects a value of type {@link Integer}.
   * </p>
   */
  public static final String HEAD_BODY_CHECK_TIMEOUT = "head-body-check-timeout";

  /**
   * <p/>
   * Activates 'Expect: 100-Continue' handshake for the
   * {@link com.sap.httpclient.http.methods.ExpectingContinueRequest entity enclosing methods}.
   * The purpose of the 'Expect: 100-Continue' handshake to allow a client that is sending a request message
   * with a request body to determine if the origin server is willing to accept the request
   * (based on the request headers) before the client sends the request body.
   * </p>
   * The use of the 'Expect: 100-continue' handshake can result in noticable peformance improvement for
   * entity enclosing requests(such as POST and PUT) that require the target server's authentication.
   * </p>
   * 'Expect: 100-continue' handshake should be used with caution, as it may cause problems with HTTP servers
   * and proxies that do not support HTTP/1.1 net.
   * </p>
   * This parameter expects a value of type {@link Boolean}.
   */
  public static final String USE_EXPECT_CONTINUE = "use-expect-continue";

  /**
   * Defines the charset to be used when encoding {@link com.sap.httpclient.auth.Credentials}.
   * If not defined then the {@link #HTTP_ELEMENT_CHARSET} should be used.
   * <p/>
   * This parameter expects a value of type {@link String}.
   * </p>
   */
  public static final String CREDENTIAL_CHARSET = "credential-charset";

  /**
   * Defines the charset to be used for encoding HTTP net elements.
   * <p/>
   * This parameter expects a value of type {@link String}.
   * </p>
   */
  public static final String HTTP_ELEMENT_CHARSET = "element-charset";

  /**
   * Defines the charset to be used for encoding content body.
   * <p/>
   * This parameter expects a value of type {@link String}.
   * </p>
   */
  public static final String HTTP_CONTENT_CHARSET = "content-charset";

  /**
   * Defines {@link com.sap.httpclient.http.cookie.CookiePolicy cookie policy} to be used for cookie management.
   * <p/>
   * This parameter expects a value of type {@link String}.
   * </p>
   */
  public static final String COOKIE_POLICY = "cookie-policy";

  /**
   * Defines HttpClient's behavior when a response provides more bytes than
   * expected (specified with Content-Length, for example).
   * <p/>
   * Such surplus data makes the HTTP connection unreliable for keep-alive requests, as malicious response
   * data (faked headers etc.) can lead to undesired results on the next request using that connection.
   * </p>
   * If this parameter is set to <code>true</code>, any detection of extra
   * incoming data will generate a warning in the log.
   * <p/>
   * This parameter expects a value of type {@link Boolean}.
   * </p>
   */
  public static final String WARN_EXTRA_INPUT = "warn-extra-incoming";

  /**
   * Defines the maximum number of ignorable lines before we expect a HTTP response's status code.
   * <p/>
   * With HTTP/1.1 persistent connections, the problem arises that broken scripts could return a wrong
   * Content-Length(there are more bytes sent than specified). Unfortunately, in some cases, this is not
   * possible after the bad response, but only before the next one.
   * So, HttpClient must be able to skip those surplus lines this way.
   * </p>
   * Set this to 0 to disallow any garbage/empty lines before the status line.<br />
   * To specify no limit, use {@link java.lang.Integer#MAX_VALUE} (default in lenient mode).
   * <p/>
   * This parameter expects a value of type {@link Integer}.
   */
  public static final String STATUS_LINE_GARBAGE_LIMIT = "status-line-garbage-limit";

  /**
   * Sets the socket timeout (<tt>SO_TIMEOUT</tt>) in milliseconds to be used when executing the method.
   * A timeout value of zero is interpreted as an infinite timeout.
   * <p/>
   * This parameter expects a value of type {@link Integer}.
   * </p>
   */
  public static final String SO_TIMEOUT = "socket-timeout";

  /**
   * The key used to look up the date patterns used for parsing. The String patterns are stored
   * in a {@link java.util.Collection} and must be compatible with
   * {@link java.text.SimpleDateFormat}.
   * <p/>
   * This parameter expects a value of type {@link java.util.Collection}.
   * </p>
   */
  public static final String DATE_PATTERNS = "date-patterns";

  /**
   * Sets the method retry handler parameter.
   * <p/>
   * This parameter expects a value of type {@link com.sap.httpclient.HttpMethodRetryHandler}.
   * </p>
   */
  public static final String RETRY_HANDLER = "retry-handler";

  /**
   * Sets the maximum buffered response size (in bytes) that triggers no warning. Buffered
   * responses exceeding this size will trigger a warning in the log.
   * <p/>
   * This parameter expects a value if type {@link Integer}.
   * </p>
   */
  public static final String BUFFER_WARN_TRIGGER_LIMIT = "response-buffer-warnlimit";

  /**
   * Defines the virtual host name.
   * <p/>
   * This parameter expects a value of type {@link java.lang.String}.
   * </p>
   */
  public static final String VIRTUAL_HOST = "virtual-host";

  /**
   * Sets the value to use as the multipart boundary.
   * <p/>
   * This parameter expects a value if type {@link String}.
   * </p>
   */
  public static final String MULTIPART_BOUNDARY = "multipart-boundary";

  /**
   * Defines whether the Host header is send with each request.
   * <p/>
   * This parameter expects a value if type {@link Boolean}.
   * </p>
   */
  public static final String DISABLE_HOST_SENDING = "disable-host-sending";

  /**
   * Defines whether to encode the data with the specified encoding("gzip" or "deflate").
   * <p/>
   * This parameter expects a value if type {@link String}.
   * </p>
   */
  public static final String FORCE_CONTENT_ENCODING = "force-content-encoding";

  /**
   * This parameter passes a output stream interceptor to the http method
   * <p/>
   * This parameter expects a value if type {@link com.sap.httpclient.utils.OutputStreamInterceptor}.
   * </p>
   */
  public static final String OUTPUT_STREAM_INTERCEPTOR = "output-stream-interceptor";

  /**
   * This parameter sets strict character behaviout to urls
   * <p/>
   * This parameter expects a value if type {@@link Boolean}.
   * </p>
   */
  public static final String ENCODE_URLS = "encode-urls";

  /**
   * This parameter enable/disable the cache
   * <p>
   * This parameter enable/disable the cache {@@link Boolean}.
   * </p>
   */
  public static final String CACHE_ENABLED = "cache-enabled";

  /**
   * This parameter set memory cache size in MB
   * <p>
   * This parameter set memory cache size in MB {@@link Integer}.
   * </p>
   */
  public static final String CACHE_MAX_MEMORY_SIZE = "cache-max-memory-size";

  /**
   * This parameter set disk cache size in MB
   * <p>
   * This parameter set disk cache size in MB {@@link Integer}.
   * </p>
   */
  public static final String CACHE_MAX_DISK_SIZE = "cache-max-disk-size";

  /**
   * This parameter set output cache directory
   * <p>
   * This parameter set output cache directory {@@link String}.
   * </p>
   */
  public static final String CACHE_PATH_DIR = "cache-path-dir";

  /**
   * Returns a default set of properties
   *
   * @return the default parameters
   */
  public Parameters getDefaults();

  /**
   * Sets the default parameters
   *
   * @param params the default parameters
   */
  public void setDefaults(final Parameters params);

  /**
   * Returns a parameter value with the specified name.
   * If the parameter is not explicitly set, <tt>null</tt> value is returned.
   *
   * @param name the parameter name.
   * @return an object that represents the value of the parameter.
   */
  public Object getParameter(final String name);

  /**
   * Assigns the value to the parameter with the specified name
   *
   * @param name  parameter name
   * @param value parameter value
   */
  public void setParameter(final String name, final Object value);

  /**
   * Returns a {@link Long} parameter value with the specified name.
   * If the parameter is not explicitly set, the default value is returned.
   *
   * @param name the parent name.
   * @param defaultValue the default value.
   * @return a {@link Long} that represents the value of the parameter.
   */
  public long getLong(final String name, long defaultValue);

  /**
   * Returns an {@link Integer} parameter value with the specified name.
   * If the parameter is not explicitly set, the default value is returned.
   *
   * @param name the parent name.
   * @param defaultValue the default value.
   * @return a {@link Integer} that represents the value of the parameter.
   */
  public int getInt(final String name, int defaultValue);

  /**
   * Returns a {@link Double} parameter value with the specified name.
   * If the parameter is not explicitly set, the default value is returned.
   *
   * @param name the parent name.
   * @param defaultValue the default value.
   * @return a {@link Double} that represents the value of the parameter.
   */
  public double getDouble(final String name, double defaultValue);

  /**
   * Returns a {@link Boolean} parameter value with the specified name.
   * If the parameter is not explicitly set, the default value is returned.
   *
   * @param name the parent name.
   * @param defaultValue the default value.
   * @return a {@link Boolean} that represents the value of the parameter.
   */
  public boolean getBoolean(final String name, boolean defaultValue);

  /**
   * Returns <tt>true</tt> if the parameter is set, <tt>false</tt> otherwise.
   *
   * @param name parameter name
   * @return <tt>true</tt> if the parameter is set, <tt>false</tt> otherwise.
   */
  public boolean isSet(final String name);

  /**
   * Returns <tt>true</tt> if the parameter is set and is <tt>true</tt>, <tt>false</tt> otherwise.
   *
   * @param name parameter name
   * @return <tt>true</tt> if the parameter is set and is <tt>true</tt>, <tt>false</tt> otherwise.
   */
  public boolean isTrue(final String name);

  /**
   * Returns <tt>true</tt> if the parameter is either not set or is <tt>false</tt>, <tt>false</tt> otherwise.
   *
   * @param name parameter name
   * @return <tt>true</tt> if the parameter is either not set or is <tt>false</tt>, <tt>false</tt> otherwise.
   */
  public boolean isFalse(final String name);

}
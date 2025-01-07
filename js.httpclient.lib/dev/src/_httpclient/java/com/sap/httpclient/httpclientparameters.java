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

import com.sap.httpclient.http.HttpVersion;
import com.sap.httpclient.http.cookie.CookiePolicy;
import com.sap.httpclient.utils.DateParser;
import com.sap.httpclient.net.connection.HttpConnectionManager;
import com.sap.httpclient.net.connection.HttpConnectionManagerImpl;
import com.sap.tc.logging.Location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds all the parameters
 *
 * @author Nikolai Neichev
 */
public class HttpClientParameters implements Parameters, Serializable, Cloneable{

  private Parameters defaults;

  /**
   * Hash map of the contained parameters
   */
  private HashMap<String, Object> parameters = null;

  /**
   * Log object for this class.
   */
  private static final Location LOG = Location.getLocation(HttpClientParameters.class);

  public synchronized Parameters getDefaultParams() {
    if (defaults == null) {
      defaults = createParams();
    }
    return defaults;
  }

  /**
   * Creates a default set of parameters
   * @return the default parameters
   */
  protected Parameters createParams() {
    HttpClientParameters params = new HttpClientParameters(null);
    params.setParameter(Parameters.USER_AGENT, "SAP HttpClient v1.0");
    params.setVersion(HttpVersion.HTTP_1_1);
    params.setConnectionManagerClass(HttpConnectionManagerImpl.class);
    params.setCookiePolicy(CookiePolicy.RFC_2109);
    params.setHttpElementCharset("US-ASCII");
    params.setContentCharset("ISO-8859-1");
    params.setParameter(Parameters.RETRY_HANDLER, new HttpMethodRetryHandler());
    ArrayList<String> datePatterns = new ArrayList<String>();
    datePatterns.addAll(Arrays.asList(new String[]{
      DateParser.DATE_PATTERN_RFC1123,
      DateParser.DATE_PATTERN_RFC1036,
      DateParser.DATE_PATTERN_ASCTIME,
      "EEE, dd-MMM-yyyy HH:mm:ss z",
      "EEE, dd-MMM-yyyy HH-mm-ss z",
      "EEE, dd MMM yy HH:mm:ss z",
      "EEE dd-MMM-yyyy HH:mm:ss z",
      "EEE dd MMM yyyy HH:mm:ss z",
      "EEE dd-MMM-yyyy HH-mm-ss z",
      "EEE dd-MMM-yy HH:mm:ss z",
      "EEE dd MMM yy HH:mm:ss z",
      "EEE,dd-MMM-yy HH:mm:ss z",
      "EEE,dd-MMM-yyyy HH:mm:ss z",
      "EEE, dd-MM-yyyy HH:mm:ss z",
    }));
    params.setParameter(Parameters.DATE_PATTERNS, datePatterns);
    return params;
  }

  /**
   * Creates a new parameters object with a specified defaults.
   *
   * @param defaults the defaults.
   */
  public HttpClientParameters(final Parameters defaults) {
    this.defaults = defaults;
  }

  /**
   * Creates a new parameters object with the default values.
   */
  public HttpClientParameters() {
    this.defaults = getDefaultParams();
  }

  /**
   * Getter.
   * @return the default parameters
   */
  public synchronized Parameters getDefaults() {
    return this.defaults;
  }

  /**
   * Setter
   * @param params the default params
   */
  public synchronized void setDefaults(final Parameters params) {
    this.defaults = params;
  }

  /**
   * Gets the parameter value assosiated witn the specified name
   * @param name the name
   * @return the parameter value
   */
  public synchronized Object getParameter(final String name) {
    Object param = null;
    if (this.parameters != null) {
      param = this.parameters.get(name);
    }
    if (param != null) { // the parameter has been explicitly defined
      return param;
    } else { // the parameter is not found, checking defaults
      if (this.defaults != null) {
        return this.defaults.getParameter(name); // return default parameter value
      } else { // bad luck :(
        return null;
      }
    }
  }

  /**
   * Removes a parameter
   * @param name the parameter name
   */
  public synchronized void removeParameter(final String name) {
    if (this.parameters != null) {
      this.parameters.remove(name);
      if (LOG.beDebug()) {
        LOG.debugT("Remove parameter " + name);
      }
    }
  }

  /**
   * Sets a parameter
   * @param name the parameter name
   * @param value the parameter value
   */
  public synchronized void setParameter(final String name, final Object value) {
    if (this.parameters == null) {
      this.parameters = new HashMap<String, Object>();
    }
    this.parameters.put(name, value);
    if (LOG.beDebug()) {
      LOG.debugT("Set parameter " + name + " = " + value);
    }
  }

  /**
   * Assigns the value to all the parameter with the specified names
   *
   * @param names array of parameter name
   * @param value parameter value
   */
  public synchronized void setParameters(final String[] names, final Object value) {
    for (String name : names) {
      setParameter(name, value);
    }
  }

  public long getLong(final String name, long defaultValue) {
    Object param = getParameter(name);
    if (param == null) {
      return defaultValue;
    }
    return (Long) param;
  }

  public int getInt(final String name, int defaultValue) {
    Object param = getParameter(name);
    if (param == null) {
      return defaultValue;
    }
    return (Integer) param;
  }

  public double getDouble(final String name, double defaultValue) {
    Object param = getParameter(name);
    if (param == null) {
      return defaultValue;
    }
    return (Double) param;
  }

  public boolean getBoolean(final String name, boolean defaultValue) {
    Object param = getParameter(name);
    if (param == null) {
      return defaultValue;
    }
    return (Boolean) param;
  }

  public boolean isSet(final String name) {
    return getParameter(name) != null;
  }

  public boolean isSetLocally(final String name) {
    return this.parameters != null && this.parameters.get(name) != null;
  }

  public boolean isTrue(final String name) {
    return getBoolean(name, false);
  }

  public boolean isFalse(final String name) {
    return !getBoolean(name, false);
  }

  /**
   * Removes all parameters from this collection.
   */
  public void clear() {
    this.parameters = null;
  }

  /**
   * Clones this parameters. The paramter values themselves are not cloned.
   */
  @SuppressWarnings({"CloneDoesntCallSuperClone"})
	public Object clone() throws CloneNotSupportedException {
    HttpClientParameters clone = new HttpClientParameters(this.defaults);
    if (this.parameters != null) {
			clone.parameters = new HashMap<String, Object>();
			clone.parameters.putAll(this.parameters);
    }
    return clone;
  }

  /**
   * Returns the timeout in milliseconds used when retrieving an
   * {@link com.sap.httpclient.net.connection.HttpConnection HTTP connection} from the
   * {@link com.sap.httpclient.net.connection.HttpConnectionManager HTTP connection manager}.
   *
   * @return timeout in milliseconds.
   */
  public long getConnectionManagerTimeout() {
    return getLong(CONNECTION_MANAGER_TIMEOUT, 0);
  }

  /**
   * Sets the timeout in milliseconds used when retrieving an
   * {@link com.sap.httpclient.net.connection.HttpConnection HTTP connection} from the
   * {@link com.sap.httpclient.net.connection.HttpConnectionManager HTTP connection manager}.
   *
   * @param timeout the timeout in milliseconds
   */
  public void setConnectionManagerTimeout(long timeout) {
    setParameter(CONNECTION_MANAGER_TIMEOUT, timeout);
  }

  /**
   * Returns the default {@link com.sap.httpclient.net.connection.HttpConnectionManager HTTP connection manager} class.
   *
   * @return {@link com.sap.httpclient.net.connection.HttpConnectionManager HTTP connection manager} factory class.
   */
  public Class getConnectionManagerClass() {
    return (Class) getParameter(CONNECTION_MANAGER_CLASS);
  }

  /**
   * Sets {@link com.sap.httpclient.net.connection.HttpConnectionManager HTTP connection manager}
   * class to be used der default.
   *
   * @param clazz {@link com.sap.httpclient.net.connection.HttpConnectionManager HTTP connection manager} factory class.
   */
  public void setConnectionManagerClass(Class<HttpConnectionManagerImpl> clazz) {
    setParameter(CONNECTION_MANAGER_CLASS, clazz);
  }

  /**
   * Returns <tt>true</tt> if authentication should be attempted preemptively, <tt>false</tt> otherwise.
   *
   * @return <tt>true</tt> if authentication should be attempted preemptively, <tt>false</tt> otherwise.
   */
  public boolean isAuthenticationPreemptive() {
    return getBoolean(PREEMPTIVE_AUTHENTICATION, false);
  }

  /**
   * Sets whether authentication should be attempted preemptively.
   *
   * @param value <tt>true</tt> if authentication should be attempted preemptively, <tt>false</tt> otherwise.
   */
  public void setAuthenticationPreemptive(boolean value) {
    setParameter(PREEMPTIVE_AUTHENTICATION, value);
  }

  /**
   * Sets the default maximum number of connections allowed for a specified host config.
   *
   * @param maxHostConnections The default maximum.
   */
  public void setDefaultMaxConnectionsPerHost(int maxHostConnections) {
    setMaxConnectionsPerHost(HostConfiguration.ANY_HOST_CONFIGURATION, maxHostConnections);
  }

  /**
   * Sets the maximum number of connections to be used for the specified host config.
   *
   * @param hostConfiguration  The host config to set the maximum for.  Use
   *  {@link HostConfiguration#ANY_HOST_CONFIGURATION} to configure the default value per host.
   * @param maxHostConnections The maximum number of connections, <code>> 0</code>
   */
  public void setMaxConnectionsPerHost(HostConfiguration hostConfiguration, int maxHostConnections) {
    if (maxHostConnections <= 0) {
      throw new IllegalArgumentException("maxHostConnections must be greater than 0");
    }
    HashMap currentValues = (HashMap) getParameter(MAX_HOST_CONNECTIONS);
    // param values are meant to be immutable so we'll make a copy to modify
    HashMap<Object, Object> newValues;
    if (currentValues == null) {
      newValues = new HashMap<Object, Object>();
    } else {
      newValues = new HashMap<Object, Object>(currentValues);
    }
    newValues.put(hostConfiguration, maxHostConnections);
    setParameter(MAX_HOST_CONNECTIONS, newValues);
  }

  /**
   * Gets the default maximum number of connections allowed for a specified host config.
   *
   * @return The default maximum.
   */
  public int getDefaultMaxConnectionsPerHost() {
    return getMaxConnectionsPerHost(HostConfiguration.ANY_HOST_CONFIGURATION);
  }

  /**
   * Gets the maximum number of connections to be used for a particular host config.  If
   * the value has not been specified for the specified host the default value will be returned.
   *
   * @param hostConfiguration The host config.
   * @return The maximum number of connections to be used for the specified host config.
   */
  public int getMaxConnectionsPerHost(HostConfiguration hostConfiguration) {
    Map m = (Map) getParameter(MAX_HOST_CONNECTIONS);
    if (m == null) {
      // MAX_HOST_CONNECTIONS have not been configured, using the default value
      return HttpConnectionManager.DEFAULT_MAX_HOST_CONNECTIONS;
    } else {
      Integer max = (Integer) m.get(hostConfiguration);
      if (max == null && hostConfiguration != HostConfiguration.ANY_HOST_CONFIGURATION) {
        // the value has not been configured specifically for this host config,
        // use the default value
        return getMaxConnectionsPerHost(HostConfiguration.ANY_HOST_CONFIGURATION);
      } else {
        return (
                max == null
                ? HttpConnectionManager.DEFAULT_MAX_HOST_CONNECTIONS
                : max
				);
      }
    }
  }

  /**
   * Sets the maximum number of connections allowed.
   *
   * @param maxTotalConnections The maximum number of connections allowed.
   */
  public void setMaxTotalConnections(int maxTotalConnections) {
    setParameter(Parameters.MAX_TOTAL_CONNECTIONS, maxTotalConnections);
  }

  /**
   * Gets the maximum number of connections allowed.
   *
   * @return The maximum number of connections allowed.
   */
  public int getMaxTotalConnections() {
    return getInt(Parameters.MAX_TOTAL_CONNECTIONS,
            HttpConnectionManager.DEFAULT_MAX_TOTAL_CONNECTIONS);
  }

  /**
   * Returns the default socket timeout (<tt>SO_TIMEOUT</tt>) in milliseconds which is the
   * timeout for waiting. A timeout value of zero is interpreted as an infinite timeout.
   *
   * @return timeout in milliseconds
   */
  public int getSoTimeout() {
    return getInt(SO_TIMEOUT, 0);
  }

  /**
   * Sets the default socket timeout (<tt>SO_TIMEOUT</tt>) in milliseconds which is the
   * timeout for waiting. A timeout value of zero is interpreted as an infinite timeout.
   *
   * @param timeout Timeout in milliseconds
   */
  public void setSoTimeout(int timeout) {
    setParameter(SO_TIMEOUT, timeout);
  }

  /**
   * Determines whether Nagle's algorithm is to be used. The Nagle's algorithm
   * tries to conserve bandwidth by minimizing the number of segments that are
   * sent. When applications wish to decrease network latency and increase
   * performance, they can disable Nagle's algorithm (that is enable TCP_NODELAY).
   * Data will be sent earlier, at the cost of an increase in bandwidth consumption.
   *
   * @param value <tt>true</tt> if the Nagle's algorithm is to NOT be used
   *              (that is enable TCP_NODELAY), <tt>false</tt> otherwise.
   */
  public void setTcpNoDelay(boolean value) {
    setParameter(TCP_NODELAY, value);
  }

  /**
   * Tests if Nagle's algorithm is to be used.
   *
   * @return <tt>true</tt> if the Nagle's algorithm is to NOT be used
   *         (that is enable TCP_NODELAY), <tt>false</tt> otherwise.
   */
  public boolean getTcpNoDelay() {
    return getBoolean(TCP_NODELAY, true);
  }

  /**
   * Returns a hint the size of the underlying buffers used by the platform for
   * outgoing network I/O. This value is a suggestion to the kernel from the
   * application about the size of buffers to use for the data to be sent over the socket.
   *
   * @return the hint size of the send buffer
   */
  public int getSendBufferSize() {
    return getInt(SO_SNDBUF, -1);
  }

  /**
   * Sets a hint the size of the underlying buffers used by the platform for
   * outgoing network I/O. This value is a suggestion to the kernel from the
   * application about the size of buffers to use for the data to be sent over the socket.
   *
   * @param size the hint size of the send buffer
   */
  public void setSendBufferSize(int size) {
    setParameter(SO_SNDBUF, size);
  }

  /**
   * Returns a hint the size of the underlying buffers used by the platform for incoming network I/O.
   * This value is a suggestion to the kernel from the application about the size of buffers to use
   * for the data to be received over the socket.
   *
   * @return the hint size of the send buffer
   */
  public int getReceiveBufferSize() {
    return getInt(SO_RCVBUF, -1);
  }

  /**
   * Sets a hint the size of the underlying buffers used by the platform for incoming network I/O.
   * This value is a suggestion to the kernel from the application about the size of buffers to use
   * for the data to be received over the socket.
   *
   * @param size the hint size of the send buffer
   */
  public void setReceiveBufferSize(int size) {
    setParameter(SO_RCVBUF, size);
  }

  /**
   * Returns linger-on-close timeout. Value <tt>0</tt> implies that the option is
   * disabled. Value <tt>-1</tt> implies that the JRE default is used.
   *
   * @return the linger-on-close timeout
   */
  public int getLinger() {
    return getInt(SO_LINGER, -1);
  }

  /**
   * Returns linger-on-close timeout. This option disables/enables immediate return from a close() of a TCP Socket.
   * Enabling this option with a non-zero Integer timeout means that a close() will block pending the transmission
   * and acknowledgement of all data written to the peer, at which point the socket is closed gracefully.
   * Value <tt>0</tt> implies that the option is disabled.
   * Value <tt>-1</tt> implies that the JRE default is used.
   *
   * @param value the linger-on-close timeout
   */
  public void setLinger(int value) {
    setParameter(SO_LINGER, value);
  }

  /**
   * Returns the timeout until a connection is etablished. A value of zero
   * means the timeout is not used. The default value is zero.
   *
   * @return timeout in milliseconds.
   */
  public int getConnectionTimeout() {
    return getInt(CONNECTION_TIMEOUT, 0);
  }

  /**
   * Sets the timeout until a connection is etablished. A value of zero
   * means the timeout is not used. The default value is zero.
   *
   * @param timeout Timeout in milliseconds.
   */
  public void setConnectionTimeout(int timeout) {
    setParameter(CONNECTION_TIMEOUT, timeout);
  }

  /**
   * Tests whether stale connection check is to be used. Disablingnstale connection check may result in
   * slight performance improvement at the risk of getting an I/O error when executing a request over a
   * connection that has been closed at the server side.
   *
   * @return <tt>true</tt> if stale connection check is to be used, <tt>false</tt> otherwise.
   */
  public boolean isStaleCheckingEnabled() {
    return getBoolean(STALE_CONNECTION_CHECK, false);
  }

  /**
   * Defines whether stale connection check is to be used. Disabling stale connection check may result in
   * slight performance improvement at the risk of getting an I/O error when executing a request over a
   * connection that has been closed at the server side.
   *
   * @param value <tt>true</tt> if stale connection check is to be used, <tt>false</tt> otherwise.
   */
  public void setStaleCheckingEnabled(boolean value) {
    setParameter(STALE_CONNECTION_CHECK, value);
  }

  /**
   * Returns the charset to be used for writing HTTP headers.
   *
   * @return The charset
   */
  public String getHttpElementCharset() {
    String charset = (String) getParameter(HTTP_ELEMENT_CHARSET);
    if (charset == null) {
      LOG.warningT("HTTP element charset not configured, using US-ASCII");
      charset = "US-ASCII";
    }
    return charset;
  }

  /**
   * Sets the charset to be used for writing HTTP headers.
   *
   * @param charset The charset
   */
  public void setHttpElementCharset(String charset) {
    setParameter(HTTP_ELEMENT_CHARSET, charset);
  }

  /**
   * Returns the default charset to be used for writing content body, when no charset explicitly specified.
   *
   * @return The charset
   */
  public String getContentCharset() {
    String charset = (String) getParameter(HTTP_CONTENT_CHARSET);
    if (charset == null) {
      LOG.warningT("Default content charset not configured, using ISO-8859-1");
      charset = "ISO-8859-1";
    }
    return charset;
  }

  /**
   * Sets the default charset to be used for writing content body, when no charset explicitly specified.
   *
   * @param charset The charset
   */
  public void setContentCharset(String charset) {
    setParameter(HTTP_CONTENT_CHARSET, charset);
  }

  /**
   * Returns the charset to be used for {@link com.sap.httpclient.auth.Credentials}.
   * If not configured the {@link #HTTP_ELEMENT_CHARSET HTTP element charset} is used.
   *
   * @return The charset
   */
  public String getCredentialCharset() {
    String charset = (String) getParameter(CREDENTIAL_CHARSET);
    if (charset == null) {
      LOG.debugT("Credential charset not configured, using HTTP element charset");
      charset = getHttpElementCharset();
    }
    return charset;
  }

  /**
   * Sets the charset to be used for writing HTTP headers.
   *
   * @param charset The charset
   */
  public void setCredentialCharset(String charset) {
    setParameter(CREDENTIAL_CHARSET, charset);
  }

  /**
   * Returns {@link HttpVersion HTTP net version} to be used by the
   * {@link com.sap.httpclient.HttpMethod HTTP methods} that this collection of parameters applies to.
   *
   * @return A {@link HttpVersion HTTP net version}
   */
  public HttpVersion getVersion() {
    Object param = getParameter(PROTOCOL_VERSION);
    if (param == null) {
      return HttpVersion.HTTP_1_1;
    }
    return (HttpVersion) param;
  }

  /**
   * Assigns the {@link HttpVersion HTTP net version} to be used by the
   * {@link com.sap.httpclient.HttpMethod HTTP methods} that this collection of parameters applies to.
   *
   * @param version the {@link HttpVersion HTTP net version}
   */
  public void setVersion(HttpVersion version) {
    setParameter(PROTOCOL_VERSION, version);
  }

  /**
   * Returns {@link com.sap.httpclient.http.cookie.CookiePolicy cookie policy} to be used by the
   * {@link com.sap.httpclient.HttpMethod HTTP methods} this collection of parameters applies to.
   *
   * @return A {@link com.sap.httpclient.http.cookie.CookiePolicy cookie policy}
   */
  public String getCookiePolicy() {
    Object param = getParameter(COOKIE_POLICY);
    if (param == null) {
      return CookiePolicy.DEFAULT;
    }
    return (String) param;
  }

  /**
   * Assigns the {@link CookiePolicy cookie policy} to be used by the
   * {@link com.sap.httpclient.HttpMethod HTTP methods} this collection of parameters applies to.
   *
   * @param policy the {@link CookiePolicy cookie policy}
   */
  public void setCookiePolicy(String policy) {
    setParameter(COOKIE_POLICY, policy);
  }

  /**
   * Sets the virtual host name.
   *
   * @param hostname The host name
   */
  public void setVirtualHost(final String hostname) {
    setParameter(VIRTUAL_HOST, hostname);
  }

  /**
   * Returns the virtual host name.
   *
   * @return The virtual host name
   */
  public String getVirtualHost() {
    return (String) getParameter(VIRTUAL_HOST);
  }

  private static final String[] STRICTNESS_PARAMETERS = {
    REJECT_RELATIVE_REDIRECT,
    ALLOW_CIRCULAR_REDIRECTS,
    UNAMBIGUOUS_STATUS_LINE,
    SINGLE_COOKIE_HEADER,
    STRICT_TRANSFER_ENCODING,
    REJECT_HEAD_BODY,
    WARN_EXTRA_INPUT
  };

  /**
   * Makes the {@link com.sap.httpclient.HttpMethod HTTP methods} strictly follow the HTTP net specification
   * (RFC 2616 and other relevant RFCs). It must be noted that popular HTTP agents have different degree
   * of HTTP net compliance and some HTTP serves are programmed to expect the behaviour that does not
   * strictly adhere to the HTTP specification.
   */
  public void makeStrict() {
    setParameters(STRICTNESS_PARAMETERS, Boolean.TRUE);
    setParameter(STATUS_LINE_GARBAGE_LIMIT, 0);
  }

  /**
   * Makes the {@link com.sap.httpclient.HttpMethod HTTP methods} attempt to mimic the exact behaviour of commonly
   * used HTTP agents, which many HTTP servers expect, even though such behaviour may violate the HTTP net
   * specification (RFC 2616 and other relevant RFCs).
   */
  public void makeLenient() {
    setParameters(STRICTNESS_PARAMETERS, Boolean.FALSE);
    setParameter(STATUS_LINE_GARBAGE_LIMIT, Integer.MAX_VALUE);
  }

}

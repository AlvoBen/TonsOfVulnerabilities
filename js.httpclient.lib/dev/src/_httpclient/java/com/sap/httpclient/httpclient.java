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

import com.sap.httpclient.auth.AuthScope;
import com.sap.httpclient.auth.Credentials;
import com.sap.httpclient.http.cookie.Cookie;
import com.sap.httpclient.http.cache.CacheManager;
import com.sap.httpclient.uri.URI;
import com.sap.httpclient.net.connection.HttpConnectionManager;
import com.sap.httpclient.net.connection.HttpConnectionManagerImpl;
import com.sap.tc.logging.Location;

import java.io.IOException;
import java.security.Provider;
import java.security.Security;
import java.util.Date;

/**
 * This class represents an HTTP client, use to execute HTTP methods
 *
 * @author Nikolai Neichev
 */
public class HttpClient {

  public final static Location LOG = Location.getLocation(HttpClient.class);

  static {
    if (LOG.beDebug()) {
      try {
        LOG.debugT("Java version: " + System.getProperty("java.version"));
        LOG.debugT("Java vendor: " + System.getProperty("java.vendor"));
        LOG.debugT("Java class path: " + System.getProperty("java.class.path"));
        LOG.debugT("Operating system name: " + System.getProperty("os.name"));
        LOG.debugT("Operating system architecture: " + System.getProperty("os.arch"));
        LOG.debugT("Operating system version: " + System.getProperty("os.version"));
        Provider[] providers = Security.getProviders();
        for (Provider provider : providers) {
          LOG.debugT(provider.getName() + " " + provider.getVersion() + ": " + provider.getInfo());
        }
      } catch (SecurityException se) {
        // $JL-EXC$
      }
    }
  }

  /**
   * The {@link HttpConnectionManager connection manager}
   */
  private HttpConnectionManager httpConnectionManager;

  /**
   * The {@link HttpState HTTP state} of this HttpClient.
   */
  private HttpState state = new HttpState();

  /**
   * The {@link HttpClientParameters collection of parameters} associated with this HttpClient.
   */
  private HttpClientParameters params = null;

  /**
   * The {@link HostConfiguration host configuration} associated with the HttpClient
   */
  private HostConfiguration hostConfiguration = new HostConfiguration();

  /**
   * Creates an instance of HttpClient using default {@link HttpClientParameters parameter set}.
   */
  public HttpClient() {
    this(new HttpClientParameters());
  }

  /**
   * Creates an instance of HttpClient using the specified {@link HttpClientParameters parameter set}.
   *
   * @param params The {@link HttpClientParameters parameters} to use.
   */
  public HttpClient(HttpClientParameters params) {
    super();
    if (params == null) {
      throw new IllegalArgumentException("Params is null");
    }
    this.params = params;
    this.httpConnectionManager = null;
    Class clazz = params.getConnectionManagerClass();
    if (clazz != null) {
      try {
        this.httpConnectionManager = (HttpConnectionManager) clazz.newInstance();
      } catch (Exception e) {
        LOG.warningT("Error instantiating connection manager class, defaulting to SingleConnectionManager : "+ e.getMessage());
      }
    }
    if (this.httpConnectionManager == null) {
      this.httpConnectionManager = new HttpConnectionManagerImpl();
    }
    this.httpConnectionManager.getParams().setDefaults(this.params);

    if(this.params.getBoolean(Parameters.CACHE_ENABLED, false)){
      /* init the Cache Manager */
      CacheManager.createInstance(params);
      LOG.pathT("Start the Http Client's Cache");

      Runtime.getRuntime().addShutdownHook(new Thread() {
        public void run(){
          if(CacheManager.isRunning()){
            if (LOG.bePath()) {
              LOG.pathT("HttpClient.finalize() CacheManager is stopping");
            }
            try{
              CacheManager.stop();
            } catch(Exception ex){
              LOG.pathT("Exception in Cache stoppping : " + ex.getMessage());
            }
          }
        }
      });
    }

  }

  /**
   * Creates an instance of HttpClient with specified {@link HttpClientParameters parameter set} and
   * {@link HttpConnectionManager HTTP connection manager}.
   *
   * @param params The {@link HttpClientParameters parameters} to use.
   * @param httpConnectionManager The {@link HttpConnectionManager connection manager} to use.
   */
  public HttpClient(HttpClientParameters params, HttpConnectionManager httpConnectionManager) {
    super();
    if (httpConnectionManager == null) {
      throw new IllegalArgumentException("httpConnectionManager cannot be null");
    }
    if (params == null) {
      throw new IllegalArgumentException("Params is null");
    }
    this.params = params;
    this.httpConnectionManager = httpConnectionManager;
    this.httpConnectionManager.getParams().setDefaults(this.params);
  }

  /**
   * Creates an instance of HttpClient with specified {@link HttpConnectionManager HTTP connection manager}.
   *
   * @param httpConnectionManager The {@link HttpConnectionManager connection manager} to use.
   */
  public HttpClient(HttpConnectionManager httpConnectionManager) {
    this(new HttpClientParameters(), httpConnectionManager);
  }

  /**
   * Returns the {@link HttpState HTTP state} of this HttpClient.
   *
   * @return the client state
   */
  public synchronized HttpState getState() {
    return state;
  }

  /**
   * Assigns {@link HttpState HTTP state} for this HttpClient.
   *
   * @param state the new {@link HttpState HTTP state} for the client
   */
  public synchronized void setState(HttpState state) {
    this.state = state;
  }

  /**
   * Executes the specified {@link HttpMethod HTTP method}.
   *
   * @param method the {@link HttpMethod HTTP method} to execute.
   * @return the method's response code
   * @throws IOException   If an I/O (transport) error occurs.
   */
  public int executeMethod(HttpMethod method) throws IOException {
    return executeMethod(null, method, null);
  }

  /**
   * Executes the specified {@link HttpMethod HTTP method} with the specified host configuration.
   *
   * @param hc The {@link HostConfiguration host configuration} to use.
   * @param method the {@link HttpMethod HTTP method} to execute.
   * @return the method's response code
   * @throws IOException   If an I/O (transport) error occurs.
   */
  public int executeMethod(final HostConfiguration hc, final HttpMethod method) throws IOException {
    return executeMethod(hc, method, null);
  }

  /**
   * Executes the specified {@link HttpMethod HTTP method} using the specified
   * {@link HostConfiguration host configuration} and the specified {@link HttpState HTTP state}.
   *
   * @param hostconfig The {@link HostConfiguration host configuration} to use.
   * @param method     the {@link HttpMethod HTTP method} to execute.
   * @param state      the {@link HttpState HTTP state} to use when executing the method.
   * @return the method's response code
   * @throws IOException   If an I/O (transport) error occurs.
   */
  public int executeMethod(HostConfiguration hostconfig,
                           final HttpMethod method, final HttpState state) throws IOException {

    if (method == null) {
      throw new IllegalArgumentException("HttpMethod parameter is null");
    }
    HostConfiguration defaulthostconfig = getHostConfiguration();
    if (hostconfig == null) {
      hostconfig = defaulthostconfig;
    }
    URI uri = method.getURI();
    if (hostconfig == defaulthostconfig || uri.isAbsoluteURI()) {
      // make a deep copy of the host defaults
      hostconfig = new HostConfiguration(hostconfig);
      if (uri.isAbsoluteURI()) {
        hostconfig.setHost(uri);
      }
    }
    HttpMethodProcessor methodProcessor = new HttpMethodProcessor(getHttpConnectionManager(),
            hostconfig,
            this.params,
            (state == null ? getState() : state));
    methodProcessor.executeMethod(method);
    return method.getStatusCode();
  }

  /**
   * Returns the {@link HostConfiguration host configuration} of the HttpClient.
   *
   * @return {@link HostConfiguration host configuration} the client host configuration
   */
  public synchronized HostConfiguration getHostConfiguration() {
    return hostConfiguration;
  }

  /**
   * Assigns the {@link HostConfiguration host configuration} to the HttpClient.
   *
   * @param hostConfiguration The {@link HostConfiguration host configuration} to set
   */
  public synchronized void setHostConfiguration(HostConfiguration hostConfiguration) {
    this.hostConfiguration = hostConfiguration;
  }

  /**
   * Returns the {@link HttpConnectionManager HTTP connection manager} of the HttpClient.
   *
   * @return {@link HttpConnectionManager HTTP connection manager} the connection manager
   */
  public synchronized HttpConnectionManager getHttpConnectionManager() {
    return httpConnectionManager;
  }

  /**
   * Assigns the {@link HttpConnectionManager HTTP connection manager} to the HttpClient.
   *
   * @param httpConnectionManager The {@link HttpConnectionManager HTTP connection manager} to set
   */
  public synchronized void setHttpConnectionManager(HttpConnectionManager httpConnectionManager) {
    this.httpConnectionManager = httpConnectionManager;
    if (this.httpConnectionManager != null) {
      this.httpConnectionManager.getParams().setDefaults(this.params);
    }
  }

  /**
   * Returns {@link HttpClientParameters HTTP net parameters} of this HttpClient.
	 *
	 * @return the http client params
	 */
  public HttpClientParameters getParams() {
    return this.params;
  }

  /**
   * Assigns {@link HttpClientParameters HTTP net parameters} for this HttpClient.
	 *
	 * @param params the params to set
	 */
  public void setParams(final HttpClientParameters params) {
    if (params == null) {
      throw new IllegalArgumentException("Parameters is null");
    }
    this.params = params;
  }

  // STATE RELATED METHODS FOLLOW

  /**
   * Adds an {@link Cookie HTTP cookie}
   *
   * @param cookie the {@link Cookie cookie} to be added
   */
  public void addCookie(Cookie cookie) {
    state.addCookie(cookie);
  }

  /**
   * Adds an array of {@link Cookie HTTP cookies}.
   *
   * @param cookies the {@link Cookie cookies} to be added
   */
  public void addCookies(Cookie[] cookies) {
    state.addCookies(cookies);
  }

  /**
   * Returns an array of {@link Cookie cookies} that this HTTP state contains.
   *
   * @return an array of {@link Cookie cookies}.
   */
  public Cookie[] getCookies() {
    return state.getCookies();
  }

  /**
   * Removes all of {@link Cookie cookies} in this HTTP state that have expired.
   *
   * @return true if any cookies were purged.
   */
  public boolean purgeExpiredCookies() {
    return purgeExpiredCookies(new Date());
  }

  /**
   * Removes all of {@link Cookie cookies} in this HTTP state
   * that have expired by the specified {@link Date date}.
   *
   * @param date The {@link Date date} to compare against.
   * @return true if any cookies were purged.
   */
  public boolean purgeExpiredCookies(Date date) {
    return state.purgeExpiredCookies(date);
  }

  /**
   * Sets the {@link Credentials credentials} for the specified authentication scope.
   *
   * @param authscope   the {@link AuthScope authentication scope}
   * @param credentials the authentication {@link Credentials credentials} for the specified scope.
   */
  public void setCredentials(final AuthScope authscope, final Credentials credentials) {
    state.setCredentials(authscope, credentials);
  }

  /**
   * Get the {@link Credentials credentials} for the specified authentication scope.
   *
   * @param authscope the {@link AuthScope authentication scope}
   * @return the credentials
   */
  public Credentials getCredentials(final AuthScope authscope) {
    return state.getCredentials(authscope);
  }

  /**
   * Sets the {@link Credentials proxy credentials} for the specified authentication scope.
   *
   * @param authscope   the {@link AuthScope authentication scope}
   * @param credentials the authentication {@link Credentials credentials} for the specified scope.
   */
  public void setProxyCredentials(final AuthScope authscope, final Credentials credentials) {
    state.setProxyCredentials(authscope, credentials);
  }

  /**
   * Get the {@link Credentials proxy credentials} for the specified authentication scope.
   *
   * @param authscope the {@link AuthScope authentication scope}
   * @return the credentials
   */
  public Credentials getProxyCredentials(final AuthScope authscope) {
    return state.getProxyCredentials(authscope);
  }

  /**
   * Clears all credentials.
   */
  public void clearCredentials() {
    state.clearCredentials();
  }

  /**
   * Clears all proxy credentials.
   */
  public void clearProxyCredentials() {
    state.clearProxyCredentials();
  }

  /**
   * Clears all cookies.
   */
  public void clearCookies() {
    state.clearCookies();
  }

  /**
   * Clears the state information (cookies, credentials and proxy credentials).
   */
  public void clear() {
    clearCookies();
    clearCredentials();
    clearProxyCredentials();
  }

}
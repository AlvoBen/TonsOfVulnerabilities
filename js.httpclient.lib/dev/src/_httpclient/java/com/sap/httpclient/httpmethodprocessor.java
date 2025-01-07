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

import com.sap.httpclient.auth.*;
import com.sap.httpclient.exception.*;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.http.methods.CONNECT;
import com.sap.httpclient.uri.URI;
import com.sap.httpclient.net.connection.HttpConnectionManager;
import com.sap.httpclient.net.connection.HttpConnection;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import java.io.IOException;
import java.util.*;

import static com.sap.httpclient.http.HttpStatus.*;

/**
 * Handles the process of executing a method including authentication, redirection and retries.
 *
 * @author Nikolai Neichev
 */
class HttpMethodProcessor {

  /**
   * The www authenticate challange header.
   */
  public static final String WWW_AUTH_CHALLENGE = "WWW-Authenticate";

  /**
   * The www authenticate response header.
   */
  public static final String WWW_AUTH_RESP = "Authorization";

  /**
   * The proxy authenticate challange header.
   */
  public static final String PROXY_AUTH_CHALLENGE = "Proxy-Authenticate";

  /**
   * The proxy authenticate response header.
   */
  public static final String PROXY_AUTH_RESP = "Proxy-Authorization";

  private static final Location LOG = Location.getLocation(HttpMethodProcessor.class);

  private CONNECT connectMethod;

  private HttpState state;

  private HostConfiguration hostConfiguration;

  private HttpConnectionManager connectionManager;

  private HttpClientParameters params;

  private HttpConnection conn;

  /**
   * A flag to indicate if the connection should be released after the method is executed.
   */
  private boolean releaseConnection = false;

  /**
   * Authentication processor
   */
  private AuthChallengeProcessor authProcessor = null;

  private Set<URI> redirectLocations = null;

  public HttpMethodProcessor(final HttpConnectionManager connectionManager,
                            final HostConfiguration hostConfiguration,
                            final HttpClientParameters params,
                            final HttpState state) {
    super();
    this.connectionManager = connectionManager;
    this.hostConfiguration = hostConfiguration;
    this.params = params;
    this.state = state;
    this.authProcessor = new AuthChallengeProcessor(this.params);
  }

  /**
   * Executes the method associated with this method director.
   *
	 * @param method the http method to execute
   * @throws IOException if an IO problem occures
   */
  public void executeMethod(final HttpMethod method) throws IOException {
    if (method == null) {
      throw new IllegalArgumentException("Method is null");
    }
    this.hostConfiguration.getParams().setDefaults(this.params);
    method.getParams().setDefaults(this.hostConfiguration.getParams());
    Collection<Header> defaultHeaders =
            (Collection<Header>) this.hostConfiguration.getParams().getParameter(Parameters.DEFAULT_HEADERS);

    if (defaultHeaders != null) {
      for (Header header : defaultHeaders) {
        method.addRequestHeader(header);
      }
    }
    try {
      int maxRedirects = this.params.getInt(HttpClientParameters.MAX_REDIRECTS, 100);
      for (int redirectCount = 0; ;) {
        if (this.conn != null && !hostConfiguration.hostEquals(this.conn)) { // is the connection correct
          this.conn.setLocked(false);
          this.conn.releaseConnection();
          this.conn = null;
        }
        if (this.conn == null) { // get a connection
          this.conn = connectionManager.getConnectionWithTimeout(hostConfiguration,
                  this.params.getConnectionManagerTimeout());
          this.conn.setLocked(true);
          if (this.params.isAuthenticationPreemptive()) {
            LOG.debugT("Preemptively sending default basic credentials");
            method.getHostAuthState().setPreemptive();
            method.getHostAuthState().setAuthAttempted(true);
            if (this.conn.isProxied() && !this.conn.isSecure()) {
              method.getProxyAuthState().setPreemptive();
              method.getProxyAuthState().setAuthAttempted(true);
            }
          }
        }
        authenticate(method);
        executeWithRetry(method);
        if (this.connectMethod != null) { // tunnel is created
          if (method instanceof HttpMethodImpl) {
            ((HttpMethodImpl) method).setConnectResponse(connectMethod.getStatusLine(),
                        connectMethod.getResponseHeaderGroup(),
                        connectMethod.getResponseBodyAsStream());
            method.getProxyAuthState().setAuthScheme(connectMethod.getProxyAuthState().getAuthScheme());
            this.connectMethod = null;
          } else {
            releaseConnection = true;
          }
          break;
        }
        boolean willRetry = false;
        if (isRedirectNeeded(method)) {
          if (processRedirectResponse(method)) {
            willRetry = true;
            redirectCount++;
            if (redirectCount >= maxRedirects) {
              throw new RedirectException("Maximum redirects (" + maxRedirects + ") exceeded.");
            }
            if (LOG.beDebug()) {
              LOG.debugT("Execute " + redirectCount + " redirect of maximum " + maxRedirects);
            }
          }
        }
        if (isAuthenticationNeeded(method)) {
          if (processAuthenticationResponse(method)) {
            willRetry = true;
          }
        }
        if (!willRetry) { // won't retry, break loop
          break;
        }
        if (method.getResponseBodyAsStream() != null) { // will retry, close previous stream
          method.getResponseBodyAsStream().close();
        }

      } //end of retry loop
    } finally {
      if (this.conn != null) {
        this.conn.setLocked(false);
      }
      // If the response has been fully processed, return the connection to the pool.
      if ( (this.conn != null) && ( releaseConnection || (method.getResponseBodyAsStream() == null) ) ) {
        this.conn.releaseConnection();
      }
    } // end finally
  }

  private void authenticate(final HttpMethod method) {
    try {
      if (this.conn.isProxied() && !this.conn.isSecure()) {
        authenticateProxy(method);
      }
      authenticateHost(method);
    } catch (AuthenticationException e) {
      LOG.traceThrowableT(Severity.ERROR, "Authentication fails", e);
    }
  }

  private boolean cleanAuthHeaders(final HttpMethod method, final String name) {
    ArrayList<Header> authheaders = method.getRequestHeaders(name);
    boolean clean = true;
    for (Header authheader : authheaders) {
      if (authheader.isAutogenerated()) {
        method.removeRequestHeader(authheader);
      } else { // this header is user defined
        clean = false;
      }
    }
    return clean;
  }

  private void authenticateHost(final HttpMethod method) throws AuthenticationException {
    if (!cleanAuthHeaders(method, WWW_AUTH_RESP)) { // cleans existing authentication headers
      return; // User defined authentication header(s) present
    }
    AuthState authstate = method.getHostAuthState();
    AuthScheme authscheme = authstate.getAuthScheme();
    if (authscheme == null) {
      return;
    }
    if (authstate.isAuthRequested() || !authscheme.isConnectionBased()) {
      String host = method.getParams().getVirtualHost();
      if (host == null) {
        host = conn.getHost();
      }
      int port = conn.getPort();
      AuthScope authscope = new AuthScope(host, port, authscheme.getRealm(), authscheme.getSchemeName());
      if (LOG.beDebug()) {
        LOG.debugT("Authenticating with scope : " + authscope);
      }
      Credentials credentials = this.state.getCredentials(authscope);
      if (credentials != null) {
        String authstring = authscheme.authenticate(credentials, method);
        if (authstring != null) {
          method.addRequestHeader(new Header(WWW_AUTH_RESP, authstring, true));
        }
      } else {
        if (LOG.beWarning()) {
          LOG.warningT("Required credentials not available for scope : " + authscope);
          if (method.getHostAuthState().isPreemptive()) {
            LOG.warningT("Preemptive authentication requested but no default credentials available");
          }
        }
      }
    }
  }

  private void authenticateProxy(final HttpMethod method) throws AuthenticationException {
    if (!cleanAuthHeaders(method, PROXY_AUTH_RESP)) { // cleans existing authentication headers
      return; // User defined authentication header(s) present
    }
    AuthState authstate = method.getProxyAuthState();
    AuthScheme authscheme = authstate.getAuthScheme();
    if (authscheme == null) {
      return;
    }
    if (authstate.isAuthRequested() || !authscheme.isConnectionBased()) {
      AuthScope authscope = new AuthScope(conn.getProxyHost(), conn.getProxyPort(),
              authscheme.getRealm(),
              authscheme.getSchemeName());
      if (LOG.beDebug()) {
        LOG.debugT("Authenticating with scope : " + authscope);
      }
      Credentials credentials = this.state.getProxyCredentials(authscope);
      if (credentials != null) {
        String authstring = authscheme.authenticate(credentials, method);
        if (authstring != null) {
          method.addRequestHeader(new Header(PROXY_AUTH_RESP, authstring, true));
        }
      } else {
        if (LOG.beWarning()) {
          LOG.warningT("Required proxy credentials not available for scope : " + authscope);
          if (method.getProxyAuthState().isPreemptive()) {
            LOG.warningT("Preemptive authentication requested but no default proxy credentials available");
          }
        }
      }
    }
  }

  /**
   * Applies connection parameters specified for a specified method
   *
   * @param method HTTP method
   * @throws IOException if an I/O occurs setting connection parameters
   */
  private void applyConnectionParams(final HttpMethod method) throws IOException {
    int timeout = 0;
    Object timeoutParam = method.getParams().getParameter(HttpClientParameters.SO_TIMEOUT);
    if (timeoutParam == null) {
      timeoutParam = this.conn.getParams().getParameter(HttpClientParameters.SO_TIMEOUT);
    }
    if (timeoutParam != null) { // timeout property found
      timeout = (Integer) timeoutParam;
    }
    this.conn.setSocketTimeout(timeout);
  }

  /**
   * Executes the specified method with the current hostConfiguration.
   *
	 * @param method the http method
   * @throws IOException   if an I/O (transport) error occurs.
   * @throws HttpException if a net exception occurs.
   */
  private void executeWithRetry(final HttpMethod method) throws IOException {
    int execCount = 0;
    try {
      while (true) {
        execCount++;
        try {
          if (LOG.beDebug()) {
            LOG.debugT("Attempt number " + execCount + " to execute method");
          }
          if (this.conn.getParams().isStaleCheckingEnabled()) {
            this.conn.closeIfStale();
          }
          if (!this.conn.isOpen()) {
            this.conn.open();
            if (this.conn.isProxied() && this.conn.isSecure() && !(method instanceof CONNECT)) {
              if (!createSecureTunnel()) { // createing a secure tunnel with a connect method
                // abort, the connect method failed
                return;
              }
            }
          }
          applyConnectionParams(method);
          // redirecting flag is set, we'll use the redirection URI for this execution
          // after this execution the original URI will be used, because this is a 'temporary redirect' case
          if (redirecting) {
            URI stored = method.getURI();
            try {
              method.setURI(redirectedUri);
              method.execute(state, this.conn);
            } finally { // restoring the previous id
              method.setURI(stored);
            }
          } else {
            method.execute(state, this.conn);
          }
          break;
        } catch (HttpException e) {
          // protocol exception, can't recover from
          throw e;
        } catch (IOException e) {
          LOG.debugT("Closing the connection.");
          this.conn.close();
          HttpMethodRetryHandler retryHandler = new HttpMethodRetryHandler();
          if (!retryHandler.retryMethod(method, e, execCount)) {
            LOG.debugT("Method retry retryHandler returned false. Automatic recovery will not be attempted");
            throw e;
          }
          if (LOG.beInfo()) {
            LOG.infoT("I/O exception (" + e.getClass().getName() + ") caught when processing request: "
                    + e.getMessage());
          }
          if (LOG.beDebug()) {
            LOG.traceThrowableT(Severity.DEBUG, e.getMessage(), e);
          }
          LOG.infoT("Retrying request");
        }
      }
    } catch (IOException e) {
      if (this.conn.isOpen()) {
        LOG.debugT("Closing the connection.");
        this.conn.close();
      }
      releaseConnection = true;
      throw e;
    } catch (RuntimeException e) {
      if (this.conn.isOpen()) {
        LOG.debugT("Closing the connection.");
        this.conn.close();
      }
      releaseConnection = true;
      throw e;
    }
  }

  /**
   * Executes a CONNECT method to establish a secured tunneled connection.
   *
   * @return <code>true</code> if the connect was successful
   * @throws IOException if there is an IO problem
   */
  private boolean createSecureTunnel() throws IOException {
    this.connectMethod = new CONNECT();
    this.connectMethod.getParams().setDefaults(this.hostConfiguration.getParams());
    int code;
    for (; ;) {
      if (!this.conn.isOpen()) {
        this.conn.open();
      }
      if (this.params.isAuthenticationPreemptive()) {
        LOG.debugT("Preemptively sending default basic credentials");
        this.connectMethod.getProxyAuthState().setPreemptive();
        this.connectMethod.getProxyAuthState().setAuthAttempted(true);
      }
      try {
        authenticateProxy(this.connectMethod);
      } catch (AuthenticationException e) {
        LOG.traceThrowableT(Severity.ERROR, e.getMessage(), e);
      }
      applyConnectionParams(this.connectMethod);
      this.connectMethod.execute(state, this.conn);
      code = this.connectMethod.getStatusCode();
      boolean retry = false;
      AuthState authstate = this.connectMethod.getProxyAuthState();
      authstate.setAuthRequested(code == SC_PROXY_AUTHENTICATION_REQUIRED);
      if (authstate.isAuthRequested()) {
        if (processAuthenticationResponse(this.connectMethod)) {
          retry = true;
        }
      }
      if (!retry) {
        break;
      }
      if (this.connectMethod.getResponseBodyAsStream() != null) {
        this.connectMethod.getResponseBodyAsStream().close();
      }
    }
    if ((code >= 200) && (code < 300)) {
      this.conn.tunnelCreated();
      // Drop the connect method, as it is no longer needed
      this.connectMethod = null;
      return true;
    } else {
      return false;
    }
  }

  private boolean redirecting = false;
  private URI redirectedUri = null;

  /**
   * Process the redirect response.
   *
   * @return <code>true</code> if the redirect was successful
	 * @param method the http method
	 * @throws com.sap.httpclient.exception.RedirectException if a redirect exceprion occures
   */
  private boolean processRedirectResponse(final HttpMethod method) throws RedirectException {
    //get the location header to find out where to redirect to
    Header locationHeader = method.getResponseHeader("location");
    if (locationHeader == null) {
      // got a redirect response, but no location header
      LOG.errorT("Received redirect response " + method.getStatusCode() + " but no location header");
      return false;
    }
    String location = locationHeader.getValue();
    if (LOG.beDebug()) {
      LOG.debugT("Redirect requested to location '" + location + "'");
    }
    //rfc2616 demands the location value be a complete URI
    //Location       = "Location" ":" absoluteURI
    URI redirectUri;
    URI currentUri;
		try {
      currentUri = new URI(this.conn.getProtocol().getScheme(),
              null,
              this.conn.getHost(),
              this.conn.getPort(),
              method.getPath());
      redirectUri = new URI(location, true);
      if (redirectUri.isRelativeURI()) {
        if (this.params.isTrue(HttpClientParameters.REJECT_RELATIVE_REDIRECT)) {
          if (LOG.beWarning()) {
            LOG.warningT("Relative redirect location '" + location + "' not allowed");
          }
          return false;
        } else {
          //location is incomplete, use current values for defaults
          LOG.debugT("Redirect URI is not absolute - parsing as relative");
          redirectUri = new URI(currentUri, redirectUri);
        }
      }

//      method.setURI(redirectUri);
//      hostConfiguration.setHost(redirectUri);

      hostConfiguration.setHost(redirectUri);

      // the method will change it's uri only when there is a MOVED_PERMANENTLY or MULTIPLE_CHOICES response,
      // in all other 3xx responses we redurect unly the current request.
      if ( (method.getStatusCode() == SC_MOVED_PERMANENTLY) || (method.getStatusCode() == SC_MULTIPLE_CHOICES)) {
        method.setURI(redirectUri);
      } else {
        redirecting = true;
        redirectedUri = redirectUri;
      }
    } catch (URIException e) {
      if (LOG.beWarning()) {
        LOG.warningT("Redirected location '" + location + "' is malformed");
      }
      return false;
    }
    if (this.params.isFalse(HttpClientParameters.ALLOW_CIRCULAR_REDIRECTS)) {
      if (this.redirectLocations == null) {
        this.redirectLocations = new HashSet<URI>();
        this.redirectLocations.add(currentUri);
      }
      try {
        if (redirectUri.hasQuery()) {
          redirectUri.setQuery(null);
        }
      } catch (URIException e) {
        // Should never happen
        return false;
      }
      if (this.redirectLocations.contains(redirectUri)) {
        throw new CircularRedirectException("Circular redirect to '" + redirectUri + "'");
      } else {
        this.redirectLocations.add(redirectUri);
      }
    }
    if (LOG.beDebug()) {
      LOG.debugT("Redirecting from '" + currentUri.getEscapedURI() + "' to '" + redirectUri.getEscapedURI());
    }
    //And finally invalidate the actual authentication scheme
    method.getHostAuthState().invalidate();
    return true;
  }

  /**
   * Processes a response that requires authentication
   *
   * @param method the current {@link HttpMethod HTTP method}
   * @return <tt>true</tt> if the authentication challenge can be responsed to,
   *         (that is, at least one of the requested authentication scheme is supported,
   *         and matching credentials have been found), <tt>false</tt> otherwise.
   */
  private boolean processAuthenticationResponse(final HttpMethod method) {
    try {
      switch (method.getStatusCode()) {
        case SC_UNAUTHORIZED:
          return processWWWAuthChallenge(method);
        case SC_PROXY_AUTHENTICATION_REQUIRED:
          return processProxyAuthChallenge(method);
        default:
          return false;
      }
    } catch (Exception e) {
      if (LOG.beError()) {
        LOG.traceThrowableT(Severity.ERROR, e.getMessage(), e);
      }
      return false;
    }
  }

  private boolean processWWWAuthChallenge(final HttpMethod method)
          throws MalformedChallengeException, AuthenticationException {
    AuthState authstate = method.getHostAuthState();
    Map<String, String> challenges = AuthChallengeParser.parseChallenges(method.getResponseHeaders(WWW_AUTH_CHALLENGE));
    if (challenges.isEmpty()) {
      LOG.debugT("Authentication challenge(s) not found");
      return false;
    }
    AuthScheme authscheme = null;
    try {
      authscheme = this.authProcessor.processChallenge(authstate, challenges);
    } catch (AuthChallengeException e) {
      if (LOG.beWarning()) {
        LOG.warningT(e.getMessage());
      }
    }
    if (authscheme == null) {
      return false;
    }
    String host = method.getParams().getVirtualHost();
    if (host == null) {
      host = conn.getHost();
    }
    int port = conn.getPort();
    AuthScope authscope = new AuthScope(host, port, authscheme.getRealm(), authscheme.getSchemeName());
    if (LOG.beDebug()) {
      LOG.debugT("Authentication scope: " + authscope);
    }
    if (authstate.isAuthAttempted() && authscheme.isComplete()) {
      // Already tried and failed
      Credentials credentials = promptForCredentials(authscheme, method.getParams(), authscope);
      if (credentials == null) {
        if (LOG.beInfo()) {
          LOG.infoT("Failure authenticating with " + authscope);
        }
        return false;
      } else {
        return true;
      }
    } else {
      authstate.setAuthAttempted(true);
      Credentials credentials = this.state.getCredentials(authscope);
      if (credentials == null) {
        credentials = promptForCredentials(authscheme, method.getParams(), authscope);
      }
      if (credentials == null) {
        if (LOG.beInfo()) {
          LOG.infoT("No credentials available for " + authscope);
        }
        return false;
      } else {
        return true;
      }
    }
  }

  private boolean processProxyAuthChallenge(final HttpMethod method)
          throws MalformedChallengeException, AuthenticationException {
    AuthState authstate = method.getProxyAuthState();
    Map<String, String> proxyChallenges = AuthChallengeParser.parseChallenges(method.getResponseHeaders(PROXY_AUTH_CHALLENGE));
    if (proxyChallenges.isEmpty()) {
      LOG.debugT("Proxy authentication challenge(s) not found");
      return false;
    }
    AuthScheme authscheme = null;
    try {
      authscheme = this.authProcessor.processChallenge(authstate, proxyChallenges);
    } catch (AuthChallengeException e) {
      if (LOG.beWarning()) {
        LOG.warningT(e.getMessage());
      }
    }
    if (authscheme == null) {
      return false;
    }
    AuthScope authscope = new AuthScope(conn.getProxyHost(), conn.getProxyPort(),
            authscheme.getRealm(),
            authscheme.getSchemeName());

    if (LOG.beDebug()) {
      LOG.debugT("Proxy authentication scope: " + authscope);
    }
    if (authstate.isAuthAttempted() && authscheme.isComplete()) {
      // Already tried and failed
      Credentials credentials = promptForProxyCredentials(authscheme, method.getParams(), authscope);
      if (credentials == null) {
        if (LOG.beInfo()) {
          LOG.infoT("Failure authenticating with " + authscope);
        }
        return false;
      } else {
        return true;
      }
    } else {
      authstate.setAuthAttempted(true);
      Credentials credentials = this.state.getProxyCredentials(authscope);
      if (credentials == null) {
        credentials = promptForProxyCredentials(authscheme, method.getParams(), authscope);
      }
      if (credentials == null) {
        if (LOG.beInfo()) {
          LOG.infoT("No credentials available for " + authscope);
        }
        return false;
      } else {
        return true;
      }
    }
  }

  /**
   * Tests if the {@link HttpMethod method} requires a redirect to another location.
   *
   * @param method HTTP method
   * @return boolean <tt>true</tt> if a retry is needed, <tt>false</tt> otherwise.
   */
  private boolean isRedirectNeeded(final HttpMethod method) {
    switch (method.getStatusCode()) {
      case SC_MULTIPLE_CHOICES:
      case SC_MOVED_PERMANENTLY:
      case SC_MOVED_TEMPORARILY:
      case SC_SEE_OTHER:
      case SC_TEMPORARY_REDIRECT:
        LOG.debugT("Redirect required");
        if (method.getFollowRedirects()) {
          return true;
        } else {
          LOG.infoT("Redirect requested but followRedirects is disabled");
          return false;
        }
      default:
        return false;
    } //end of switch
  }

  /**
   * Tests if the {@link HttpMethod method} requires authentication.
   *
   * @param method HTTP method
   * @return boolean <tt>true</tt> if a retry is needed, <tt>false</tt> otherwise.
   */
  private boolean isAuthenticationNeeded(final HttpMethod method) {
    method.getHostAuthState().setAuthRequested(method.getStatusCode() == SC_UNAUTHORIZED);
    method.getProxyAuthState().setAuthRequested(method.getStatusCode() == SC_PROXY_AUTHENTICATION_REQUIRED);
    if (method.getHostAuthState().isAuthRequested() || method.getProxyAuthState().isAuthRequested()) {
      LOG.debugT("Authorization required");
      if (method.getDoAuthentication()) { //process authentication response
        return true;
      } else { //let the client handle the authenticaiton
        LOG.infoT("Authentication requested but doAuthentication is disabled");
        return false;
      }
    } else {
      return false;
    }
  }

  private Credentials promptForCredentials(final AuthScheme authScheme,
                                           final Parameters params,
                                           final AuthScope authscope) {
    LOG.debugT("Credentials required");
    Credentials creds = null;
    CredentialsProvider credProvider =
            (CredentialsProvider) params.getParameter(CredentialsProvider.PROVIDER);
    if (credProvider != null) {
      try {
        creds = credProvider.getCredentials(authScheme, authscope.getHost(), authscope.getPort(), false);
      } catch (CredentialsNotAvailableException e) {
        LOG.warningT(e.getMessage());
      }
      if (creds != null) {
        this.state.setCredentials(authscope, creds);
        if (LOG.beDebug()) {
          LOG.debugT(authscope + " new credentials specified");
        }
      }
    } else {
      LOG.debugT("Credentials provider not available");
    }
    return creds;
  }

  private Credentials promptForProxyCredentials(final AuthScheme authScheme,
                                                final Parameters params,
                                                final AuthScope authscope) {
    LOG.debugT("Proxy credentials required");
    Credentials creds = null;
    CredentialsProvider credProvider =
            (CredentialsProvider) params.getParameter(CredentialsProvider.PROVIDER);
    if (credProvider != null) {
      try {
        creds = credProvider.getCredentials(authScheme, authscope.getHost(), authscope.getPort(), true);
      } catch (CredentialsNotAvailableException e) {
        LOG.warningT(e.getMessage());
      }
      if (creds != null) {
        this.state.setProxyCredentials(authscope, creds);
        if (LOG.beDebug()) {
          LOG.debugT(authscope + " new credentials specified");
        }
      }
    } else {
      LOG.debugT("Proxy credentials provider not available");
    }
    return creds;
  }

  /**
	 * Getter method
	 *
   * @return the host configuration
   */
  public HostConfiguration getHostConfiguration() {
    return hostConfiguration;
  }

  /**
	 * Getter method
	 *
   * @return the state
   */
  public HttpState getState() {
    return state;
  }

  /**
	 * Getter method
	 *
   * @return the connection manager
   */
  public HttpConnectionManager getConnectionManager() {
    return connectionManager;
  }

  /**
	 * Getter method
	 *
   * @return the parameters
   */
  public Parameters getParams() {
    return this.params;
  }
}
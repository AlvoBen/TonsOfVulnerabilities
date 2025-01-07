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
package com.sap.httpclient.auth;

import com.sap.httpclient.exception.CredentialsNotAvailableException;

/**
 * <p/>
 * Credentials provider interface can be used to provide {@link
 * com.sap.httpclient.HttpMethod HTTP method} with a means to request
 * authentication credentials if no credentials have been specified or specified
 * credentials are incorrect.
 * </p>
 * <p/>
 * HttpClient makes no provisions to check whether the same credentials have
 * been tried already. It is a responsibility of the custom credentials provider
 * to keep track of authentication attempts and to ensure that credentials known
 * to be invalid are not retried. HttpClient will simply store the set of
 * credentials returned by the custom credentials provider in the
 * {@link com.sap.httpclient.HttpState http state} object and will
 * attempt to use these credentials for all subsequent requests with the specified
 * authentication scope.
 * </p>
 * <p/>
 * Classes implementing this interface must synchronize access to shared data as
 * methods of this interfrace may be executed from multiple threads
 * </p>
 *
 * @author Nikolai Neichev
 */
public interface CredentialsProvider {

  /**
   * Sets the credentials provider parameter.
   * <p/>
   * This parameter expects a value of type {@link CredentialsProvider}.
   * </p>
   */
  public static final String PROVIDER = "authentication.credential-provider";

  /**
   * Requests additional {@link Credentials authentication credentials}.
   *
   * @param scheme the {@link AuthScheme authentication scheme}
   * @param host   the authentication host
   * @param port   the port of the authentication host
   * @param proxy  <tt>true</tt> if authenticating with a proxy, <tt>false</tt> otherwise
	 * @return a credentials object
	 * @throws com.sap.httpclient.exception.CredentialsNotAvailableException if the credentials are not available
   */
  public Credentials getCredentials(final AuthScheme scheme, final String host, int port, boolean proxy)
          throws CredentialsNotAvailableException;

}
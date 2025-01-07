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

import com.sap.httpclient.HttpMethod;
import com.sap.httpclient.exception.AuthenticationException;
import com.sap.httpclient.exception.MalformedChallengeException;

/**
 * <p/>
 * This interface represents an abstract challenge-response oriented authentication scheme.
 * </p>
 * <p/>
 * An authentication scheme should be able to support the following functions:
 * <ul>
 * <li>Parse and process the challenge sent by the targer server
 * in response to request for a protected resource
 * <li>Provide its textual designation
 * <li>Provide its parameters, if available
 * <li>Provide the realm this authentication scheme is applicable to, if available
 * <li>Generate authorization string for the specified set of credentials,
 * request method and URI as specificed in the HTTP request line
 * in response to the actual authorization challenge
 * </ul>
 * </p>
 * <p/>
 * Authentication schemes may ignore method name and URI parameters
 * if they are not relevant for the specified authentication mechanism
 * </p>
 * <p/>
 * Authentication schemes may be stateful involving a series of challenge-response exchanges
 * </p>
 *
 * @author Nikolai Neichev
 */
public interface AuthScheme {

  /**
   * Processes the specified challenge token. Some authentication schemes
   * may involve multiple challenge-response exchanges. Such schemes must be able
   * to maintain the state information when dealing with sequential challenges
   *
   * @param challenge the challenge string
	 * @throws com.sap.httpclient.exception.MalformedChallengeException if the challenge in malformed
   */
  void processChallenge(final String challenge) throws MalformedChallengeException;

  /**
   * Returns textual designation of the specified authentication scheme.
   *
   * @return the name of the specified authentication scheme
   */
  String getSchemeName();

  /**
   * Returns authentication parameter with the specified name, if available.
   *
   * @param name The name of the parameter to be returned
   * @return the parameter with the specified name
   */
  String getParameter(final String name);

  /**
   * Returns authentication realm. If the concept of an authentication
   * realm is not applicable to the specified authentication scheme, returns <code>null</code>.
   *
   * @return the authentication realm
   */
  String getRealm();

  /**
   * Tests if the authentication scheme is provides authorization on a per
   * connection basis instead of usual per request basis
   *
   * @return <tt>true</tt> if the scheme is connection based, <tt>false</tt>
   *         if the scheme is request based.
   */
  boolean isConnectionBased();

  /**
   * Authentication process may involve a series of challenge-response exchanges.
   * This method tests if the authorization process has been completed, either
   * successfully or unsuccessfully, that is, all the required authorization
   * challenges have been processed in their entirety.
   *
   * @return <tt>true</tt> if the authentication process has been completed, <tt>false</tt> otherwise.
   */
  boolean isComplete();

  /**
   * Produces an authorization string for the specified set of {@link Credentials}.
   *
   * @param credentials The set of credentials to be used for athentication
   * @param method      The method being authenticated
   * @return the authorization string
   * @throws com.sap.httpclient.exception.AuthenticationException if authorization string cannot
   *                                 be generated due to an authentication failure
   */
  String authenticate(Credentials credentials, HttpMethod method) throws AuthenticationException;

}
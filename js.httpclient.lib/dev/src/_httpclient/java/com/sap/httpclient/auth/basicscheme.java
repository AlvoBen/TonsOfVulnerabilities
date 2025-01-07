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
import com.sap.httpclient.exception.InvalidCredentialsException;
import com.sap.httpclient.exception.MalformedChallengeException;
import com.sap.httpclient.uri.EncodingUtil;

/**
 * <p/>
 * Basic authentication scheme as defined in RFC 2617.
 * </p>
 *
 * @author Nikolai Neichev
 */

public class BasicScheme extends RFC2617Scheme {

  /**
   * Whether the basic authentication process is complete
   */
  private boolean complete;

  /**
   * Default constructor for the basic authetication scheme.
   */
  public BasicScheme() {
    super();
    this.complete = false;
  }

  /**
   * Returns textual designation of the basic authentication scheme.
   *
   * @return <code>basic</code>
   */
  public String getSchemeName() {
    return "basic";
  }

  /**
   * Processes the Basic challenge.
   *
   * @param challenge the challenge string
   * @throws MalformedChallengeException is thrown if the authentication challenge is malformed
   */
  public void processChallenge(String challenge) throws MalformedChallengeException {
    super.processChallenge(challenge);
    this.complete = true;
  }

  /**
   * Tests if the Basic authentication process has been completed.
   *
   * @return <tt>true</tt> if Basic authorization has been processed,
   *         <tt>false</tt> otherwise.
   */
  public boolean isComplete() {
    return this.complete;
  }

  /**
   * Returns <tt>false</tt>. Basic authentication scheme is request based.
   *
   * @return <tt>false</tt>.
   */
  public boolean isConnectionBased() {
    return false;
  }

  /**
   * Produces basic authorization string for the specified set of {@link Credentials}.
   *
   * @param credentials The set of credentials to be used for athentication
   * @param method      The method being authenticated
   * @return a basic authorization string
   * @throws InvalidCredentialsException if authentication credentials
   *                                     are not valid or not applicable for this authentication scheme
   * @throws com.sap.httpclient.exception.AuthenticationException     if authorization string cannot
   *                                     be generated due to an authentication failure
   */
  public String authenticate(Credentials credentials, HttpMethod method) throws AuthenticationException {
    if (method == null) {
      throw new IllegalArgumentException("Method is null");
    }
    UserPassCredentials usernamepassword;
    try {
      usernamepassword = (UserPassCredentials) credentials;
    } catch (ClassCastException e) {
      throw new InvalidCredentialsException("Credentials cannot be used for basic authentication: "
              + credentials.getClass().getName());
    }
    return BasicScheme.authenticate(usernamepassword, method.getParams().getCredentialCharset());
  }

  /**
   * Returns a basic <tt>Authorization</tt> header value for the specified
   * {@link UserPassCredentials} and charset.
   *
   * @param credentials The credentials to encode.
   * @param charset     The charset to use for encoding the credentials
   * @return a basic authorization string
   */
  public static String authenticate(UserPassCredentials credentials, String charset) {
    if (credentials == null) {
      throw new IllegalArgumentException("Credentials is null");
    }
    if (charset == null || charset.length() == 0) {
      throw new IllegalArgumentException("charset is null or empty");
    }
    StringBuilder buffer = new StringBuilder();
    buffer.append(credentials.getUserName());
    buffer.append(":");
    buffer.append(credentials.getPassword());
    return "Basic " + EncodingUtil.getBase64EncodedString(EncodingUtil.getBytes(buffer.toString(), charset));
  }

}
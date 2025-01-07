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

/**
 * An implementation of the Microsoft proprietary NTLM authentication scheme.  For a detailed
 * explanation of the NTLM scheme please see <a href="http://davenport.sourceforge.net/ntlm.html">
 * http://davenport.sourceforge.net/ntlm.html</a>.
 *
 */
public class NTLMScheme implements AuthScheme {

  /**
   * NTLM challenge string.
   */
  private String ntlmchallenge = null;

  private static final int UNINITIATED = 0;
  private static final int INITIATED = 1;
  private static final int TYPE1_MSG_GENERATED = 2;
  private static final int TYPE2_MSG_RECEIVED = 3;
  private static final int TYPE3_MSG_GENERATED = 4;
  private static final int FAILED = Integer.MAX_VALUE;

  /**
   * Authentication process state
   */
  private int state;

  /**
   * Default constructor for the NTLM authentication scheme.
   */
  public NTLMScheme() {
    super();
    this.state = UNINITIATED;
  }

  /**
   * Constructor for the NTLM authentication scheme.
   *
   * @param challenge The authentication challenge
   * @throws MalformedChallengeException is thrown if the authentication challenge
   *                                     is malformed
   */
  public NTLMScheme(final String challenge) throws MalformedChallengeException {
    super();
    processChallenge(challenge);
  }

  /**
   * Processes the NTLM challenge.
   *
   * @param challenge the challenge string
   * @throws com.sap.httpclient.exception.MalformedChallengeException is thrown if the authentication challenge
   *                                     is malformed
   */
  public void processChallenge(final String challenge) throws MalformedChallengeException {
    String s = AuthChallengeParser.extractScheme(challenge);
    if (!s.equalsIgnoreCase(getSchemeName())) {
      throw new MalformedChallengeException("Invalid NTLM challenge: " + challenge);
    }
    int i = challenge.indexOf(' ');
    if (i != -1) {
      s = challenge.substring(i, challenge.length());
      this.ntlmchallenge = s.trim();
      this.state = TYPE2_MSG_RECEIVED;
    } else {
      this.ntlmchallenge = "";
      if (this.state == UNINITIATED) {
        this.state = INITIATED;
      } else {
        this.state = FAILED;
      }
    }
  }

  /**
   * Tests if the NTLM authentication process has been completed.
   *
   * @return <tt>true</tt> if Basic authorization has been processed,
   *         <tt>false</tt> otherwise.
   */
  public boolean isComplete() {
    return this.state == TYPE3_MSG_GENERATED || this.state == FAILED;
  }

  /**
   * Returns textual designation of the NTLM authentication scheme.
   *
   * @return <code>ntlm</code>
   */
  public String getSchemeName() {
    return "ntlm";
  }

  /**
   * The concept of an authentication realm is not supported by the NTLM
   * authentication scheme. Always returns <code>null</code>.
   *
   * @return <code>null</code>
   */
  public String getRealm() {
    return null;
  }

  /**
   * Returns the authentication parameter with the specified name, if available.
   * <p/>
   * <p>There are no valid parameters for NTLM authentication so this method always returns
   * <tt>null</tt>.</p>
   *
   * @param name The name of the parameter to be returned
   * @return the parameter with the specified name
   */
  public String getParameter(String name) {
    if (name == null) {
      throw new IllegalArgumentException("Parameter name is null");
    }
    return null;
  }

  /**
   * Returns <tt>true</tt>. NTLM authentication scheme is connection based.
   *
   * @return <tt>true</tt>.
   */
  public boolean isConnectionBased() {
    return true;
  }

  /**
   * Produces NTLM authorization string for the specified set of
   * {@link Credentials}.
   *
   * @param credentials The set of credentials to be used for athentication
   * @param method      The method being authenticated
   * @return an NTLM authorization string
   * @throws InvalidCredentialsException if authentication credentials
   *                                     are not valid or not applicable for this authentication scheme
   * @throws com.sap.httpclient.exception.AuthenticationException     if authorization string cannot
   *                                     be generated due to an authentication failure
   */
  public String authenticate(Credentials credentials, HttpMethod method) throws AuthenticationException {
    if (this.state == UNINITIATED) {
      throw new IllegalStateException("NTLM authentication process has not been initiated");
    }
    NTCredentials ntcredentials;
    try {
      ntcredentials = (NTCredentials) credentials;
    } catch (ClassCastException e) {
      throw new InvalidCredentialsException("Credentials cannot be used for NTLM authentication: "
              + credentials.getClass().getName());
    }
    NTLM ntlm = new NTLM();
    ntlm.setCredentialCharset(method.getParams().getCredentialCharset());
    String response;
    if (this.state == INITIATED || this.state == FAILED) {
      response = ntlm.getType1Message(ntcredentials.getHost(), ntcredentials.getDomain());
      this.state = TYPE1_MSG_GENERATED;
    } else {
      try {
        response = ntlm.getType3Message(ntcredentials.getUserName(),
                ntcredentials.getPassword(),
                ntcredentials.getHost(),
                ntcredentials.getDomain(),
                ntlm.parseType2Message(this.ntlmchallenge));
      } catch(Exception e) {
        throw new AuthenticationException(e.getMessage(), e.getCause());
      }
      this.state = TYPE3_MSG_GENERATED;
    }
    return "NTLM " + response;
  }
}
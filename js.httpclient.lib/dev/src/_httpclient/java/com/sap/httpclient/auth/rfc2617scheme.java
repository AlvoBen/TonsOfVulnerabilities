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

import com.sap.httpclient.exception.MalformedChallengeException;

import java.util.Map;

/**
 * <p/>
 * Abstract authentication scheme class that lays foundation for all
 * RFC 2617 compliant authetication schemes and provides capabilities common
 * to all authentication schemes defined in RFC 2617.
 * </p>
 *
 * @author Nikolai Neichev
 */
public abstract class RFC2617Scheme implements AuthScheme {

  /**
   * Authentication parameter map.
   */
  private Map params = null;

  /**
   * Default constructor for RFC2617 compliant authetication schemes.
   */
  public RFC2617Scheme() {
    super();
  }

  /**
   * Processes the specified challenge token. Some authentication schemes
   * may involve multiple challenge-response exchanges. Such schemes must be able
   * to maintain the state information when dealing with sequential challenges
   *
   * @param challenge the challenge string
   * @throws MalformedChallengeException is thrown if the authentication challenge is malformed
   */
  public void processChallenge(final String challenge) throws MalformedChallengeException {
    String s = AuthChallengeParser.extractScheme(challenge);
    if (!s.equalsIgnoreCase(getSchemeName())) {
      throw new MalformedChallengeException("Invalid " + getSchemeName() + " challenge: " + challenge);
    }
    this.params = AuthChallengeParser.extractParams(challenge);
  }

  /**
   * Returns authentication parameters map. Keys in the map are lower-cased.
   *
   * @return the map of authentication parameters
   */
  protected Map getParameters() {
    return this.params;
  }

  /**
   * Returns authentication parameter with the specified name, if available.
   *
   * @param name The name of the parameter to be returned
   * @return the parameter with the specified name
   */
  public String getParameter(String name) {
    if (name == null) {
      throw new IllegalArgumentException("Parameter name is null");
    }
    if (this.params == null) {
      return null;
    }
    return (String) this.params.get(name.toLowerCase());
  }

  /**
   * Returns authentication realm. The realm is null.
   *
   * @return the authentication realm
   */
  public String getRealm() {
    return getParameter("realm");
  }

}
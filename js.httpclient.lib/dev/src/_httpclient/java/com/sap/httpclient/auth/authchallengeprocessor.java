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

import com.sap.httpclient.Parameters;
import com.sap.httpclient.exception.AuthChallengeException;
import com.sap.httpclient.exception.AuthenticationException;
import com.sap.httpclient.exception.MalformedChallengeException;
import com.sap.tc.logging.Location;

import java.util.Collection;
import java.util.Map;


/**
 * This class provides utility methods for processing HTTP www and proxy authentication
 * challenges.
 *
 * @author Nikolai Neichev
 */
public final class AuthChallengeProcessor {

  private static final Location LOG = Location.getLocation(AuthChallengeProcessor.class);

  private Parameters params = null;

  /**
   * Creates an authentication challenge processor with the specified {@link Parameters HTTP
   * parameters}
   *
   * @param params the {@link Parameters HTTP parameters} used by this processor
   */
  public AuthChallengeProcessor(final Parameters params) {
    super();
    if (params == null) {
      throw new IllegalArgumentException("Parameter collection is null");
    }
    this.params = params;
  }

  /**
   * Determines the preferred {@link AuthScheme authentication scheme} that can be used
   * to respond to the specified collection of challenges.
   *
   * @param challenges the collection of authentication challenges
   * @return the preferred {@link AuthScheme authentication scheme}
   * @throws com.sap.httpclient.exception.AuthChallengeException if the preferred authentication scheme
   *                                cannot be determined or is not supported
   */
  public AuthScheme selectAuthScheme(final Map<String, String> challenges) throws AuthChallengeException {
    if (challenges == null) {
      throw new IllegalArgumentException("Challenge is null");
    }
    Collection authPrefs = (Collection) this.params.getParameter(AuthPolicy.AUTH_SCHEME_PRIORITY);
    if (authPrefs == null || authPrefs.isEmpty()) {
      authPrefs = AuthPolicy.getDefaultAuthPrefs();
    }
    if (LOG.beDebug()) {
      LOG.debugT("Supported authentication schemes in the order of preference: " + authPrefs);
    }
    AuthScheme authscheme = null;
    String challenge;
		for (Object authPref : authPrefs) {
			String id = (String) authPref;
			challenge = challenges.get(id.toLowerCase());
			if (challenge != null) {
				if (LOG.beInfo()) {
					LOG.infoT(id + " authentication scheme selected");
				}
				try {
					authscheme = AuthPolicy.getAuthScheme(id);
				} catch (IllegalStateException e) {
					throw new AuthChallengeException(e.getMessage(), e.getCause());
				}
				break;
			} else {
				if (LOG.beDebug()) {
					LOG.debugT("Challenge for " + id + " authentication scheme not available");
					// Try again
				}
			}
		}
		if (authscheme == null) {
      // If none selected, something is wrong
      throw new AuthChallengeException("Unable to respond to any of these challenges: " + challenges);
    }
    return authscheme;
  }

  /**
   * Processes the specified collection of challenges and updates the
   * {@link AuthState state} of the authentication process.
   *
	 * @param state the authentication state
   * @param challenges the collection of authentication challenges
   * @return the {@link AuthScheme authentication scheme} used to
   *         process the challenge
   * @throws AuthChallengeException if authentication challenges cannot be successfully processed or
	 * 																the preferred authentication scheme cannot be determined
	 * @throws com.sap.httpclient.exception.MalformedChallengeException if the challenge is malformed 
   */
  public AuthScheme processChallenge(final AuthState state, final Map<String, String> challenges)
          throws MalformedChallengeException, AuthenticationException {
    if (state == null) {
      throw new IllegalArgumentException("Authentication state is null");
    }
    if (challenges == null) {
      throw new IllegalArgumentException("Challenge is null");
    }

    if (state.isPreemptive() || state.getAuthScheme() == null) {
      // Authentication not attempted before
      state.setAuthScheme(selectAuthScheme(challenges));
    }
    AuthScheme authscheme = state.getAuthScheme();
    String id = authscheme.getSchemeName();
    if (LOG.beDebug()) {
      LOG.debugT("Using authentication scheme: " + id);
    }
    String challenge = challenges.get(id.toLowerCase());
    if (challenge == null) {
      throw new AuthenticationException(id + " authorization challenge expected, but not found");
    }
    authscheme.processChallenge(challenge);
    LOG.debugT("Authorization challenge processed");
    return authscheme;
  }
}
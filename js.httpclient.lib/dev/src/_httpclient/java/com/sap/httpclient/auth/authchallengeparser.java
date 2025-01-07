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

import com.sap.httpclient.NameValuePair;
import com.sap.httpclient.exception.MalformedChallengeException;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.utils.ParameterParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * This class provides utility methods for parsing HTTP www and proxy authentication
 * challenges.
 *
 * @author Nikolai Neichev
 */
public final class AuthChallengeParser {
  /**
   * Extracts authentication scheme from the specified authentication challenge.
   *
   * @param challengeStr the authentication challenge string
   * @return authentication scheme
   * @throws MalformedChallengeException when the authentication challenge string is malformed
   */
  public static String extractScheme(final String challengeStr) throws MalformedChallengeException {
    if (challengeStr == null) {
      throw new IllegalArgumentException("Challenge is null");
    }
    int idx = challengeStr.indexOf(' ');
    String s;
    if (idx == -1) {
      s = challengeStr;
    } else {
      s = challengeStr.substring(0, idx);
    }
    if (s.equals("")) {
      throw new MalformedChallengeException("Invalid challenge: " + challengeStr);
    }
    return s.toLowerCase();
  }

  /**
   * Extracts a map of challenge parameters from an authentication challenge.
   * Keys in the map are lower-cased
   *
   * @param challengeStr the authentication challenge string
   * @return a map of authentication challenge parameters
   * @throws MalformedChallengeException when the authentication challenge string is malformed
   */
  public static Map<String, String> extractParams(final String challengeStr) throws MalformedChallengeException {
    if (challengeStr == null) {
      throw new IllegalArgumentException("Challenge is null");
    }
    int idx = challengeStr.indexOf(' ');
    if (idx == -1) {
      throw new MalformedChallengeException("Invalid challenge: " + challengeStr);
    }
    Map<String, String> map = new HashMap<String, String>();
    ParameterParser parser = new ParameterParser();
    List<NameValuePair> params = parser.parse(challengeStr.substring(idx + 1, challengeStr.length()), ',');
    for (NameValuePair param: params) {
      map.put(param.getName().toLowerCase(), param.getValue());
    }
    return map;
  }

  /**
   * Extracts a map of challenges ordered by authentication scheme name
   *
   * @param headers the array of authorization challenges
   * @return a map of authorization challenges
   * @throws MalformedChallengeException if any of challenge strings
   *                                     is malformed
   */
  public static Map<String, String> parseChallenges(final ArrayList<Header> headers) throws MalformedChallengeException {
    if (headers == null) {
      throw new IllegalArgumentException("headers is null");
    }
    String challenge;
    Map<String, String> challengemap = new HashMap<String, String>(headers.size());
    for (Header header : headers) {
      challenge = header.getValue();
      String s = AuthChallengeParser.extractScheme(challenge);
      challengemap.put(s, challenge);
    }
    return challengemap;
  }
}
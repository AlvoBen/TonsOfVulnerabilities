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

import java.util.*;

/**
 * Represents a state, holds the {@link Cookie cookies} and authentication {@link Credentials credentials}.
 *
 * @author Nikolai Neichev
 */
public class HttpState {

  /**
   * Map of {@link Credentials credentials} by realm that this HTTP state contains.
   */
  private HashMap<AuthScope, Credentials> credMap = new HashMap<AuthScope, Credentials>();

  /**
   * Map of {@link Credentials proxy credentials} by realm that this HTTP state contains
   */
  private HashMap<AuthScope, Credentials> proxyCred = new HashMap<AuthScope, Credentials>();

  /**
   * Array of {@link Cookie cookies} that this HTTP state contains.
   */
  private ArrayList<Cookie> cookies = new ArrayList<Cookie>();

  /** The boolean system property name to turn on preemptive authentication. */
  public static final String PREEMPTIVE_PROPERTY = "httpclient.authentication.preemptive";

  /** The default value for {@link #PREEMPTIVE_PROPERTY}. */
  public static final String PREEMPTIVE_DEFAULT = "false";

  /**
   * Default constructor.
   */
  public HttpState() {
    super();
  }

  /**
   * Adds the specified {@link Cookie HTTP cookie}, replacing existing equivalent ones.
   *
   * @param cookie the {@link Cookie cookie} to be added
   */
  public synchronized void addCookie(Cookie cookie) {
    if (cookie != null) {
      // remove the old cookies that is equivalent
      for (Iterator<Cookie> it = cookies.iterator(); it.hasNext();) {
        Cookie tmp = it.next();
        if (cookie.equals(tmp)) {
          it.remove();
          break;
        }
      }
      if (!cookie.isExpired()) {
        cookies.add(cookie);
      }
    }
  }

  /**
   * Adds an array of {@link Cookie HTTP cookies}.
   *
   * @param cookies the {@link Cookie cookies} to be added
   */
  public synchronized void addCookies(Cookie[] cookies) {
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        this.addCookie(cookie);
      }
    }
  }

  /**
   * Returns an array of {@link Cookie cookies} that this HTTP state currently contains.
   *
   * @return an array of {@link Cookie cookies}.
   */
  public synchronized Cookie[] getCookies() {
    return (cookies.toArray(new Cookie[cookies.size()]));
  }

  /**
   * Removes all of expired {@link Cookie cookies}.
	 * @return TRUE if any cookie was purged
	 */
  public synchronized boolean purgeExpiredCookies() {
    return purgeExpiredCookies(new Date());
  }

  /**
   * Removes all of expired {@link Cookie cookies} for the specified current time
   *
   * @param date The {@link java.util.Date date} give as gurrent.
   * @return true if any cookies were purged.
   */
  public synchronized boolean purgeExpiredCookies(Date date) {
    boolean removed = false;
    Iterator<Cookie> it = cookies.iterator();
    while (it.hasNext()) {
      if (((it.next())).isExpired(date)) {
        it.remove();
        removed = true;
      }
    }
    return removed;
  }

  /**
   * Sets the {@link Credentials credentials} for the specified authentication scope.
   *
   * @param authscope   the {@link AuthScope authentication scope}
   * @param credentials the authentication {@link Credentials credentials} for the specified scope.
   */
  public synchronized void setCredentials(final AuthScope authscope, final Credentials credentials) {
    if (authscope == null) {
      throw new IllegalArgumentException("Authentication scope is null");
    }
    credMap.put(authscope, credentials);
  }

  /**
   * Find matching {@link com.sap.httpclient.auth.Credentials credentials} for the specified authentication scope.
   *
   * @param map   the credentials hash map
   * @param authscope the {@link AuthScope authentication scope}
   * @return the credentials
   */
  private static Credentials matchCredentials(final HashMap<AuthScope, Credentials> map, final AuthScope authscope) {
    Credentials creds = map.get(authscope);
    if (creds == null) { // not found in the map, we'll check all
      int bestMatchFactor = -1;
      AuthScope bestMatch = null;
			for (AuthScope current : map.keySet()) {
				int factor = authscope.match(current);
				if (factor > bestMatchFactor) {
					bestMatchFactor = factor;
					bestMatch = current;
				}
			}
			if (bestMatch != null) {
        creds = map.get(bestMatch);
      }
    }
    return creds;
  }

  /**
   * Get the {@link Credentials credentials} for the specified authentication scope.
   *
   * @param authscope the {@link AuthScope authentication scope}
   * @return the credentials
   */
  public synchronized Credentials getCredentials(final AuthScope authscope) {
    if (authscope == null) {
      throw new IllegalArgumentException("Authentication scope is null");
    }
    return matchCredentials(this.credMap, authscope);
  }

  /**
   * Sets the {@link Credentials proxy credentials} for the specified authentication scope
   *
   * @param authscope   the {@link AuthScope authentication scope}
   * @param credentials the authentication {@link Credentials credentials} for the specified scope.
   */
  public synchronized void setProxyCredentials(final AuthScope authscope, final Credentials credentials) {
    if (authscope == null) {
      throw new IllegalArgumentException("Authentication scope is null");
    }
    proxyCred.put(authscope, credentials);
  }

  /**
   * Get the {@link Credentials proxy credentials} for the specified authentication scope.
   *
   * @param authscope the {@link AuthScope authentication scope}
   * @return the credentials
   */
  public synchronized Credentials getProxyCredentials(final AuthScope authscope) {
    if (authscope == null) {
      throw new IllegalArgumentException("Authentication scope is null");
    }
    return matchCredentials(this.proxyCred, authscope);
  }

  /**
   * Returns a string representation of this HTTP state.
   *
   * @return The string representation of the HTTP state.
   */
  public synchronized String toString() {
    StringBuilder sbResult = new StringBuilder();
    sbResult.append("[");
    sbResult.append(getCredentialsAsString(proxyCred));
    sbResult.append(" | ");
    sbResult.append(getCredentialsAsString(credMap));
    sbResult.append(" | ");
    sbResult.append(getCookiesAsString(cookies));
    sbResult.append("]");
		return sbResult.toString();
  }

  /**
   * Returns a string representation of the credentials.
   *
   * @param credMap The credentials.
   * @return The string representation.
   */
  private static String getCredentialsAsString(final Map<AuthScope, Credentials> credMap) {
    StringBuilder sbResult = new StringBuilder();
		for (AuthScope key : credMap.keySet()) {
			Credentials cred = credMap.get(key);
			if (sbResult.length() > 0) {
				sbResult.append(", ");
			}
			sbResult.append(key);
			sbResult.append("#");
			sbResult.append(cred.toString());
		}
		return sbResult.toString();
  }

  /**
   * Returns a string representation of the cookies.
   *
   * @param cookies The cookies
   * @return The string representation.
   */
  private static String getCookiesAsString(final List<Cookie> cookies) {
    StringBuilder sbResult = new StringBuilder();
		for (Cookie ck : cookies) {
			if (sbResult.length() > 0) {
				sbResult.append("#");
			}
			sbResult.append(ck.toText());
		}
		return sbResult.toString();
  }

  /**
   * Clears all credentials.
   */
  public void clearCredentials() {
    this.credMap.clear();
  }

  /**
   * Clears all proxy credentials.
   */
  public void clearProxyCredentials() {
    this.proxyCred.clear();
  }

  /**
   * Clears all cookies.
   */
  public void clearCookies() {
    this.cookies.clear();
  }

  /**
   * Clears the state information (all cookies, credentials and proxy credentials).
   */
  public void clear() {
    clearCookies();
    clearCredentials();
    clearProxyCredentials();
  }
}
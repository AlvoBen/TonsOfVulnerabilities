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

import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Authentication policy class. The Authentication policy provides corresponding
 * authentication scheme interfrace for a specified type of authorization challenge.
 * <p>The following specifications are provided:
 * <ul>
 * <li><tt>Basic</tt>: Basic authentication scheme as defined in RFC2617
 * (considered inherently insecure, but most widely supported)
 * <li><tt>Digest</tt>: Digest authentication scheme as defined in RFC2617
 * <li><tt>NTLM</tt>: The NTLM scheme is a proprietary Microsoft Windows
 * Authentication net (considered to be the most secure among
 * currently supported authentication schemes)
 * </ul>
 *
 * @author Nikolai Neichev
 */
public abstract class AuthPolicy {

  private static final HashMap<String, Class> SCHEMES = new HashMap<String, Class>();
  private static final ArrayList<String> SCHEME_LIST = new ArrayList<String>();

  /**
   * The key used to look up the list of IDs of supported {@link AuthScheme
   * authentication schemes} in their order of preference. The scheme IDs are
   * stored in a {@link java.util.Collection} as {@link java.lang.String}s.
   * <p/>
   * <p/>
   * If several schemes are returned in the <tt>WWW-Authenticate</tt>
   * or <tt>Proxy-Authenticate</tt> header, this parameter defines which
   * {@link AuthScheme authentication schemes} takes precedence over others.
   * The first item in the collection represents the most preferred
   * {@link AuthScheme authentication scheme}, the last item represents the ID
   * of the least preferred one.
   * </p>
   */
  public static final String AUTH_SCHEME_PRIORITY = "auth-scheme-priority";

  /**
   * The NTLM scheme is a proprietary Microsoft Windows Authentication
   * net (considered to be the most secure among currently supported
   * authentication schemes).
   */
  public static final String NTLM = "NTLM";

  /**
   * Digest authentication scheme as defined in RFC2617.
   */
  public static final String DIGEST = "Digest";

  /**
   * Basic authentication scheme as defined in RFC2617 (considered inherently
   * insecure, but most widely supported)
   */
  public static final String BASIC = "Basic";

  static {
    AuthPolicy.registerAuthScheme(NTLM, NTLMScheme.class);
    AuthPolicy.registerAuthScheme(DIGEST, DigestScheme.class);
    AuthPolicy.registerAuthScheme(BASIC, BasicScheme.class);
  }

  /**
   * Log object.
   */
  protected static final Location LOG = Location.getLocation(AuthPolicy.class);

  /**
   * Registers a class implementing an {@link AuthScheme authentication scheme} with
   * the specified identifier. If a class with the specified ID already exists it will be overridden.
   * This ID is the same one used to retrieve the {@link AuthScheme authentication scheme}
   * from {@link #getAuthScheme(String)}.
   * <p/>
   * <p/>
   * Please note that custom authentication preferences, if used, need to be updated accordingly
   * for the new {@link AuthScheme authentication scheme} to take effect.
   * </p>
   *
   * @param id    the identifier for this scheme
   * @param clazz the class to register
   */
  public static synchronized void registerAuthScheme(final String id, Class clazz) {
    if (id == null) {
      throw new IllegalArgumentException("Id is null");
    }
    if (clazz == null) {
      throw new IllegalArgumentException("Authentication scheme class is null");
    }
    SCHEMES.put(id.toLowerCase(), clazz);
    SCHEME_LIST.add(id.toLowerCase());
  }

  /**
   * Unregisters the class implementing an {@link AuthScheme authentication scheme} with
   * the specified ID.
   *
   * @param id the ID of the class to unregister
   */
  public static synchronized void unregisterAuthScheme(final String id) {
    if (id == null) {
      throw new IllegalArgumentException("Id is null");
    }
    SCHEMES.remove(id.toLowerCase());
    SCHEME_LIST.remove(id.toLowerCase());
  }

  /**
   * Gets the {@link AuthScheme authentication scheme} with the specified ID.
   *
   * @param id the {@link AuthScheme authentication scheme} ID
   * @return {@link AuthScheme authentication scheme} the auth scheme
   * @throws IllegalStateException if a scheme with the ID cannot be found
   */
  public static synchronized AuthScheme getAuthScheme(final String id) throws IllegalStateException {
    if (id == null) {
      throw new IllegalArgumentException("Id is null");
    }
    Class clazz = SCHEMES.get(id.toLowerCase());
    if (clazz != null) {
      try {
        return (AuthScheme) clazz.newInstance();
      } catch (Exception e) {
        LOG.traceThrowableT(Severity.ERROR, "Error initializing authentication scheme: " + id, e);
        throw new IllegalStateException(id + " authentication scheme implemented by " +
                                        clazz.getName() + " could not be initialized");
      }
    } else {
      throw new IllegalStateException("Unsupported authentication scheme " + id);
    }
  }

  /**
   * Returns a list containing all registered {@link AuthScheme authentication
   * schemes} in their default order.
   *
   * @return {@link AuthScheme authentication scheme}  the dafault auth preferences
   */
  public static synchronized List getDefaultAuthPrefs() {
    return (List) SCHEME_LIST.clone();
  }
}
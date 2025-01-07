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
package com.sap.httpclient.http.cookie;

import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Cookie management policy class. The cookie policy provides corresponding
 * cookie management interfrace for a specified type or version of cookie.
 * <p>RFC 2109 specification is used per default. Other supported specification
 * can be  chosen when appropriate or set default when desired
 * <p>The following specifications are provided:
 * <ul>
 * <li><tt>BROWSER_COMPATIBILITY</tt>: compatible with the common cookie
 * management practices (even if they are not 100% standards compliant)
 * <li><tt>NETSCAPE</tt>: Netscape cookie draft compliant
 * <li><tt>RFC_2109</tt>: RFC2109 compliant (default)
 * <li><tt>IGNORE_COOKIES</tt>: do not automcatically process cookies
 * </ul>
 *
 * @author Nikolai Neichev
 */
public abstract class CookiePolicy {

  private static Map<String, Class> SPECS = Collections.synchronizedMap(new HashMap<String, Class>());

  /**
   * The policy that provides high degree of compatibilty
   * with common cookie management of popular HTTP agents.
   */
  public static final String BROWSER_COMPATIBILITY = "compatibility";

  /**
   * The Netscape cookie draft compliant policy.
   */
  public static final String NETSCAPE = "netscape";

  /**
   * The RFC 2109 compliant policy.
   */
  public static final String RFC_2109 = "rfc2109";

  /**
   * The policy that ignores cookies.
   */
  public static final String IGNORE_COOKIES = "ignoreCookies";

  /**
   * The default cookie policy.
   */
  public static final String DEFAULT = "default";

  static {
    CookiePolicy.registerCookieSpec(DEFAULT, RFC2109Spec.class);
    CookiePolicy.registerCookieSpec(RFC_2109, RFC2109Spec.class);
    CookiePolicy.registerCookieSpec(BROWSER_COMPATIBILITY, CookieSpecBase.class);
    CookiePolicy.registerCookieSpec(NETSCAPE, NetscapeDraftSpec.class);
    CookiePolicy.registerCookieSpec(IGNORE_COOKIES, IgnoreCookiesSpec.class);
  }

  /**
   * Log object.
   */
  protected static final Location LOG = Location.getLocation(CookiePolicy.class);

  /**
   * Registers a new {@link CookieSpec cookie specification} with the specified identifier.
   * If a specification with the specified ID already exists it will be overridden.
   * This ID is the same one used to retrieve the {@link CookieSpec cookie specification}
   * from {@link #getCookieSpec(String)}.
   *
   * @param id    the identifier for this specification
   * @param clazz the {@link CookieSpec cookie specification} class to register
   */
  public static void registerCookieSpec(final String id, final Class clazz) {
    if (id == null) {
      throw new IllegalArgumentException("Id is null");
    }
    if (clazz == null) {
      throw new IllegalArgumentException("Cookie spec class is null");
    }
    SPECS.put(id.toLowerCase(), clazz);
  }

  /**
   * Unregisters the {@link CookieSpec cookie specification} with the specified ID.
   *
   * @param id the ID of the {@link CookieSpec cookie specification} to unregister
   */
  public static void unregisterCookieSpec(final String id) {
    if (id == null) {
      throw new IllegalArgumentException("Id is null");
    }
    SPECS.remove(id.toLowerCase());
  }

  /**
   * Gets the {@link CookieSpec cookie specification} with the specified ID.
   *
   * @param id the {@link CookieSpec cookie specification} ID
   * @return {@link CookieSpec cookie specification} the cookie spec
   * @throws IllegalStateException if a policy with the ID cannot be found
   */
  public static CookieSpec getCookieSpec(final String id) throws IllegalStateException {
    if (id == null) {
      throw new IllegalArgumentException("Id is null");
    }
    Class clazz = SPECS.get(id.toLowerCase());
    if (clazz != null) {
      try {
        return (CookieSpec) clazz.newInstance();
      } catch (Exception e) {
        LOG.traceThrowableT(Severity.ERROR, "Error initializing cookie spec: " + id, e);
        throw new IllegalStateException(id + " cookie spec implemented by " +
                clazz.getName() + " could not be initialized");
      }
    } else {
      throw new IllegalStateException("Unsupported cookie spec " + id);
    }
  }

  /**
   * Returns {@link CookieSpec cookie specification} registered as {@link #DEFAULT}.
   * If no default {@link CookieSpec cookie specification} has been registered,
   * {@link RFC2109Spec RFC2109 specification} is returned.
   *
   * @return default {@link CookieSpec cookie specification}
   */
  public static CookieSpec getDefaultSpec() {
    try {
      return getCookieSpec(DEFAULT);
    } catch (IllegalStateException e) {
      LOG.warningT("Default cookie policy is not registered");
      return new RFC2109Spec();
    }
  }

}
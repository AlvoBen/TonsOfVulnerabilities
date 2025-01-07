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

import com.sap.httpclient.NameValuePair;
import java.io.Serializable;
import java.text.RuleBasedCollator;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 * Cookie representarion
 *
 * @author Nikoali Neichev
 */
public class Cookie extends NameValuePair implements Serializable, Comparator {

  /**
   * Comment attribute.
   */
  private String cookieComment;

  /**
   * Domain attribute.
   */
  private String cookieDomain;

  /**
   * Expiration {@link Date}.
   */
  private Date cookieExpiryDate;

  /**
   * Path attribute.
   */
  private String cookiePath;

  /**
   * Secure flag.
   */
  private boolean isSecure;

  /**
   * Specifies if the set-cookie header included a Path attribute for this cookie
   */
  private boolean hasPathAttribute = false;

  /**
   * Specifies if the set-cookie header included a Domain attribute for this cookie
   */
  private boolean hasDomainAttribute = false;

  /**
   * The version of the cookie specification.
   */
  private int cookieVersion = 0;

  /**
   * Collator for Cookie comparisons. Could be replaced with references to specific Locales.
   */
  private static final RuleBasedCollator STRING_COLLATOR =
          (RuleBasedCollator) RuleBasedCollator.getInstance(new Locale("en", "US", ""));

  /**
   * Default constructor. Creates a blank cookie
   */
  public Cookie() {
    this(null, "noname", null, null, null, false);
  }

  /**
   * Creates a cookie with the specified name, value and domain attribute.
   *
   * @param name   the cookie name
   * @param value  the cookie value
   * @param domain the domain this cookie can be sent to
   */
  public Cookie(String domain, String name, String value) {
    this(domain, name, value, null, null, false);
  }

  /**
   * Creates a cookie with the specified name, value, domain attribute,
   * path attribute, expiration attribute, and secure attribute
   *
   * @param name    the cookie name
   * @param value   the cookie value
   * @param domain  the domain this cookie can be sent to
   * @param path    the path prefix for which this cookie can be sent
   * @param expires the {@link Date} at which this cookie expires,
   *                or <tt>null</tt> if the cookie expires at the end of the session
   * @param secure  if true this cookie can only be sent over secure connections
   * @throws IllegalArgumentException If cookie name is null or blank,
   *                                  cookie name contains a blank, or cookie name starts with character $
   */
  public Cookie(String domain, String name, String value, String path, Date expires, boolean secure) {
    super(name, value);
    if (name == null) {
      throw new IllegalArgumentException("Cookie name is null");
    }
    if (name.trim().equals("")) {
      throw new IllegalArgumentException("Cookie name may not be blank");
    }
    this.setPath(path);
    this.setDomain(domain);
    this.setExpiryDate(expires);
    this.setSecure(secure);
  }

  /**
   * Creates a cookie with the specified name, value, domain attribute,
   * path attribute, maximum age attribute, and secure attribute
   *
   * @param name   the cookie name
   * @param value  the cookie value
   * @param domain the domain this cookie can be sent to
   * @param path   the path prefix for which this cookie can be sent
   * @param maxAge the number of seconds for which this cookie is valid.
   *               maxAge is expected to be a non-negative number.
   *               <tt>-1</tt> signifies that the cookie should never expire.
   * @param secure if <tt>true</tt> this cookie can only be sent over secure connections
   */
  public Cookie(String domain, String name, String value, String path, int maxAge, boolean secure) {
    this(domain, name, value, path, null, secure);
    if (maxAge < -1) {
      throw new IllegalArgumentException("Invalid max age:  " + Integer.toString(maxAge));
    }
    if (maxAge >= 0) {
      setExpiryDate(new Date(System.currentTimeMillis() + maxAge * 1000L));
    }
  }

  /**
   * Returns the comment describing the purpose of this cookie
   *
   * @return comment, <tt>null</tt> if not defined.
   */
  public String getComment() {
    return cookieComment;
  }

  /**
   * Set the cookie comment
   *
   * @param comment the comment to set
   */
  public void setComment(String comment) {
    cookieComment = comment;
  }

  /**
   * Returns the expiration {@link Date} of the cookie.
   *
   * @return Expiration {@link Date}, or <tt>null</tt>.
   */
  public Date getExpiryDate() {
    return cookieExpiryDate;
  }

  /**
   * Sets expiration {@link Date} after which this cookie is no longer valid.
   *
   * @param expiryDate the {@link Date}
   */
  public void setExpiryDate(Date expiryDate) {
    cookieExpiryDate = expiryDate;
  }

  /**
   * Checks if the cookie is persistent
   *
   * @return <tt>false</tt> if the cookie should be discarded at the end of the "session"; <tt>true</tt> otherwise
   */
  public boolean isPersistent() {
    return (null != cookieExpiryDate);
  }

  /**
   * Returns domain attribute of the cookie.
   *
   * @return the value of the domain attribute
   */
  public String getDomain() {
    return cookieDomain;
  }

  /**
   * Sets the domain attribute.
   *
   * @param domain The value of the domain attribute
   */
  public void setDomain(String domain) {
    if (domain != null) {
      int ndx = domain.indexOf(":");
      if (ndx != -1) {
        domain = domain.substring(0, ndx);
      }
      cookieDomain = domain.toLowerCase();
    }
  }

  /**
   * Returns the path attribute of the cookie
   *
   * @return The value of the path attribute.
   */
  public String getPath() {
    return cookiePath;
  }

  /**
   * Sets the path attribute.
   *
   * @param path The value of the path attribute
   */
  public void setPath(String path) {
    cookiePath = path;
  }

  /**
   * Checks if the cookie is secure
   *
   * @return <code>true</code> if this cookie should only be sent over secure connections.
   */
  public boolean getSecure() {
    return isSecure;
  }

  /**
   * Sets the secure attribute of the cookie.
   *
   * @param secure The value of the secure attribute
   */
  public void setSecure(boolean secure) {
    isSecure = secure;
  }

  /**
   * Returns the version of the cookie.
   *
   * @return the version of the cookie.
   */
  public int getVersion() {
    return cookieVersion;
  }

  /**
   * Sets the version of the cookie
   *
   * @param version the version of the cookie.
   */
  public void setVersion(int version) {
    cookieVersion = version;
  }

  /**
   * Checks if the cookie is expired.
   *
   * @return <tt>true</tt> if the cookie has expired.
   */
  public boolean isExpired() {
    return (cookieExpiryDate != null && ( cookieExpiryDate.getTime() <= System.currentTimeMillis() ) );
  }

  /**
   * Checks if the cookie is expired for a specified current time
   *
   * @param now The specified current time.
   * @return <tt>true</tt> if the cookie expired.
   */
  public boolean isExpired(Date now) {
    return (cookieExpiryDate != null && ( cookieExpiryDate.getTime() <= now.getTime() ) );
  }

  /**
   * Indicates whether the cookie had a path specified
   *
   * @param value <tt>true</tt> if the cookie's path was explicitly set, <tt>false</tt> otherwise.
   */
  public void setPathAttributeSpecified(boolean value) {
    hasPathAttribute = value;
  }

  /**
   * Returns <tt>true</tt> if cookie's path was set via a path attribute in the <tt>Set-Cookie</tt> header.
   *
   * @return value <tt>true</tt> if the cookie's path was explicitly set, <tt>false</tt> otherwise.
   */
  public boolean isPathAttributeSpecified() {
    return hasPathAttribute;
  }

  /**
   * Indicates whether the cookie had a domain specified
   *
   * @param value <tt>true</tt> if the cookie's domain was explicitly set, <tt>false</tt> otherwise.
   */
  public void setDomainAttributeSpecified(boolean value) {
    hasDomainAttribute = value;
  }

  /**
   * Returns <tt>true</tt> if cookie's domain was set via a domain attribute in the <tt>Set-Cookie</tt> header.
   *
   * @return value <tt>true</tt> if the cookie's domain was explicitly set, <tt>false</tt> otherwise.
   */
  public boolean isDomainAttributeSpecified() {
    return hasDomainAttribute;
  }

  public int hashCode() {
    int hash = 0;
    if (this.getName() != null) {
      hash += this.getName().hashCode();
    }
    if (this.cookieDomain != null) {
      hash += this.cookieDomain.hashCode();
    }
    if (this.cookiePath != null) {
      hash += this.cookiePath.hashCode();
    }
    return hash;
  }

  /**
   * Compares this cookie with the given object.
   *
   * @param obj The object to compare against.
   * @return true the name, path and domain match.
   */
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (this == obj) return true;
    if (obj instanceof Cookie) {
      Cookie that = (Cookie) obj;
      return areEqual(this.getName(), that.getName())
              && areEqual(this.cookieDomain, that.cookieDomain)
              && areEqual(this.cookiePath, that.cookiePath);
    } else {
      return false;
    }
  }

  private boolean areEqual(final Object obj1, final Object obj2) {
    return obj1 == null ? obj2 == null : obj1.equals(obj2);
  }

  /**
   * Return a textual representation of the cookie.
   *
   * @return string.
   */
  public String toText() {
    CookieSpec spec;
    if (getVersion() > 0) {
      spec = CookiePolicy.getDefaultSpec();
    } else {
      spec = CookiePolicy.getCookieSpec(CookiePolicy.NETSCAPE);
    }
    return spec.formatCookie(this);
  }

  /**
   * Compares two cookies to determine order for cookie header.
   *
   * @param o1 The first object to be compared
   * @param o2 The second object to be compared
   * @return See {@link java.util.Comparator#compare(Object,Object)}
   */
  public int compare(Object o1, Object o2) {
    if (!(o1 instanceof Cookie)) {
      throw new ClassCastException(o1.getClass().getName());
    }
    if (!(o2 instanceof Cookie)) {
      throw new ClassCastException(o2.getClass().getName());
    }
    Cookie c1 = (Cookie) o1;
    Cookie c2 = (Cookie) o2;
    if (c1.getPath() == null && c2.getPath() == null) {
      return 0;
    } else if (c1.getPath() == null) { // null is "/" by default
      if (c2.getPath().equals(CookieSpec.PATH_DELIM)) {
        return 0;
      } else {
        return -1;
      }
    } else if (c2.getPath() == null) { // null is "/" by default
      if (c1.getPath().equals(CookieSpec.PATH_DELIM)) {
        return 0;
      } else {
        return 1;
      }
    } else {
      return STRING_COLLATOR.compare(c1.getPath(), c2.getPath());
    }
  }

  /**
   * Return a textual representation of the cookie.
   *
   * @return string.
   */
  public String toString() {
    return toText();
  }

}
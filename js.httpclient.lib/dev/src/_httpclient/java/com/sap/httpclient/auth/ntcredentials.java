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

/**
 * {@link com.sap.httpclient.auth.Credentials} for use with the NTLM authentication scheme which requires additional
 * information.
 *
 * @author Nikolai Neichev
 */
public class NTCredentials extends UserPassCredentials {

  /**
   * The Domain to authenticate with.
   */
  private String domain;

  /**
   * The host the authentication request is originating from.
   */
  private String host;

  /**
   * Constructor.
   *
   * @param userName The user name.  This should not include the domain to authenticate with.
   *                 For example: "user" is correct whereas "DOMAIN\\user" is not.
   * @param password The password.
   * @param host     The host the authentication request is originating from.  Essentially, the
   *                 computer name for this machine.
   * @param domain   The domain to authenticate within.
   */
  public NTCredentials(String userName, String password, String host, String domain) {
    super(userName, password);
    if (domain == null) {
      throw new IllegalArgumentException("Domain is null");
    }
    this.domain = domain;
    if (host == null) {
      throw new IllegalArgumentException("Host is null");
    }
    this.host = host;
  }

  /**
   * Retrieves the name to authenticate with.
   *
   * @return String the domain these credentials are intended to authenticate with.
   */
  public String getDomain() {
    return domain;
  }

  /**
   * Retrieves the host name of the computer originating the request.
   *
   * @return String the host the user is logged into.
   */
  public String getHost() {
    return this.host;
  }

  /**
   * Return a string representation of this object.
   *
   * @return A string represenation of this object.
   */
  public String toString() {
    final StringBuilder sbResult = new StringBuilder(super.toString());
    sbResult.append("@");
    sbResult.append(this.host);
    sbResult.append(".");
    sbResult.append(this.domain);
    return sbResult.toString();
  }

  /**
   * Computes a hash code based on all the case-sensitive parts of the credentials object.
   *
   * @return The hash code for the credentials.
   */
  public int hashCode() {
    int hash = super.hashCode();
    if (this.host != null) {
      hash += this.host.hashCode();
    }
    if (this.domain != null) {
      hash += this.domain.hashCode();
    }
    return hash;
  }

  /**
   * Performs a case-sensitive check to see if the components of the credentials
   * are the same.
   *
   * @param o The object to match.
   * @return <code>true</code> if all of the credentials match.
   */
  public boolean equals(Object o) {
    if (o == null) return false;
    if (this == o) return true;
    if (super.equals(o)) {
      if (o instanceof NTCredentials) {
        NTCredentials that = (NTCredentials) o;
        return two_equals(this.domain, that.domain) && two_equals(this.host, that.host);
      }
    }
    return false;
  }

  private boolean two_equals(final Object obj1, final Object obj2) {
    return obj1 == null ? obj2 == null : obj1.equals(obj2);
  }

}
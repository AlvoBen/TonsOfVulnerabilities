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
 * <p>Username and password {@link com.sap.httpclient.auth.Credentials}.</p>
 *
 * @author Nikolai Neichev
 */

public class UserPassCredentials implements Credentials {

  /**
   * User name.
   */
  private String userName;

  /**
   * Password.
   */
  private String password;

  /**
   * The constructor with the username and password combined string argument.
   *
   * @param user_pass the username:password formed string
   */
  public UserPassCredentials(String user_pass) {
    super();
    if (user_pass == null) {
      throw new IllegalArgumentException("Username:password string is null");
    }
    int colIndex = user_pass.indexOf(':');
    if (colIndex >= 0) {
      this.userName = user_pass.substring(0, colIndex);
      this.password = user_pass.substring(colIndex + 1);
    } else {
      this.userName = user_pass;
    }
  }

  /**
   * The constructor with the username and password arguments.
   *
   * @param userName the user name
   * @param password the password
   */
  public UserPassCredentials(String userName, String password) {
    super();
    if (userName == null) {
      throw new IllegalArgumentException("Username is null");
    }
    this.userName = userName;
    this.password = password;
  }

  /**
   * User name property getter.
   *
   * @return the userName
   */
  public String getUserName() {
    return userName;
  }

  /**
   * Password property getter.
   *
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Get this object string.
   *
   * @return the username:password formed string
   */
  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append(this.userName);
    result.append(":");
    result.append((this.password == null) ? "null" : this.password);
    return result.toString();
  }

  /**
   * Does a hash of both user name and password.
   *
   * @return The hash code including user name and password.
   */
  public int hashCode() {
    int hash = 0;
    if (this.userName != null) {
      hash += this.userName.hashCode();
    }
    if (this.password != null) {
      hash += this.password.hashCode();
    }
    return hash;
  }

  /**
   * These credentials are assumed equal if the username and password are the
   * same.
   *
   * @param o The other object to compare with.
   * @return <code>true</code> if the object is equivalent.
   */
  public boolean equals(Object o) {
    if (o == null) return false;
    if (this == o) return true;
    // note - to allow for sub-classing, this checks that class is the same
    // rather than do "instanceof".
    if (this.getClass().equals(o.getClass())) {
      UserPassCredentials that = (UserPassCredentials) o;
      if (two_equals(this.userName, that.userName) && two_equals(this.password, that.password)) {
        return true;
      }
    }
    return false;
  }

  private boolean two_equals(final Object obj1, final Object obj2) {
    return obj1 == null ? obj2 == null : obj1.equals(obj2);
  }

}
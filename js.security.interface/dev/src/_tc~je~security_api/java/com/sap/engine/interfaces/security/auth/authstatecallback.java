/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.interfaces.security.auth;

import javax.security.auth.callback.Callback;

/**
 * Callback that gives information for the status of the authentication.
 * 
 * @author Krasimira Velikova
 * @version 7.1
 */
public class AuthStateCallback implements Callback {//$JL-EQUALS$
  private byte state;
  
  /**
   * Callback that is used when the authentication has failed.
   */
  public static AuthStateCallback FAILED;
  /**
   * Callback that is used when the authentication has passed.
   */
  public static AuthStateCallback PASSED;
  /**
   * Callback that is used when the password change has failed. 
   */
  public static AuthStateCallback PASSWORD_CHANGE_FAILED;
  
  private boolean showErrorOnLogonPage = false;
  
  static {
    PASSED = new AuthStateCallback((byte) 0);
    FAILED = new AuthStateCallback((byte) 1);
    PASSWORD_CHANGE_FAILED =  new AuthStateCallback((byte) 10);
  }
  
  private AuthStateCallback(byte state) {
    this.state = state;
  }
  
  public boolean getShowErrorOnLogonPage() {
    return showErrorOnLogonPage;
  }
  
  public void setShowErrorOnLogonPage(boolean show) {
    showErrorOnLogonPage = show;
  }
    
  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object obj) {
    if (!(obj instanceof AuthStateCallback)) {
      return false;
    }
    
    return ((AuthStateCallback) obj).state == state;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString() {
    StringBuffer sb = new StringBuffer(AuthStateCallback.class.getName());
    sb.append(':').append('[');
    
    if (this == FAILED) {
      sb.append("FAILED");
    } else if (this == PASSED) {
      sb.append("PASSED");
    } else if (this == PASSWORD_CHANGE_FAILED) {
      sb.append("PASSWORD_CHANGE_FAILED");
    } else {
      sb.append("UNKNOWN");
    }
    
    sb.append(']');
    return sb.toString();
  }
}

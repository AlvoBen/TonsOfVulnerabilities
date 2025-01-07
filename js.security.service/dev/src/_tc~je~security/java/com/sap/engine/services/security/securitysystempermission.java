/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security;

import java.security.*;

/**
 *  SecuritySystemPermission is granted only within the server.
 *
 * @deprecated This class is kept only for cases of upgrade from previous versions, where the
 *             name of the class may be stored in the configuration manager
 * @author  Stephan Zlaterev
 * @version 4.0.3
 */
public class SecuritySystemPermission extends Permission {

  /**
   * Creates SecuritySystemPermission object which name is the empty string.
   * The name is not used, but is required by the constructor of Permission class
   */
  public SecuritySystemPermission() {
    super("");
  }

  /**
   * SecuritySystemPermission objects does not provide any actions
   *
   * @throw  IllegalStateException when trying to use this method
   */
  public String getActions() {
    return "";
  }

  public boolean equals(Object permission) {
    return (permission instanceof SecuritySystemPermission);
  }

  /**
   * SecuritySystemPermission objects does not have hash code
   *
   * @throw  IllegalStateException when trying to use this method
   */
  public int hashCode() {
    //$JL-EQUALS$ 
    // this class is not used  
    return 0;
  }

  /**
   * SecuritySystemPermission objects are not implied
   *
   * @throw  IllegalStateException when trying to use this method
   */
  public boolean implies(Permission permission) {
    throw new IllegalStateException();
  }

  /**
   * Returns null.
   *
   * @return  null.
   */
  public PermissionCollection newPermissionCollection() {
    return null;
  }

  public String toString() {
    return "Security core permission";
  }

}


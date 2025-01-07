/**
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.security.api.logon;

import java.security.Principal;
import java.io.Serializable;

/**
 * This principal should be used when the authentication is done from the portal.
 *
 * @author Svetlana Stancheva
 * @author d028305
 * @version 6.40
 */
public class AuthSchemePrincipal implements Principal, Serializable {
  private String authschemeName;

  public AuthSchemePrincipal(String name) {
    if (name == null) {
      throw new NullPointerException ("Name must not be null.");
    }

    authschemeName = name;
  }

  /**
   * (non-Javadoc)
   * @see java.security.Principal#getName()
   */
  public String getName() {
    return authschemeName;
  }

}

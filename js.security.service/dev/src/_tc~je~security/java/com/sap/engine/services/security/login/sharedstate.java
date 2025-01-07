/**
 * Copyright (c) 2008 by SAP Labs Bulgaria,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * 
 * Created on Oct 25, 2008 by I027020
 *   
 */
package com.sap.engine.services.security.login;

import java.util.HashMap;

import com.sap.engine.interfaces.security.auth.AbstractLoginModule;
import com.sap.engine.services.security.exceptions.UnsupportedPrincipalChangeException;

/**
 * The SharedState class wraps a HashMap object and keeps the shared data passed to the login modules.
 * The put method is overridden to check if a login module is trying to change the authenticated principal.
 * The login module index for the login module that has performed the authentication is kept in a local field.
 * 
 * @author Ralin Chimev 
 * 
 */
public class SharedState<K,V> extends HashMap<K,V>   {

  private static final long serialVersionUID = 924977909915422777L;
  
  private int successfulLoginModuleIndex = -1;

  public V put(K key, V value) {
    if (AbstractLoginModule.NAME.equals(key)) {
      V loginName = get(key);
      if ((loginName != null) && !loginName.equals(value)) {
        throw new UnsupportedPrincipalChangeException("The login name in shared state cannot be changed during the authentication process.");
      }
    }
    
    if (AbstractLoginModule.PRINCIPAL.equals(key)) {
      V loginPrincipal = get(key);
      if ((loginPrincipal != null) && !loginPrincipal.equals(value)) {
        throw new UnsupportedPrincipalChangeException("The login principal in shared state cannot be changed during the authentication process.");
      }
    }
    
    return super.put(key, value);
  }
  
  public int getSuccessfulLoginModuleIndex() {
    return successfulLoginModuleIndex;
  }

  void setSuccessfulLoginModuleIndex(int successfulLoginModuleIndex) {
    this.successfulLoginModuleIndex = successfulLoginModuleIndex;
  }
}

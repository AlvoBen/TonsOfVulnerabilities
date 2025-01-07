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
package com.sap.engine.interfaces.security.userstore.config;

import javax.security.auth.login.AppConfigurationEntry;

/**                                                  
 *   Configuration of the authentication  for the activation of a 
 * single userstore.
 *
 * @author Jako Blagoev
 * @author Stephan Zlatarev
 * @author Svetlana Stancheva
 * @author Ekaterina Jeleva
 */
public interface AuthenticationDescriptor extends java.io.Serializable {
  
  public AppConfigurationEntry[] listAppConfigurationEntries() throws SecurityException;

  public String getTemplate();

}
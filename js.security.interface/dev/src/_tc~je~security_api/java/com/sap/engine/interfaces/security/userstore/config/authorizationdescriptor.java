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

import com.sap.engine.interfaces.security.SecurityRoleContext;

/**
 *   Configuration of the default security roles for the activation of a 
 * single userstore.
 *
 * @author Jako Blagoev
 * @author Stephan Zlatarev
 * @author Svetlana Stancheva
 * @author Ekaterina Jeleva
 */
public interface AuthorizationDescriptor extends java.io.Serializable {
  public static final String ROLE_ADMINISTRATORS = SecurityRoleContext.ROLE_ADMINISTRATORS;
  public static final String ROLE_ALL            = SecurityRoleContext.ROLE_ALL;
  public static final String ROLE_GUESTS         = SecurityRoleContext.ROLE_GUESTS;
  
  public String[] listSecurityRoles() throws SecurityException;
  
  public String[] listUsersInRole(String role) throws SecurityException;
  
  public String[] listGroupsInRole(String role) throws SecurityException;  
}
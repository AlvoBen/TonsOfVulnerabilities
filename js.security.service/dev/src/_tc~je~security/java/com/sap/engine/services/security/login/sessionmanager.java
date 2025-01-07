/**
 * Copyright (c) 2007 by SAP Labs Bulgaria,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 *
 */

package com.sap.engine.services.security.login;

import java.security.Principal;
import java.util.Set;

import com.sap.engine.session.usr.ClientContext;
import com.sap.engine.session.usr.UserContextException;

/**
 * SessionManager is a wrapper class of the {@link LoginAccessor). 
 * It allows certain functionality of {@link LoginAccessor) to be publicly 
 * accessible. It provides access to the client context alias, 
 * attributes and principals. The class is a singleton.  
 * 
 * @author Ralin Chimev
 */

public class SessionManager {
  
  private static SessionManager manager = new SessionManager();
  
  private SessionManager() {
    
  }
  
  /** 
   * Gets the single instance of the SessionManager class. As the class 
   * implements the singleton pattern it has only one instance and cannot 
   * be instantiated.   
   *
   * @return          <code>SessionManager</code> the one and only instance of the class  
   */
  public static SessionManager getInstance() {
    return manager;
  }

  /** 
   * Gets the current client context alias.   
   *
   * @return          <code>String</code> the client context alias.
   */
  public String getSessionAlias() {
    return SecurityContext.getLoginAccessor().getAlias();
  }
  
  /** 
   * Sets the current client context alias.   
   *
   * @param alias     The client context alias.
   */
  public void setSessionAlias(String alias) {
   SecurityContext.getLoginAccessor().setAliasToClientContext(alias); 
  }
  
  /** 
   * Logout the client context by given alias. The client context is destroyed once the 
   * last application session is terminated. Logging out the client context makes impossible for 
   * new application threads to be associated with it.    
   *
   * @param alias     The client context alias.
   */
  public void logoutSession(String alias) throws Exception {
    ClientContext clientContext = SecurityContext.getLoginAccessor().getClientContextByAlias(alias);
    if (clientContext != null) {
      clientContext.logout();
    } else {
      throw new Exception("Client context not found by alias [" + alias + "]");
    }
  }

  /** 
   * Gets current client context attribute. 
   *
   * @param attributeName   The name of client context attribute.     
   * 
   * @return                The attribute value.
   */
  public Object getAttribute(String attributeName) throws Exception {
    return SecurityContext.getLoginAccessor().getCurrentClientAttribute(attributeName);
  }

  /** 
   * Sets current client context attribute. 
   *
   * @param attributeName   The name of client context attribute.     
   * @param attributeValue  The value of client context attribute.
   */
  public void setAttribute(String attributeName, Object attributeValue) throws Exception {
    SecurityContext.getLoginAccessor().setCurrentClientAttribute(attributeName, attributeValue);
  }

  /** 
   * Gets the first principal from the login session subject matching the class name parameter.  
   *
   * @param principalClass    The class of the principal. It must extend java.security.Principal.     
   * 
   * @return                  The principal from the subject.
   */
  public Principal getPrincipal(Class<? extends Principal> principalClass) throws UserContextException {
    Set principals = SecurityContext.getLoginAccessor().getClientLoginSession().getSubject().getPrincipals(principalClass);
    if (principals.isEmpty()) {
      return null;
    } else {
      return (Principal) principals.iterator().next();
    }
  }
}

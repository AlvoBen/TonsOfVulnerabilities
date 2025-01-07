/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.interfaces.security;

import java.io.Serializable;
import java.security.Principal;
import javax.security.auth.Subject;
import java.util.HashSet;

/**
 *  Security session contains information about the caller user and information about the
 * environment where the user authentication is valid.
 *
 * @author  Stephan Zlatarev
 * @version 6.30
 */
public interface SecuritySession extends Serializable {


  /**
   *  Returns the name of the authentication configuration that was used to authenticated the user
   * or <code>null</code> if no user has authenticated.
   *
   * @return the name of a policy configuration or null
   */
  public String getAuthenticationConfiguration();


  /**
   *  Returns the timestamp of security session creation or -1.
   *
   * @return  the creation time timestamp.
   */
  public long getCreationTime();

  public long getSessionNumber();

  /**
   *  Returns the expiration period of security session creation or -1.
   *
   * @return  the expiration period of the session.
   */
  public long getExpirationPeriod();


  /**
   *  Returns the timestamp of last access of the session creation or -1.
   *
   * @return  the timestamp of last access of the session.
   */
  public long getLastAccessed();


  /**
   *  Returns the principal or null.
   *
   * @return  authenticated principal.
   */
  public Principal getPrincipal();


  /**
   *  Returns the subject or null.
   *
   * @return  authenticated subject.
   */
  public Subject getSubject();


  /**
   *  Register a listener for session closure. Sessions can be closed when either a user logs out
   * or a user stops using the session on this VM.
   *    
   * @param  listener  a session listener instance.
   * 
   * @deprecated Security session listeners are deprecated since NW04 AS Java.
   *    Use Session Management API instead.
   */
  public void registerListener(SessionListener listener);


  /**
   *  Changes the expiration period of the session.
   *
   * @param  expirationPeriod  the new value of expiration period in milliseconds.
   * 
   * @deprecated Security session listeners are deprecated since NW04 AS Java.
   *    Use Session Management API instead.
   */
  public void setExpirationPeriod(long expirationPeriod);


  /**
   *  Unregister a listener for session closure. Sessions can be closed when either a user logs out
   * or a user stops using the session on a VM.
   *
   * @param  listener  a session listener instance.
   *
   * @deprecated Security session listeners are deprecated since NW04 AS Java.
   *    Use Session Management API instead.
   */
  public void unregisterListener(SessionListener listener);

  /**
   * 
   */
  public void setPriority(int priority);
  
  /**
   * 
   */
  public int getPriority();
  
  /**
   * Associates a new security policy domain to the session.
   *
   * @param  policyDomain  a security policy domain. 
   */
  public void addSecurityPolicyDomain(String policyDomain);
  
  /**
   * Returns the security policy domains associated with the session.
   *
   * @return  the security policy domains in HashSet
   */
  public HashSet getSecurityPolicyDomains();
  
  /**
   * Allows checking if the current user is anonymous
   * 
   * @return true if anonymous, otherwise - false
   */
  public boolean isAnonymous();

}


/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.interfaces.security;

import com.sap.engine.interfaces.security.auth.LoginContextReference;

import javax.security.auth.login.LoginContext;
import javax.security.auth.Subject;

/**
 *  Security context attached to the thread with identifier "security".
 *  It is accessible as ContextObject in the ThreadContext object under "security".
 *
 * @author  Stephan Zlatarev
 * @version 6.30
 *
 * @see com.sap.engine.frame.core.thread.ContextObject
 * @see com.sap.engine.frame.core.thread.ThreadContext
 */
public interface SecurityContextObject {

  /* When listener is registered, an event is sent back to it containing the current session. */
  public final static int LISTENER_ADDED = 0;
  /* This event is sent when the session is removed from the thread. */
  public final static int SESSION_REMOVED = 1;
  /* This event is sent when the session is loaded from a p4 request. */
  public final static int SESSION_LOADED = 2;
  /* This event is sent when a new session is set in the thread. */
  public final static int SESSION_CHANGED = 3;


  /**
   *  Name identifier in ThreadContext.
   *
   *  Value is "security".
   */
  public final static String NAME = "security";


  /**
   *  Give login context to process delayed authentication for
   * lazy authentication. After this call method isAuthenticationPending will
   * return true.
   *
   * @param  loginContext  instance of LoginContext configured with a callback handler.
   */
  public void delayAuthentication(LoginContext loginContext);


  /**
   *  Give creator instance for a login context to process delayed authentication for
   * lazy authentication. After this call method isAuthenticationPending will
   * return true.
   *
   * @param  loginContextReference  instance of LoginContext configured with a callback handler.
   */
  public void delayAuthentication(LoginContextReference loginContextReference);


  /**
   *  Returns the thread's associated session.
   *
   * @return  an instance of SecuritySession or null.
   */
  public SecuritySession getSession();


  /**
   *  Tests if there is pending authentication. Pending authentication
   * is used with lazy authentication when the user needs not be authenticated
   * before authorization request.
   *  If there is pending authentication the getPrincipal() and
   * getSubject() methods will force the authentication and will
   * return the authenticated user.
   *
   * @return true  if the authentication will be initiated before returning
   *   a principal or subject.
   */
  public boolean isAuthenticationPending();


  /**
   *  Associates a session to the thread.
   *
   * @param  session  an instance of SecuritySession or null.
   */
  public void setSession(SecuritySession session);


  /**
   *  Defines a <b>copy</b> of the current security context object as a default context object
   * All threads with parent that contain no security context object will use this one.
   */
  public void useAsDefault();

  /**
   *  Adds a listener for security context object notifications.
   *
   * @deprecated Security session listeners are deprecated since NW04 AS Java.
   *    Use Session Management API instead.
   * 
   * @param  listener  an instance of the listener
   * @param  removeOnEmpty  specifys whether the listener should be unregistered
   *                        when the current thread is returned to the pool.
   */
  public void addListener(SecurityContextObjectListener listener, boolean removeOnEmpty);

  /**
   *  Removes the given listener for from the list of listeners.
   *
   * @deprecated Security session listeners are deprecated since NW04 AS Java.
   *    Use Session Management API instead.
   *
   * @param  listener  an instance of the listener
   */
  public void removeListener(SecurityContextObjectListener listener);


  /**
   *  Runs the current thread on the behalf of the subject.
   *
   * @param  subject the run as subject
   */
  public void runAs(Subject subject);
  
  /**
   *  Sets security policy domain in the current thread.
   *  
   *  @deprecated Should be used only by WebContainer until new policy domain handling is provided by the user context.
   *    After that this method will be removed. 
   *
   * @param  policyDomain the application security policy domain
   */
  public void setSecurityPolicyDomain(String policyDomain);
}


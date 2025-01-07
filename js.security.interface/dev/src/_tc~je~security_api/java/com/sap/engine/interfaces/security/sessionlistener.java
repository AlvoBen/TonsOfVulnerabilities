/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.interfaces.security;

/**
 *  Listener on closure of a security session
 *
 * @author Stephan Zlatarev
 * @version 6.30
 *
 * @see com.sap.engine.interfaces.security.SecuritySession
 *
 * @deprecated Security session listeners are deprecated since NW04 AS Java.
 *    Use Session Management API instead.
 */
public interface SessionListener {


  /**
   *  Invoked when session is not used on this VM any more.
   */
  public void onSessionLocallyRemoved();


  /**
   *  Invoked on logged out of user.
   */
  public void onUserLogOut();

}
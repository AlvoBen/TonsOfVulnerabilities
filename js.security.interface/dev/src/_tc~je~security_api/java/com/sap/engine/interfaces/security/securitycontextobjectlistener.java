/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.interfaces.security;

/**
 * 
 * @version 6.30
 * @author  Stephan Zlatarev
 */
public interface SecurityContextObjectListener {

  /**
   *  Notification that the security session of the given security
   * context object has been changed.
   *
   * @param  context  the subject security contextobject
   * @param  session  the new security session
   * @param  isAnonymous  whether the session is anonymous or not
   * @param  change  shows the type of the change for the security session.
   */
  public void sessionChanged(SecurityContextObject context,
                             SecuritySession session,
                             boolean isAnonymous, int change);

}

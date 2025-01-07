/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.jndi;

import com.sap.engine.services.jndi.implserver.ServerContextInface;

/**
 * Remote interface used to obtain naming trough client
 *
 * @author Panayot Dobrikov
 * @version 4.00
 */
public interface JNDIProxy extends java.rmi.Remote {

  /**
   * Return new server context implementation for client context requested
   *
   * @return ServerContextInface
   */
  public ServerContextInface getNewServerContext();


  /**
   * Returns new servir context implementaiton
   *
   * @param   remote flags if there is no need of real proxy
   * @return ServerContextInface
   */
  public ServerContextInface getNewServerContext(boolean remote, boolean domain);
  

  /**
   * Returns new servir context implementaiton
   *
   * @param   remote flags if there is no need of real proxy
   * @return ServerContextInface
   */
  public ServerContextInface getNewServerContext(boolean remote);


  public ServerContextInface getNewServerContext(boolean remote, String user, String pass, boolean beaLoggedIn);

}


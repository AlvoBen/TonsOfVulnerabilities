/*
 * Copyright (c) 2000-2008 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver.interfaces;

import com.sap.engine.services.httpserver.lib.util.MessageBytes;

public interface HttpHandler {
  public static final byte NOOP = 0;
  public static final byte RESPONSE_DONE = 2;
  public static final byte START_SERVLET = 4;
  public static final byte ERROR = 8;

  /**
   * Starts a servlet with name servletName. Parses a JSP if the request is for jsp, load an instance of the main servlet
   * class if the class is haven't yet loaded, invoke init method of the servlet (if not invoked before in the life of
   * the servlet) and then invoke the main service method.
   *
   * @param   servletName  name of the servlet that will be loaded
   * @param   httpParameters  http parameters
   */
  public void handleRequest(String servletName, HttpParameters httpParameters);

  /**
   * Checks whether a custom error handler exists and if so delegates the error in order to be processed.
   *
   * @param httpParameters http parameters
   * @return true if there is a custom error handler, false - otherwise.
   */
  public boolean handleError(HttpParameters httpParameters);

  public void endRequest(HttpParameters httpParameters);

  /**
   * Returns the MIME time corresponding to the file extension ext.
   *
   * @param   ext  file extension
   * @param   alias  http alias
   */
  public String checkMIME(char[] ext, MessageBytes alias);

  /**
   * Returns the value of the servlet_jsp property SecuritySessionIdDomain.
   * Returns null, if the service context is not active and the property cannot be obtained.
   * @return
   */
  public String getSecurtiySessionIdDomain();
  
  public void connectionClosed(int clientId);

  public void removeSession(String cookie);

  public String getApplicationAlias(HttpParameters httpParameters);
}

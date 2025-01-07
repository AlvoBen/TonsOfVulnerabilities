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
package com.sap.engine.services.httpserver.server;

/*
 * Some static constants that are used when processing the request.
 * Contains both HTTP protocol specific headers and HTTP service additional constants.
 *
 * @author Maria Jurova
 * @version 4.0
 */
public interface Constants {
  public static final byte[] jspExtension = ".jsp".getBytes();
  public static final byte[] defaultBytes = "default".getBytes();
  public static final byte[] allowed1 = ("GET, POST, HEAD").getBytes();
  public static final byte[] HTTP_11 = "HTTP/1.1".getBytes();
  
  // for logging
  public static final String LOCATION_RESPONSES = "com.sap.engine.services.httpserver.server.Log";
  public static final String LOCATION_RESPONSES_CLF = "com.sap.engine.services.httpserver.server.CommonLogFormat";
  public static final String LOCATION_RESPONSES_SMD = "com.sap.engine.services.httpserver.server.Smd";

  // name of configuration for put files
  public static final String HTTP_ALIASES = "HttpAliases";
  
  public static final String SMDHeader = "X-CorrelationID"; 

  /**
   * CR.
   */
  public static final byte CR = (byte) '\r';

  /**
   * LF.
   */
  public static final byte LF = (byte) '\n';
  
  public static final String LOG_VIEWER_ALIAS_NAME = "webdynpro/resources/sap.com/tc~lm~itsam~ui~lv~client_ui";
  
}


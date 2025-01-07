/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver.interfaces.client;

import com.sap.engine.services.httpserver.lib.headers.MimeHeaders;
import com.sap.engine.services.httpserver.lib.HttpCookie;
import com.sap.engine.lib.util.ArrayObject;

import javax.servlet.ServletInputStream;

public interface Request {
  public SslAttributes getSslAttributes();

  public RequestLine getRequestLine();

  public MimeHeaders getHeaders();

  public ServletInputStream getBody();

  public byte[] getClientIP();

  public int getClientId();

  public String getHost();

  public int getPort();
  
  public int getRemotePort();

  public HttpCookie getSessionCookie(boolean urlSessionTracking);

  public ArrayObject getApplicationCookies(boolean urlSessionTracking);

  public ArrayObject getCookies(boolean urlSessionTracking);

  public boolean isGzipEncoding();

  public String getScheme();

  public int getDispatcherId();
  
  public boolean isSessionSizeEnabled();
  
  public void setSessionSizeEnabled(boolean isSessionSizeCalculationEabled);
}

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
package com.sap.engine.services.httpserver.interfaces;

import java.util.Hashtable;

import com.sap.engine.services.httpserver.interfaces.client.Request;
import com.sap.engine.services.httpserver.interfaces.client.Response;
import com.sap.engine.services.httpserver.lib.util.MessageBytes;
import com.sap.engine.session.runtime.http.HttpSessionRequest;
import com.sap.engine.lib.util.HashMapObjectLong;

/**
 * 
 * @author Maria Jurova
 * @version 4.0
 */
public interface HttpParameters extends Cloneable {
  public Request getRequest();

  public Response getResponse();

  public String getHostName();

  public MessageBytes getRequestParametersBody();

  public RequestPathMappings getRequestPathMappings();

  public boolean isProtected();

  public void setProtected(boolean isProtected);

  public void setApplicationSession(Object applicationSession);

  public Object getApplicationSession();

  public HttpSessionRequest getSessionRequest();

  public void redirect(byte[] location);

  public void setResponseLength(int length);

  public boolean isSetApplicationCookie();

  public boolean isSetSessionCookie();

  public void setApplicationCookie(boolean isSet);

  public void setSessionCookie(boolean isSet);

  public void setErrorData(ErrorData errorData);

  public ErrorData getErrorData();

  public void setRequestAttribute(String name, Object value);

  public Hashtable getRequestAttributes();

  public Object clone();

  public void setDebugRequest(boolean isDebug);

  public boolean isDebugRequest();

  public MessageBytes replaceAliases(MessageBytes fname);

  /**
   * Returns an absolute URI to the server root for the given scheme
   * 
   * @param scheme
   * The required scheme
   * 
   * @return
   * An absolute URI to the server root
   */
  public String getServerURL(String scheme);

  public HashMapObjectLong getTimeStatisticsMap();

  public int getTraceResponseTimeAbove();
  
  /**
   * Returns true if memory allocations for the requests are enabled
   * @return
   */
  public boolean isMemoryTrace();
  
  public void setMemoryTrace(boolean isMemoryTrace);
  
  
  public void preserveWithoutFinalizer();
  public void preserveWithFinalizer();
  public boolean isPreserved();
  public void recycleNew();
  public void recycleReturn();
  public void justNew();
  
  
}


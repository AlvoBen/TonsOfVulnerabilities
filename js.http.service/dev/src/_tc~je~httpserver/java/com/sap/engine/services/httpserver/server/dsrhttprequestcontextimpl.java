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
package com.sap.engine.services.httpserver.server;

import com.sap.engine.services.httpserver.DSRHttpRequestContext;
import com.sap.engine.services.httpserver.interfaces.client.Request;

import java.util.Hashtable;
import java.util.Enumeration;

public class DSRHttpRequestContextImpl implements DSRHttpRequestContext {
  private Request request = null;

  public void init(Request request) {
    this.request = request;
  }

  public String getHost() {
    return request.getHost();
  }

  public String getScheme() {
    return request.getScheme();
  }

  public int getPort() {
    return request.getPort();
  }

  public String getURL() {
    return request.getRequestLine().getFullUrl().toString();
  }

  public Hashtable getHeaders() {
    Hashtable result = new Hashtable();
    Enumeration en = request.getHeaders().names();
    while (en.hasMoreElements()) {
      String name = (String)en.nextElement();
      result.put(name, request.getHeaders().getHeaders(name));
    }
    return result;
  }

  public String getHeader(String name) {
    return request.getHeaders().getHeader(name);
  }
}

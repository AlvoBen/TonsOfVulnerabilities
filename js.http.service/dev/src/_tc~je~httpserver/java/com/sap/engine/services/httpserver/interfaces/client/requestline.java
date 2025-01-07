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

import com.sap.engine.services.httpserver.lib.util.MessageBytes;
import com.sap.engine.services.httpserver.interfaces.exceptions.ParseException;

public interface RequestLine {

  public static final String scheme_http = "http://";
  public static final String scheme_https = "https://";
  public static final String http = "http";
  public static final String https = "https";

  public static final byte[] scheme_http_ = scheme_http.getBytes();
  public static final byte[] scheme_https_ = scheme_https.getBytes();
  public static final byte[] http_ = http.getBytes();
  public static final byte[] https_ = https.getBytes();

  public MessageBytes getRequestLine();

  /**
   * Returns a type of the http reques, i.e. POST, GET.
   *
   * @return
   */
  public byte[] getMethod();

  public String getScheme();

  public boolean isSecure();

  public byte[] getHost();

  public int getPort();

  public int getHttpMinorVersion();

  public int getHttpMajorVersion();

  public MessageBytes getFullUrl();

  public MessageBytes getUrlNotDecoded() throws ParseException;

  public MessageBytes getUrlDecoded();

  public MessageBytes getQuery();

  public boolean isEncoded();

  public boolean isSimpleRequest();

  public byte[] toByteArray();
}

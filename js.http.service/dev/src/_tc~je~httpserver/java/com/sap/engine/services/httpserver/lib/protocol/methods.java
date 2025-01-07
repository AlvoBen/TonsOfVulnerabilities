package com.sap.engine.services.httpserver.lib.protocol;

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

/*
 * HTTP 1.1 methods
 */
public interface Methods {
  //Strings
  public static final String PUT = "PUT";
  public static final String POST = "POST";
  public static final String OPTIONS = "OPTIONS";
  public static final String GET = "GET";
  public static final String HEAD = "HEAD";
  public static final String DELETE = "DELETE";
  public static final String TRACE = "TRACE";
  //byte arrays
  public static final byte[] _PUT = PUT.getBytes();
  public static final byte[] _POST = POST.getBytes();
  public static final byte[] _OPTIONS = OPTIONS.getBytes();
  public static final byte[] _GET = GET.getBytes();
  public static final byte[] _HEAD = HEAD.getBytes();
  public static final byte[] _DELETE = DELETE.getBytes();
  public static final byte[] _TRACE = TRACE.getBytes();
}

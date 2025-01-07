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

public interface Headers {
  public Header[] getHeaders();
  public Header getHeader(byte[] name);
  public Header getHeader(String name);
  public Header getHeaders(byte[] name);
  public Header getHeaders(String name);

  public byte[] getHeaderValue(byte[] name);
  public String getHeaderValue(String name);
  public byte[][] getHeaderValues(byte[] name);
  public String getHeaderValues(String name);
}

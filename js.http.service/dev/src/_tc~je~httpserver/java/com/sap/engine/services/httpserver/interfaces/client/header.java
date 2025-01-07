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

public interface Header {
  public byte[] getName();
  public byte[] getValue();
  public String getNameString();
  public String getValueString();

  public boolean equals(Header header);

  public boolean equalsName(byte[] name);
  public boolean equalsValue(byte[] value);
  public boolean equalsName(String name);
  public boolean equalsValue(String value);
}

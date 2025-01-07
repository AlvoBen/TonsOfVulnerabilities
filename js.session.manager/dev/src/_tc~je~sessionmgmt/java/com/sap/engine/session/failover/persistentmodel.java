/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session.failover;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Author: georgi-s
 * Date: 2005-4-26
 */
public interface PersistentModel {

  int storageType();

  String sessionId();

  boolean available();

  long expTime();

  void updateExpTime(long expPeriod);

  OutputStream getOutputStrem();

  InputStream getInputStream();

  OutputStream getOutputStrem(String unit);

  InputStream getInputStream(String unit);

  void writeData(String unit, byte[] data);

  void readData(String unit);

  void remove();

  boolean tryLock();

}
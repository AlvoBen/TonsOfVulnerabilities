/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidential and proprietary information
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.internal.giop;


import com.sap.engine.services.iiop.logging.LoggerConfigurator;

/**
 * This class represents incomming reply message object version 1.2
 *
 * @author Georgy Stanev, Nikolai Neichev
 * @version 4.0
 */
public class IncomingReply_1_2 extends IncomingReply {

  // GIOP REquest Message Header information
  public IncomingReply_1_2(byte[] binaryData) {
    super(binaryData);
    set_minor_version(2);
  }

  public IncomingReply_1_2(byte[] binaryData, int size) {
    super(binaryData, size);
    set_minor_version(2);
  }

  /**
   * Reads the GIOP header.
   *
   */
  protected void readGIOPHeader() {
    versionMajor = data[4];
    versionMinor = data[5];
    int order = (data[6] & 0x01);

    if (order == 0) {
      littleEndian = false;
    } else {
      littleEndian = true;
    }

    int fragment = (data[6] & 0x02);

    if (fragment == 0) {
      isFragmented = false;
    } else {
      isFragmented = true;
    }

    reset(12);
  }

  /**
   * Reads the message header.
   *
   */
  protected void readMessageHeader() {
    request_id = read_long();
    status = unaligned_read_long();
    replyContexts = readServiceContexts();
    try {
      align(8);
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("IncommingReply_1_2.readMessageHeader()", LoggerConfigurator.exceptionTrace(e));
      }
    }
  }

}


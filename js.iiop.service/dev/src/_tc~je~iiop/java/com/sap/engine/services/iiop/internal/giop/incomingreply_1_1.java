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

/**
 * This class represents incomming reply message object version 1.1
 *
 * @author Georgy Stanev, NWN
 * @version 4.0
 */
public class IncomingReply_1_1 extends IncomingReply {

  protected IncomingReply_1_1(byte[] binaryData) {
    super(binaryData);
  }

  /**
   * Reads the GIOP header.
   *
   */
  protected void readGIOPHeader() {
    versionMajor = data[4];
    versionMajor = data[5];
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
    replyContexts = readServiceContexts();
    request_id = read_long();
    status = unaligned_read_long();
  }

}


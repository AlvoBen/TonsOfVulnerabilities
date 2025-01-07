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
 * This class is used for extracting the fragmented data
 * from a FRAGMENT message.
 *
 * @author Nikolai Neichev
 * @version 4.0
 */
public class CancelRequestMessage extends IncomingMessage {

  protected int request_id;

  public CancelRequestMessage(byte[] binaryData) {
    super(binaryData);
    readGIOPHeader();
  }

  public CancelRequestMessage(byte[] binaryData, int size) {
    super(binaryData, size);
    readGIOPHeader();
  }

  /**
   * Reads the GIOP header.
   *
   */
  protected void readGIOPHeader() {
    versionMajor = data[4];
    versionMinor = data[5];
    int order = (data[6] & 0x01);
    littleEndian = (order != 0);
    int fragment = (data[6] & 0x02);
    isFragmented = (fragment != 0);
    reset(12);
  }

  public void process() {
  }

  public void process_initial() {
  }

  /**
   * Reads the message header.
   *
   */
  protected void readMessageHeader() {
    request_id = read_long();
  }

  /**
   * Accessor method
   *
   * @return     The request id.
   */
  public int request_id() {
    return request_id;
  }

  /**
   * Creates empty reply message.
   *
   * @return     Outgoing mesage, which has no data.
   */
  public OutgoingMessage getServerReply() {
    return NoReplyMessage._this;
  }

}



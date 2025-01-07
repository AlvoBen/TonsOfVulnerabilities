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

import com.sap.engine.system.ThreadWrapper;

/**
 * This class is used for extracting the fragmented data
 * from a FRAGMENT message.
 *
 * @author Nikolai Neichev
 * @version 4.0
 */
public class FragmentMessage extends IncomingMessage {

  protected int request_id;

  public FragmentMessage(byte[] binaryData) {
    super(binaryData);
    readGIOPHeader();
  }

  public FragmentMessage(byte[] binaryData, int size) {
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
    littleEndian = ((data[6] & 0x01) != 0);
    isFragmented = ((data[6] & 0x02) != 0);
    reset(12);
  }

  public void process() {
    ThreadWrapper.pushSubtask("processing fragment", ThreadWrapper.TS_PROCESSING);
    try {
      ConnectionMetaData metaData = (ConnectionMetaData) connection.getMetaData();
      IncomingMessage msg = null;
      if (metaData != null) {
        msg = (IncomingMessage) metaData.storedFragments.get(request_id());
        msg.addFragment(this);
      } else {
        throw new RuntimeException("Stored fragment is missing");
      }

      if (!fragmented()) {
        metaData.storedFragments.remove(msg.request_id());
        if (getDefault_codebase() == null) {
          setDefault_codebase(default_codebase);
        }
        if (noResources) {
         msg.generateNoResoucesErrorReply();
        }
        msg.process();
        default_codebase = msg.getDefault_codebase();
      } else {
  //      metaData.storedFragments.put(msg.request_id(), msg);
        metaData.storeFragment(msg.request_id(), msg);
      }
    } finally {
      ThreadWrapper.popSubtask(); // pop the "processing fragment" sub task
    }

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


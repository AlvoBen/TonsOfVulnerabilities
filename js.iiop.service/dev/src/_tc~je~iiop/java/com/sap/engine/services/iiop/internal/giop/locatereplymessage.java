/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.internal.giop;

import com.sap.engine.system.ThreadWrapper;

/**
 * Implementation of LocateReply GIOP message. Send by server in response
 * to a client's LocateRequest GIOP message.
 *
 * @author Nikolai Neichev
 * @version 4.0
 */
public abstract class LocateReplyMessage extends IncomingMessage {

  protected int status;
  protected int request_id = -1;

  public LocateReplyMessage(byte[] binaryData) {
    super(binaryData);
  }

  public LocateReplyMessage(byte[] binaryData, int size) {
    super(binaryData, size);
  }


  public int request_id() {
    return request_id;
  }

  public void process() {
    ThreadWrapper.pushSubtask("processing locate reply", ThreadWrapper.TS_PROCESSING);    
    try {
      ConnectionMetaData metaData = (ConnectionMetaData) connection.getMetaData();
      if (fragmented()) {
        metaData.storeFragment(request_id(), this);
      }
    } finally {
      ThreadWrapper.popSubtask();
    }
  }

  public void process_initial() {

  }

  protected abstract void readGIOPHeader();

  protected abstract void readMessageHeader();

  public OutgoingMessage getServerReply() {
    return NoReplyMessage._this;
  }

}


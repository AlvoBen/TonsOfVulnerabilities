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

import com.sap.engine.services.iiop.internal.util.IDFactoryItem;
import com.sap.engine.services.iiop.internal.ClientORB;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import com.sap.engine.system.ThreadWrapper;
import org.omg.IOP.ServiceContext;

/**
 * It is the base class of all incomming reply messages.
 *
 * @author Georgy Stanev
 * @version 4.0
 */
public abstract class IncomingReply extends IncomingMessage {

  protected int request_id;
  protected int status;
  public ServiceContext[] replyContexts;
  protected org.omg.CORBA.ORB ownORB;

  protected IncomingReply(byte[] binaryData) {
    super(binaryData);
  }

  protected IncomingReply(byte[] binaryData, int size) {
    super(binaryData, size);
  }

  /**
   * Processes the current incomming message.
   *
   */
  public void process_initial() {
    readMessageHeader();
    try {
      IDFactoryItem item = ClientORB.getIDFactory().get(request_id);
      item.setMessage(this);
      synchronized (item) {
        item.notify();
      }
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("IncommingReply.process()", LoggerConfigurator.exceptionTrace(e));
      }
    }
  }

  public void process() {
    ConnectionMetaData metaData = (ConnectionMetaData) connection.getMetaData();
    if (fragmented()) {
      metaData.storeFragment(request_id(), this);
    } else {
      process_initial();
    }
  }

  /**
   * Accessor method.
   *
   * @return     The request id.
   */
  public int getRequestId() {
    return request_id;
  }

  /**
   * Accessor method.
   *
   * @return     Thfalsee status.
   */
  public int getStatus() {
    return status;
  }

  /**
   * Creates empty outgoing message.
   *
   * @return     Empty outgoing message.
   */
  public OutgoingMessage getServerReply() {
    return NoReplyMessage._this;
  }

  public int request_id() {
    return request_id;
  }
}


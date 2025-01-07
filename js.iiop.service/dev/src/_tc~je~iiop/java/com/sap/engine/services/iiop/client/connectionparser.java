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
package com.sap.engine.services.iiop.client;

import com.sap.engine.services.iiop.CORBA.GIOPMessageConstants;
import com.sap.engine.services.iiop.internal.giop.*;
import com.sap.engine.lib.util.ConcurrentHashMapIntObject;
import org.omg.SendingContext.RunTime;

/**
 * This class is used from CommunicationLayerImpl to build the
 * corresponding OutgoingMessage, which will be lately processed
 *
 * @author Georgy Stanev, Nikolai Neichev
 * @version 4.0
 */
public class ConnectionParser implements GIOPMessageConstants {

  private ConcurrentHashMapIntObject storedFragments = new ConcurrentHashMapIntObject();
  private boolean resetMe = false;

  protected RunTime default_codebase = null;
  private boolean isFirst = true;

  public ConnectionParser() {
  }

  public void reset() {
    storedFragments.clear();
    resetMe = false;
    default_codebase = null;
    isFirst = true;
  }

  public boolean toReset() {
    return resetMe;
  }


  /**
   * This method reads the data and creates a Object,
   * corresponding to the message type and version
   *
   * @param   inputData  The data bytes
   * @return     Object which represents OutgoingMessage
   * @exception   com.sap.engine.services.iiop.internal.giop.InvalidMessageException  If the message type is infalid.
   */
  public OutgoingMessage readMessage(byte[] inputData) throws InvalidMessageException {
    IncomingMessage msg = null;
    byte minorVersion = inputData[5];
    switch (inputData[7]) { // message type
      case REQUEST: { //0
        switch (minorVersion) {
          case 0: {
            msg = new IncomingRequest_1_0(inputData);
            break;
          }
          case 1: {
            msg = new IncomingRequest_1_1(inputData);
            break;
          }
          case 2: {
            msg = new IncomingRequest_1_2(inputData);
            break;
          }
        }

        if (isFirst) {
          msg.setToSendCodeBase(true);
          isFirst = false;
        }

        if (!msg.fragmented()) {
          if (msg.getDefault_codebase() == null) {
            msg.setDefault_codebase(default_codebase);
          }
          msg.process_initial();
          default_codebase = msg.getDefault_codebase();
          return msg.getServerReply();
        } else {
          storedFragments.put(msg.request_id(), msg);
        }

        break;
      }
      case REPLY: { //1
        switch (minorVersion) {
          case 0: {
            msg = new IncomingReply_1_0(inputData);
            break;
          }
          case 1: { // 1.1 same as 1.0
            msg = new IncomingReply_1_0(inputData);
            break;
          }
          case 2: {
            msg = new IncomingReply_1_2(inputData);
            break;
          }
        }

        if (!msg.fragmented()) {
          if (msg.getDefault_codebase() == null) {
            msg.setDefault_codebase(default_codebase);
          }
          msg.process_initial();
          default_codebase = msg.getDefault_codebase();
          return msg.getServerReply();
        } else {
          storedFragments.put(msg.request_id(), msg);
        }

        break;
      }
      case CANCEL_REQUEST: {
        msg = new CancelRequestMessage(inputData);
        if (storedFragments.containsKey(msg.request_id())) {
          storedFragments.remove(msg.request_id());
        }
        return msg.getServerReply();
      }
      case LOCATE_REQUEST: {
        switch (minorVersion) {
          case 0: {
            msg = new LocateRequestMessage_1_0(inputData);
            break;
          }
          case 1: { // 1.1. same as 1.0
            msg = new LocateRequestMessage_1_0(inputData);
            break;
          }
          case 2: {
            msg = new LocateRequestMessage_1_2(inputData);
            break;
          }
        }

        if (!msg.fragmented()) {
          if (msg.getDefault_codebase() == null) {
            msg.setDefault_codebase(default_codebase);
          }
          msg.process_initial();
          default_codebase = msg.getDefault_codebase();
          return msg.getServerReply();
        } else {
          storedFragments.put(msg.request_id(), msg);
        }

        break;
      }
      case LOCATE_REPLY: {
        switch (minorVersion) {
          case 0: {
            msg = new LocateReplyMessage_1_0(inputData);
            break;
          }
          case 1: { // 1.1 same as 1.0
            msg = new LocateReplyMessage_1_0(inputData);
            break;
          }
          case 2: {
            msg = new LocateReplyMessage_1_2(inputData);
            break;
          }
        }

        if (!msg.fragmented()) {
          if (msg.getDefault_codebase() == null) {
            msg.setDefault_codebase(default_codebase);
          }
          msg.process_initial();
          default_codebase = msg.getDefault_codebase();
          return msg.getServerReply();
        } else {
          storedFragments.put(msg.request_id(), msg);
        }

        break;
      }
      case CLOSE_CONNECTION: {
        resetMe = true;
        return null;
      }
      case MESSAGE_ERROR: {
        // all over the connection are not OK
        resetMe = true;
        return NoReplyMessage._this;
      }
      case FRAGMENT: {
        switch (minorVersion) {
          case 1:   // REQUEST or REPLY continues...
          case 2: { // REGUEST, REPLY, LOCATEREQUEST or LOCATEREPLY continues...
            FragmentMessage fragMsg = new FragmentMessage(inputData);
            msg = (IncomingMessage) storedFragments.get(fragMsg.request_id());
            msg.addFragment(fragMsg);
            break;
          }
        }

        if (!msg.fragmented()) {
          storedFragments.remove(msg.request_id());
          if (msg.getDefault_codebase() == null) {
            msg.setDefault_codebase(default_codebase);
          }
          msg.process_initial();
          default_codebase = msg.getDefault_codebase();
          return msg.getServerReply();
        } else {
          storedFragments.put(msg.request_id(), msg);
        }

        break;
      }
      default: {
        resetMe = true;
        throw new InvalidMessageException("ID010013: Invalid message type: " + inputData[0]);
      }
    }

    return NoReplyMessage._this;
  }

  /**
   * This method is called when connection is lost.
   *
   */
  public void connectionLost() {
    resetMe = true;
    // sended client's requests to be notified with exception !!
  }

}


package com.sap.engine.services.iiop.server;

import com.sap.engine.interfaces.cross.MessageProcessor;
import com.sap.engine.interfaces.cross.CrossMessage;
import com.sap.engine.interfaces.cross.Connection;
import com.sap.engine.interfaces.cross.FCAConnector;
import com.sap.engine.services.iiop.internal.giop.*;
import com.sap.engine.services.iiop.CORBA.GIOPMessageConstants;
import com.sap.engine.system.ThreadWrapper;

import java.io.IOException;
import java.util.Properties;

public class IIOPMessageProcessor implements MessageProcessor, GIOPMessageConstants {

  public static FCAConnector fcaConnector;
  private int threadsToStart = 10;
  private int requestQueueSize = 100;
  public IIOPMessageProcessor() {
    try {
      Properties properties = CommunicationLayerImpl.context.getServiceState().getProperties();
      if (properties != null) {
        threadsToStart = Integer.parseInt(properties.getProperty("parallelRequests", "10"));
        requestQueueSize = Integer.parseInt(properties.getProperty("requestQueueSize", "100"));
      }
    } catch (Exception e) { // $JL-EXC$
      // ok, will use dafault thread count
    }
  }

  public CrossMessage getMessage(byte[] data, int size, Connection connection) throws IOException {
    IncomingMessage msg = null;
    byte minorVersion = data[5];
    ThreadWrapper.pushSubtask("parsing message", ThreadWrapper.TS_PROCESSING);
    try {
      switch (data[7]) { // message type
        case REQUEST: { //0
          ThreadWrapper.setSubTaskName("parsing request");
          try{
          switch (minorVersion) {
            case 0: {
              msg = new IncomingRequest_1_0(data, size);
              break;
            }
            case 1: {
              msg = new IncomingRequest_1_1(data, size);
              break;
            }
            case 2: {
              msg = new IncomingRequest_1_2(data, size);
              break;
            }
          }
          msg.setConnection(connection);
          ConnectionMetaData metaData = (ConnectionMetaData) connection.getMetaData();
          if ( metaData == null) {
            metaData = new ConnectionMetaData();
            connection.setMetaData(metaData);
          }

          if (msg.fragmented()) {
            if (metaData.isFirst()) {
              msg.setToSendCodeBase(true);
              metaData.notFirst();
            }
            metaData.storeFragment(msg.request_id(), msg);
            return new EmptyProcessMessage(data);
          }
          return  new CrossMessageImpl(msg, fcaConnector);
          } finally {
            ThreadWrapper.popSubtask(); // for "parsing request"
          }         
        }
        case REPLY: { //1
          ThreadWrapper.setSubTaskName("parsing reply");
          try{
          switch (minorVersion) {
            case 0: {
              msg = new IncomingReply_1_0(data, size);
              break;
            }
            case 1: { // 1.1 same as 1.0
              msg = new IncomingReply_1_0(data, size);
              break;
            }
            case 2: {
              msg = new IncomingReply_1_2(data, size);
              break;
            }
          }
          msg.setConnection(connection);
          ConnectionMetaData metaData = (ConnectionMetaData) connection.getMetaData();
          if (metaData == null) {
            metaData = new ConnectionMetaData();
            connection.setMetaData(metaData);
          }

          if (msg.fragmented()) {
            if (metaData.isFirst()) {
              msg.setToSendCodeBase(true);
              metaData.notFirst();
            }
            metaData.storeFragment(msg.request_id(), msg);
            return new EmptyProcessMessage(data);
          }

          return msg;
          } finally {
            ThreadWrapper.popSubtask(); // for "parsing reply"
          }
        }
        case CANCEL_REQUEST: {
          ThreadWrapper.setSubTaskName("parsing cancel_request");
          try{
          msg = new CancelRequestMessage(data, size);
          msg.setConnection(connection);
          ConnectionMetaData metaData = (ConnectionMetaData) connection.getMetaData();

          if (metaData != null) {
            if ( metaData.storedFragments.containsKey( msg.request_id() ) ) {
              metaData.storedFragments.remove(msg.request_id());
            }
          }

          return msg;
          } finally {
            ThreadWrapper.popSubtask(); // for "parsing cancel_request"
          }          
        }
        case LOCATE_REQUEST: {
          ThreadWrapper.setSubTaskName("parsing locate_request");
          try {
          switch (minorVersion) {
            case 0: {
              msg = new LocateRequestMessage_1_0(data, size);
              break;
            }
            case 1: { // 1.1. same as 1.0
              msg = new LocateRequestMessage_1_0(data, size);
              break;
            }
            case 2: {
              msg = new LocateRequestMessage_1_2(data, size);
              break;
            }
          }
          msg.setConnection(connection);
          if (connection.getMetaData() == null) {
            ConnectionMetaData metaData = new ConnectionMetaData();
            connection.setMetaData(metaData);
          }

          return msg;
          } finally {
            ThreadWrapper.popSubtask(); // for "parsing locate_request"
          }
        }
        case LOCATE_REPLY: {
          ThreadWrapper.setSubTaskName("parsing locate_reply");
          try {
          switch (minorVersion) {
            case 0: {
              msg = new LocateReplyMessage_1_0(data, size);
              break;
            }
            case 1: { // 1.1 same as 1.0
              msg = new LocateReplyMessage_1_0(data, size);
              break;
            }
            case 2: {
              msg = new LocateReplyMessage_1_2(data, size);
              break;
            }
          }
          msg.setConnection(connection);
          if (connection.getMetaData() == null) {
            ConnectionMetaData metaData = new ConnectionMetaData();
            connection.setMetaData(metaData);
          }
          return msg;
          } finally {
            ThreadWrapper.popSubtask(); // for "parsing locate_reply"
          }
        }
        case CLOSE_CONNECTION: {
          connection.close();
          break;
        }
        case MESSAGE_ERROR: {
          // TODO
          break;
        }
        case FRAGMENT: {
          ThreadWrapper.setSubTaskName("parsing fragment");
          try {
          switch (minorVersion) {
            case 1: {  //$JL-SWITCH$
              // REQUEST or REPLY continues...
              // falls through
            }
            case 2: {  //$JL-SWITCH$
              // REGUEST, REPLY, LOCATEREQUEST or LOCATEREPLY ...
              msg = new FragmentMessage(data, size);
              break;
            }
          }
          msg.setConnection(connection);
          ConnectionMetaData metaData = (ConnectionMetaData) connection.getMetaData();
          IncomingMessage fragMsg = null;
          if (metaData != null) {
            fragMsg = (IncomingMessage) metaData.storedFragments.get(msg.request_id());
            fragMsg.addFragment((FragmentMessage) msg);
          } else {
            throw new RuntimeException("Stored fragment is missing; connection meta data is null");
          }

          if (!msg.fragmented()) {
            metaData.storedFragments.remove(fragMsg.request_id());
            return  new CrossMessageImpl(fragMsg, fcaConnector);
          } else {
            metaData.storeFragment(fragMsg.request_id(), fragMsg);
            return new EmptyProcessMessage(data);
          }
            } finally {
              ThreadWrapper.popSubtask(); // for "parsing fragment"
            }
        }
      }
    } finally {
      ThreadWrapper.popSubtask(); //for "parsing message"
    }
    throw new InvalidMessageException("ID010013: Invalid message type: " + data[0]);
  }

  public void clientConnectionClosed(Connection connection) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void setConnector(FCAConnector connector) {
    fcaConnector = connector;
  }

  public FCAConnector getConnector() {
    return fcaConnector;
  }

  public int getType() {
    return IIOP_PROCESSOR;
  }

  public int getNumberOfConcurrentThreads() {
    return threadsToStart;
  }

  public int getRequestQueueSize() {
    return requestQueueSize;
  }

  public void clientConnectionAccepted(Connection connection) {
    // nothing to do
  }
}

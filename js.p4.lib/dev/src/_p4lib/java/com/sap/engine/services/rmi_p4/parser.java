package com.sap.engine.services.rmi_p4;

import com.sap.engine.services.rmi_p4.exception.P4BaseParseRequestException;
import com.sap.engine.services.rmi_p4.exception.P4Logger;
import com.sap.engine.services.rmi_p4.monitor.PingCall;
import com.sap.engine.lib.lang.Convert;

import java.io.ByteArrayInputStream;

/**
 * Client side protocol parser.In every VM may exist only
 * one object of that class.
 * 
 * @author Ivo Simeonov, Georgy Stanev
 * @version 7.0
 */
public class Parser {

  private static byte[] ping_reply = new byte[]{0, 0, // protocol version
                                         16, 0, 0, 0, // size
                                         0, 0, 0, 0, // serverId
                                         0, 0, 0, 0, // senderId
                                         0, 0, 0, 0, 0, 0, 0, 0, // call id
                                         5, // message type "ping connection server"
                                         0, 0, 0, 0, 0, 0, 0 // empty 7 bytes
  };


  public P4ObjectBroker broker = P4ObjectBroker.getBroker();

  private static DispatchPool pool;

 public static synchronized void init() {
     if (pool == null) {
         pool = new DispatchPool();
     }
 }

  public static synchronized void close() {
    if (pool != null) {
       pool.stopWork();
       pool = null;
    }
  }

  public static synchronized void disposeConnection(ClientConnection con, Exception ex) {
    P4Call call;
    Object[] calls = P4Call.getAllCalls();
    for (int i = 0; i < calls.length; i++) {
      call = (P4Call) calls[i];
        if (call.isFrom(con)) {
          call.setException(ex);
        }
    }
    try {
      P4ObjectBroker.getBroker().disposeConnection(con);
    } catch (NullPointerException npe) { //$JL-EXC$
       //ignore - the broker was closed
    }
  }

  /**
   * Creates a new header for the specified request
   *
   * @param server_id the server to witch is the header
   * @param reply     the message for witch is the header
   * @return byte array presentation  of the created
   *         header
   */
  public static byte[] getHeader(int server_id, byte[] reply) {
    ProtocolHeader header = new ProtocolHeader();
    header.destination_server_id = server_id;
    header.size = (reply != null) ? reply.length : 0;
    return header.toByteArray();
  }

  /**
   * Initializes connection buffer size
   *
   * @param con a connection
   */
  public static void init(ClientConnection con) {
    con.request = con.headerBuffer;
  }

  /**
   * creates a new header for close request
   *
   * @return byte array presentation  of the created
   *         header
   */
  public static byte[] getCloseRequest() {
    ProtocolHeader header = new ProtocolHeader();
    header.size = 0;
    return header.toByteArray();
  }

  /**
   * receives request from connection and creates
   * object for dispatching it
   *
   * @param con the connection from witch the request was received
   */
  public static void newRequest(ClientConnection con) throws P4ParseRequestException {
    if (con.newSession) {
      ProtocolHeader header = new ProtocolHeader();
      header.loadFromByteArray(con.request);
      if (header.size <= 0) {
        if (P4Logger.getLocation().beError()) {
          P4Logger.trace(P4Logger.ERROR, "Parser.newRequest(ClientConnection)", "Invalid message header size was detected. Header size: {0}", "ASJ.rmip4.rt2027", new Object[]{header.size});
        }
        throw (P4ParseRequestException) P4ObjectBroker.getBroker().getException(P4ObjectBroker.P4_ParseRequestException, P4BaseParseRequestException.Invalid_messgae_header_size, null, new Object[]{new Integer(header.size)});       //header.size
      } 
      if (header.size > 100*1024*1024) { // message more than 100 MB --> might cause OutOfMemoryError --> Is it corrupted header, or it is really so large? 
        if (P4Logger.getLocation().beWarning()) {
          P4Logger.trace(P4Logger.WARNING, "Parser.newRequest(ClientConnection)", "Too large size of RMI-P4 message was detected. Message header might be corrupted. " +
          		"\r\n Message body size: {0} sender ID: {1}, message header bytes: {2}", "ASJ.rmip4.rt2040", new Object[]{header.size, header.sender_server_id, Message.toString(con.request, 0, ProtocolHeader.HEADER_SIZE)});
        }
      }
      if (P4Logger.dumpMessages() && P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("Parser.newRequest(ClientConnection)", "Message header:\r\n"+ Message.toString(con.request, 0, con.request.length));
      }
      con.request = new byte[header.size]; //Here it may throw OutOfMemoryError
      con.newSession = false;
      con.clusterElementId = header.sender_server_id; //TODO this is only used in the constructor below :(
    } else {
      Message msg = new Message(con.clusterElementId, -1, con.request, con);
      if (P4Logger.getLocation().bePath()) {
        P4Logger.getLocation().pathT("Parser.newRequest(ClientConnection)", "Received message type: " + msg.getType() + " call id: " + msg.getCallId() + " size:" + msg.getSize() + " received from: " + con);
        if (P4Logger.dumpMessages() && P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("Message:\r\n"+ msg.toString());
        }
      }
      if (msg.type == Message.DISPATCH_REPLY) {
        P4Call call = P4Call.getCall(msg.getCallId());
        if (call != null) {
          call.setReply(msg);
        }
      } else if (msg.type == Message.DISPATCH_ERROR_REPLY) {
        P4Call call = P4Call.getCall(msg.getCallId());
        if (call != null) {
            call.setException(msg);
        }
      } else if (msg.type == Message.SPECIAL_ERROR_MESSAGE) {
        P4Call call = P4Call.getCall(msg.getCallId());
        ByteArrayInputStream bis = msg.getByteArrayInputStream();
        byte[] errorCodeByteArr = new byte[4];
        bis.read(errorCodeByteArr, 0, 4);
        if (call != null) {
          if (P4Logger.getLocation().bePath()) {
            P4Logger.getLocation().pathT("Parser.newRequest(ClientConnection)", "The possible problem is that there is no available working correctly server node. Check your working servers. Error message: " + msg.getSpecialErrorMessage(Convert.byteArrToInt(errorCodeByteArr, 0)));
          }
          call.setException(P4ObjectBroker.getBroker().getException(P4ObjectBroker.P4_ConnectionException, "Error. Check your available working servers. Error message: " + msg.getSpecialErrorMessage(Convert.byteArrToInt(errorCodeByteArr, 0)), null));
        }
      } else if (msg.type == Message.PING_CONNECTION_MESSAGE) {
        PingCall call = PingCall.getCall(msg.getCallId());
        if (call != null) {
          call.set(msg);
        }
      } else if (msg.type == Message.PING_CONNECTION_MESSAGE_SERVER) {
       // Convert.writeLongToByteArr(ping_reply, ProtocolHeader.CALL_ID, msg.getCallId());
        ProtocolHeader.writeHeader(ping_reply, 0, ping_reply.length, msg.sender_id);
        try {
           con.sendReply(ping_reply, ping_reply.length, msg.call_id);
          //con.reply(msg.clusterEl_id, 0, ping_reply, ping_reply.length);
        } catch (Exception e) {
          if (P4Logger.getLocation().bePath()) {
            P4Logger.getLocation().pathT("Parser.newRequest(ClientConnection)", P4Logger.exceptionTrace(e));
          }
        }
      } else if (msg.type == Message.RESOLVE_INITIAL_REFERENCE_REPLY) {
        P4Call call = P4Call.getCall(msg.getCallId());
        if (call != null) {
          call.setReply(msg);
        }
      } else {
        DispatchImpl disp = pool.getDispatch();
        if (disp != null) {
          disp.setData(msg, P4ObjectBroker.getBroker(), con);
        } else {
          if (P4Logger.getLocation().beDebug()) {
            P4Logger.getLocation().debugT("Parser.newRequest(ClientConnection)", "Pool is empty. Starting new DispatchImpl thread");
          }
          disp = new DispatchImpl(msg, P4ObjectBroker.getBroker(), con);
          disp.setPool(pool);
          (new Thread(disp)).start();
        }
      }

      con.request = con.headerBuffer;
      con.newSession = true;
    }
  }

}